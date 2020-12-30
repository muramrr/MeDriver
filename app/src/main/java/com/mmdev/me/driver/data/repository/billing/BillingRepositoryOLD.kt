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
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.*
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.mmdev.me.driver.core.utils.MyDispatchers
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.core.mappers.mapList
import com.mmdev.me.driver.data.datasource.user.remote.IUserRemoteDataSource
import com.mmdev.me.driver.data.repository.billing.data.PurchaseDto
import com.mmdev.me.driver.data.repository.billing.data.SkuDto
import com.mmdev.me.driver.domain.billing.BillingResponse
import com.mmdev.me.driver.domain.billing.SubscriptionType
import com.mmdev.me.driver.presentation.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Originally based on https://github.com/android/play-billing-samples/blob/master/ClassyTaxiAppKotlin/app/src/main/java/com/example/subscriptions/billing/BillingClientLifecycle.kt
 * but added coroutines and flow
 */

@Deprecated("Migrated to third-party purchases sdk")
class BillingRepositoryOLD(
	context: Context,
	private val userRemoteDataSource: IUserRemoteDataSource
): BaseRepository(), CoroutineScope, BillingClientStateListener, PurchasesUpdatedListener {
	
	override val coroutineContext: CoroutineContext
		get() = MyDispatchers.io()
	
	private companion object {
		private const val TAG = "mylogs_Billing"
		
		private const val PREMIUM_SKU = "premium_3_month"
		private const val PRO_SKU = "pro_3_months"
		
		private const val PURCHASES_FIELD = "purchases"
	}
	
	private val skuListIdentifiers = listOf(PREMIUM_SKU, PRO_SKU)
	
	
	/** This list will be updated when the Billing Library detects new or existing purchases */
	private val purchases: MutableStateFlow<List<Purchase>> = MutableStateFlow(emptyList())
	
	fun getPermissionsFlow() = purchases.asStateFlow()
	
	/** SkuDetails for all known SKUs.*/
	private var skuListWithDetails: List<SkuDetails> = emptyList()
	
	
	/** Instantiate a new BillingClient instance.*/
	private val billingClient: BillingClient = BillingClient.newBuilder(context)
		.enablePendingPurchases()
		/** Called by the Billing Library when new purchases are detected. */
		.setListener(this)
		.build()
	
	init {
		logDebug(TAG, "Initialized")
		billingClient.startConnection(this@BillingRepositoryOLD)
		
	}
	
	override fun onBillingSetupFinished(result: BillingResult) {
		
		val responseCode = result.responseCode
		val debugMessage = result.debugMessage
		
		if (responseCode == BillingResponseCode.OK) {
			querySkuDetails()
			processPurchases(getPurchases().purchasesList ?: emptyList())
		}
		
		logDebug(
			TAG, "onBillingSetupFinished: response ${BillingResponse.getResponseType(responseCode)}, " +
			     "debug message = $debugMessage"
		)
	}
	
	override fun onBillingServiceDisconnected() {
		logDebug(TAG, "onBillingServiceDisconnected")
	}
	
	override fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
		val responseCode = result.responseCode
		val debugMessage = result.debugMessage
		logDebug(TAG, "onPurchasesUpdated: ${BillingResponse.getResponseType(responseCode)} $debugMessage")
		when (responseCode) {
			BillingResponseCode.OK -> {
				if (purchases.isNullOrEmpty()) logDebug(TAG, "onPurchasesUpdated: null purchase list")
				processPurchases(purchases ?: emptyList())
			}
			BillingResponseCode.USER_CANCELED -> {
				logInfo(TAG, "onPurchasesUpdated: User canceled the purchase")
			}
			BillingResponseCode.DEVELOPER_ERROR -> {
				logError(
					TAG, "onPurchasesUpdated: Developer error means that Google Play " +
					     "does not recognize the configuration. If you are just getting started, " +
					     "make sure you have configured the application correctly in the " +
					     "Google Play Console. The SKU product ID must match and the APK you " +
					     "are using must be signed with release keys."
				)
			}
		}
	}
	
	/** Launching the UI to make a purchase requires a reference to the Activity. */
	fun launchPurchase(activity: Activity, skuPos: Int, accountId: String) {
		
		if (skuListWithDetails.isNotEmpty()) {
			val selectedSku = skuListWithDetails[skuPos]
			
			val purchaseFlowParams =
				BillingFlowParams.newBuilder()
					.setObfuscatedAccountId(accountId)
					.setSkuDetails(selectedSku)
			
			if (!purchases.value.isNullOrEmpty()) {
				val lastPurchase = purchases.value.last()
				val oldSku = lastPurchase.sku
				// setOldSku(previousSku, purchaseTokenOfOriginalSubscription)
				purchaseFlowParams.setOldSku(oldSku, lastPurchase.purchaseToken)
				
				/* https://developer.android.com/google/play/billing/subscriptions#proration-recommendations */
//				purchaseFlowParams.setReplaceSkusProrationMode(
//					if (oldSku == PRO_SKU) ProrationMode.DEFERRED //downgrading
//					else ProrationMode.IMMEDIATE_AND_CHARGE_PRORATED_PRICE //upgrading
//				)
				
				logInfo(TAG, "Launching billing flow for: sku: ${selectedSku.sku}, oldSku: $oldSku")
			}
			
			val purchaseResult =
				billingClient.launchBillingFlow(activity, purchaseFlowParams.build())
			
			logInfo(TAG, "Purchase result = ${BillingResponse.getResponseType(purchaseResult.responseCode)}")
		}
		else return
		
	}
	
	private fun getPurchases() = billingClient.queryPurchases(SkuType.SUBS)
	
	private fun processPurchases(purchasesList: List<Purchase>) {
		/** basically this update called when [onPurchasesUpdated] invoked */
		MainActivity.currentUser?.email?.let {
			launch {
				userRemoteDataSource.updateFirestoreUserField(
					it,
					PURCHASES_FIELD,
					toPurchaseDto(purchasesList)
				).collect { result ->
					logInfo(TAG, "User purchases update result = $result")
				}
			}
		}
		
		logDebug(TAG, "Processing purchases: ${purchasesList.size} purchase(s)")
		
		purchases.tryEmit(purchasesList)
		
		//acknowledge all unacknowledged purchases
		purchasesList.forEach { purchase ->
			if (!purchase.isAcknowledged) {
				acknowledgePurchase(purchase)
			}
		}
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
				skuListWithDetails = skuList ?: emptyList()
				logInfo(TAG, "SKU Details retrieved count: ${skuListWithDetails.size}")
			}
		}
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
	private fun acknowledgePurchase(purchase: Purchase) {
		val params = AcknowledgePurchaseParams.newBuilder()
			.setPurchaseToken(purchase.purchaseToken)
			.build()
		
		billingClient.acknowledgePurchase(params) { billingResult ->
			logDebug(TAG, "Acknowledge purchases result = ${BillingResponse.getResponseType(billingResult.responseCode)}")
		}
	}
	
	
	private fun toSkuDto(sku: String): SkuDto {
		val identifiers = sku.split("_")
		val type = when (identifiers.first()) {
			"premium" -> SubscriptionType.PREMIUM
			"pro" -> SubscriptionType.PRO
			else -> SubscriptionType.FREE
		}
		val periodDuration = identifiers[1].toInt()
		
		return SkuDto(type, periodDuration)
	}
	
	private fun toPurchaseDto(purchases: List<Purchase>) = mapList(purchases) {
		PurchaseDto(
			accountId = it.accountIdentifiers!!.obfuscatedAccountId!!,
			isAcknowledged = it.isAcknowledged,
			isAutoRenewing = it.isAutoRenewing,
			orderId = it.orderId,
			originalJson = it.originalJson,
			purchaseTime = it.purchaseTime,
			purchaseToken = it.purchaseToken,
			signature = it.signature,
			sku = toSkuDto(it.sku),
			skuOriginal = it.sku
		)
	}
}