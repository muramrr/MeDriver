/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. medriver
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.me.driver.data.repository.billing

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.*
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingFlowParams.ProrationMode
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.mmdev.me.driver.core.utils.MyDispatchers
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.core.utils.log.logWarn
import com.mmdev.me.driver.data.core.firebase.safeOffer
import com.mmdev.me.driver.data.datasource.billing.TimeApi
import com.mmdev.me.driver.data.datasource.user.remote.IUserRemoteDataSource
import com.mmdev.me.driver.domain.billing.BillingResponse
import com.mmdev.me.driver.presentation.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * Originally based on https://github.com/android/play-billing-samples/blob/master/ClassyTaxiAppKotlin/app/src/main/java/com/example/subscriptions/billing/BillingClientLifecycle.kt
 * but added coroutines and flow
 */

class BillingRepository(
	context: Context,
	private val userRemoteDataSource: IUserRemoteDataSource,
	private val timeApi: TimeApi,
	private val mappers: BillingMappers
): CoroutineScope, BillingClientStateListener, PurchasesUpdatedListener {
	
	override val coroutineContext: CoroutineContext
		get() = MyDispatchers.io()
	
	private companion object {
		private const val TAG = "mylogs_Billing"
		
		private const val TIMEOUT_MILLIS = 2000L
		private const val RETRY_MILLIS = 3000L
		
		private const val PREMIUM_SKU = "premium_3_month"
		private const val PRO_SKU = "pro_3_months"
		
		private const val PURCHASES_FIELD = "lastPurchase"
	}
	
	private val skuListIdentifiers = listOf(PREMIUM_SKU, PRO_SKU)
	
	private val billingClientStatus = MutableSharedFlow<Int>(
		replay = 1,
		onBufferOverflow = BufferOverflow.DROP_OLDEST
	)
	
	/** This list will be updated when the Billing Library detects new or existing purchases */
	val purchases = MutableLiveData<List<Purchase>>()
	
	/** SkuDetails for all known SKUs.*/
	private val skuListWithDetails = MutableLiveData<Map<String, SkuDetails>>()
	
	
	/** Instantiate a new BillingClient instance.*/
	private val billingClient: BillingClient = BillingClient.newBuilder(context)
		.enablePendingPurchases()
		/** Called by the Billing Library when new purchases are detected. */
		.setListener(this)
		.build()
	
	override fun onBillingSetupFinished(result: BillingResult) {
		billingClientStatus.tryEmit(result.responseCode)
		
		val responseCode = result.responseCode
		val debugMessage = result.debugMessage
		logDebug(TAG, "onBillingSetupFinished: response ${BillingResponse.getResponseType(responseCode)}, " +
		              "debug message = $debugMessage")
		
		
	}
	
	override fun onBillingServiceDisconnected() {
		logDebug(TAG, "onBillingServiceDisconnected")
		billingClientStatus.tryEmit(BillingResponseCode.SERVICE_DISCONNECTED)
	}
	
	override fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
		val responseCode = result.responseCode
		val debugMessage = result.debugMessage
		logDebug(TAG, "onPurchasesUpdated: ${BillingResponse.getResponseType(responseCode)} $debugMessage")
		when (responseCode) {
			BillingResponseCode.OK -> {
				if (purchases.isNullOrEmpty()) logDebug(TAG, "onPurchasesUpdated: null purchase list")
				else {
					processPurchases(purchases)
					
					launch {
						val lastPurchase = purchases.last()
						acknowledgePurchase(lastPurchase.purchaseToken).collect { billingResult ->
							logInfo(TAG, "Acknowledge purchase result = ${BillingResponse.getResponseType(billingResult.responseCode)}")
							
							if (billingResult.responseCode == BillingResponseCode.OK) {
								
								val purchaseDto = mappers.toPurchaseDto(
									lastPurchase
								).copy(isAcknowledged = true)
								userRemoteDataSource.updateFirestoreUserField(
									MainActivity.currentUser!!.email,
									PURCHASES_FIELD,
									purchaseDto
								).collect { result ->
									logInfo(TAG, "User update result = $result")
								}
							}
						}
					}
				}
				
			}
			BillingResponseCode.USER_CANCELED -> {
				logInfo(TAG, "onPurchasesUpdated: User canceled the purchase")
			}
			BillingResponseCode.DEVELOPER_ERROR -> {
				logError(TAG, "onPurchasesUpdated: Developer error means that Google Play " +
				              "does not recognize the configuration. If you are just getting started, " +
				              "make sure you have configured the application correctly in the " +
				              "Google Play Console. The SKU product ID must match and the APK you " +
				              "are using must be signed with release keys."
				)
			}
		}
	}
	
	init {
		logDebug(TAG, "Initialized")
		billingClient.startConnection(this@BillingRepository)
		launch {
			billingClientStatus.collect {
				when (it) {
					BillingResponseCode.OK -> {
						querySkuDetails()
						getPurchases()
					}
					else -> {
						billingClient.startConnection(this@BillingRepository)
						delay(RETRY_MILLIS)
					}
				}
			}
		}
		
	}
	
	private suspend fun getPurchases() =
		withContext(MyDispatchers.io()) {
			val result = billingClient.queryPurchases(SkuType.SUBS).purchasesList
			processPurchases(result ?: emptyList())
		}
	
	private fun processPurchases(purchasesList: List<Purchase>) {
		logDebug(TAG, "Processing purchases: ${purchasesList.size} purchase(s)")
		purchases.postValue(purchasesList)
	}
	
	/** In order to make purchases, you need the [SkuDetails] for the item or subscription.*/
	private fun querySkuDetails() {
		val params = SkuDetailsParams.newBuilder()
			.setType(SkuType.SUBS)
			.setSkusList(skuListIdentifiers)
			.build()
		logInfo(TAG, "querying SKU details async...")
		
		billingClient.querySkuDetailsAsync(params) { billingResult, skuList ->
			val responseCode = billingResult.responseCode
			val debugMessage = billingResult.debugMessage
			logInfo(TAG, "SKU Details Response: ${BillingResponse.getResponseType(responseCode)}, $debugMessage")
			if (responseCode == BillingResponseCode.OK) {
				if (skuList.isNullOrEmpty()) skuListWithDetails.postValue(emptyMap()).also {
					logWarn(TAG, "SkuDetails list is null")
				}
				else skuListWithDetails.postValue(skuList.map { it.sku to it }.toMap().also {
					logInfo(TAG, "SKU Details retrieved count: ${it.size}")
				})
			}
		}
	}
	
	/** Launching the UI to make a purchase requires a reference to the Activity. */
	fun launchBillingFlow(
		activity: Activity,
		identifier: String,
		accountId: String
	) {
		
		val purchaseFlowParams = BillingFlowParams.newBuilder()
			.setSkuDetails(skuListWithDetails.value!![identifier]!!)
			.setObfuscatedAccountId(accountId)
		
		val sku = skuListWithDetails.value!![identifier]!!.sku
		var oldSku = "null"
		// setOldSku(previousSku, purchaseTokenOfOriginalSubscription)
		if (!purchases.value.isNullOrEmpty()) {
			val lastPurchase = purchases.value!!.last()
			purchaseFlowParams.setOldSku(lastPurchase.sku, lastPurchase.purchaseToken)
			oldSku = lastPurchase.sku
			
			/* https://developer.android.com/google/play/billing/subscriptions#proration-recommendations */
			purchaseFlowParams.setReplaceSkusProrationMode(
				if (oldSku == PRO_SKU) ProrationMode.DEFERRED else ProrationMode.IMMEDIATE_AND_CHARGE_PRORATED_PRICE
			)
		}
		
		logInfo(TAG, "Launching billing flow for: sku: $sku, oldSku: $oldSku")
		
		val billingResult = billingClient.launchBillingFlow(activity, purchaseFlowParams.build())
		val responseCode = billingResult.responseCode
		val debugMessage = billingResult.debugMessage
		
		logDebug(TAG, "Launching billing flow: BillingResponse ${BillingResponse.getResponseType(responseCode)},  $debugMessage")
		
	}
	
	
	/**
	 * Acknowledge a purchase.
	 *
	 * https://developer.android.com/google/play/billing/billing_library_releases_notes#2_0_acknowledge
	 *
	 * Apps should acknowledge the purchase after confirming that the purchase token
	 * has been associated with a user. This app only acknowledges purchases after
	 * successfully receiving the subscription data back from the server.
	 *
	 * If the purchase token is not acknowledged within 3 days,
	 * then Google Play will automatically refund and revoke the purchase.
	 * This behavior helps ensure that users are not charged for subscriptions unless the
	 * user has successfully received access to the content.
	 * This eliminates a category of issues where users complain to developers
	 * that they paid for something that the app is not giving to them.
	 */
	private fun acknowledgePurchase(purchaseToken: String) = callbackFlow {
		logDebug(TAG, "acknowledgePurchase")
		val params = AcknowledgePurchaseParams.newBuilder()
			.setPurchaseToken(purchaseToken)
			.build()
		
		billingClient.acknowledgePurchase(params) { billingResult ->
			val responseCode = billingResult.responseCode
			val debugMessage = billingResult.debugMessage
			logDebug(TAG, "acknowledgePurchase: response ${BillingResponse.getResponseType(responseCode)}, $debugMessage")
			
			safeOffer(billingResult)
			
		}
		awaitClose {
			cancel()
		}
	}
	
	
}