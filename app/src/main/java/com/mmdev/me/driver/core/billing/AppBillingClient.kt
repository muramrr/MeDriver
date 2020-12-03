/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.12.2020 15:47
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.billing

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.core.utils.log.logWarn

/**
 * Originally based on https://github.com/android/play-billing-samples/blob/master/ClassyTaxiAppKotlin/app/src/main/java/com/example/subscriptions/billing/BillingClientLifecycle.kt
 */

class AppBillingClient(context: Context) {
	
	private companion object {
		private const val TAG = "Medriver_Billing"
		
		private const val PREMIUM_SKU = "3_month_premium"
		private const val PRO_SKU = "pro_3_months"
		
	}
	
	/** The purchase event is observable. Only one oberver will be notified. */
	val purchaseUpdateEvent = MutableLiveData<List<Purchase>>()
	
	/**
	 * Purchases are observable. This list will be updated when the Billing Library
	 * detects new or existing purchases. All observers will be notified.
	 */
	val purchases = MutableLiveData<List<Purchase>>()
	
	/** SkuDetails for all known SKUs.*/
	val skuListWithDetails = MutableLiveData<Map<String, SkuDetails>>()
	
	/** Instantiate a new BillingClient instance.*/
	private val billingClient: BillingClient
	private val billingConnection = object: BillingClientStateListener {
		override fun onBillingSetupFinished(billingResult: BillingResult) {
			val responseCode = billingResult.responseCode
			val debugMessage = billingResult.debugMessage
			logDebug(TAG, "onBillingSetupFinished: response ${BillingResponses.getResponseType(responseCode)}, " +
			              "debug message = $debugMessage")
			if (responseCode == BillingResponseCode.OK) {
				// The billing client is ready. You can query purchases here.
				querySkuDetails()
				queryPurchases()
			}
		}
		override fun onBillingServiceDisconnected() {
			logDebug(TAG, "onBillingServiceDisconnected")
			// Try to restart the connection on the next request to Google Play by calling the
			// startConnection() method.
			// billingClient.startConnection(this)
		}
	}
	
	/** Called by the Billing Library when new purchases are detected. */
	private val purchasesUpdateListener =
		PurchasesUpdatedListener { billingResult, purchases ->
			val responseCode = billingResult.responseCode
			val debugMessage = billingResult.debugMessage
			logDebug(TAG, "onPurchasesUpdated: ${BillingResponses.getResponseType(responseCode)} $debugMessage")
			when (responseCode) {
				BillingResponseCode.OK -> {
					if (purchases == null) logDebug(TAG, "onPurchasesUpdated: null purchase list")
					
					processPurchases(purchases)
					
				}
				BillingResponseCode.USER_CANCELED -> {
					logInfo(TAG, "onPurchasesUpdated: User canceled the purchase")
				}
				BillingResponseCode.ITEM_ALREADY_OWNED -> {
					logInfo(TAG, "onPurchasesUpdated: The user already owns this item")
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
		billingClient = BillingClient.newBuilder(context)
			.enablePendingPurchases()
			.setListener(purchasesUpdateListener)
			.build()
		logDebug(TAG, "BillingClient: Start connection...")
		billingClient.startConnection(billingConnection)
	}
	
	
	/** In order to make purchases, you need the [SkuDetails] for the item or subscription.*/
	fun querySkuDetails() {
		val params = SkuDetailsParams.newBuilder()
			.setType(BillingClient.SkuType.SUBS)
			.setSkusList(listOf(PREMIUM_SKU, PRO_SKU))
			.build()
		logInfo(TAG, "querying SKU details async...")
		/**
		 * Receives the result from [querySkuDetails].
		 *
		 * Store the SkuDetails and post them in the [skuListWithDetails]. This allows other parts
		 * of the app to use the [SkuDetails] to show SKU information and make purchases.
		 */
		billingClient.querySkuDetailsAsync(params) { billingResult, skuList ->
			val responseCode = billingResult.responseCode
			val debugMessage = billingResult.debugMessage
			logInfo(TAG, "SKU Details Response: ${BillingResponses.getResponseType(responseCode)}, $debugMessage")
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
	
	/**
	 * Query Google Play Billing for existing purchases.
	 *
	 * New purchases will be provided to the PurchasesUpdatedListener.
	 * You still need to check the Google Play Billing API to know when purchase tokens are removed.
	 */
	fun queryPurchases() {
		if (!billingClient.isReady) {
			logError(TAG, "Querying purchases: BillingClient is not ready")
		}
		logDebug(TAG, "Querying purchases: SUBS")
		val result = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
		if (result.purchasesList.isNullOrEmpty()) logWarn(TAG, "Purchases list is null")
		
		processPurchases(result.purchasesList)
	}
	
	
	/**
	 * Send purchase SingleLiveEvent and update purchases LiveData.
	 *
	 * The SingleLiveEvent will trigger network call to verify the subscriptions on the sever.
	 * The LiveData will allow Google Play settings UI to update based on the latest purchase data.
	 */
	private fun processPurchases(purchasesList: List<Purchase>?) {
		logDebug(TAG, "Processing purchases: ${purchasesList?.size} purchase(s)")
		if (isUnchangedPurchaseList(purchasesList)) {
			logDebug(TAG, "Processing purchases: Purchase list has not changed")
			return
		}
		purchaseUpdateEvent.postValue(purchasesList)
		purchases.postValue(purchasesList)
		purchasesList?.let {
			logAcknowledgementStatus(purchasesList)
		}
	}
	
	/** Check whether the purchases have changed before posting changes.*/
	private fun isUnchangedPurchaseList(purchasesList: List<Purchase>?): Boolean {
		// TODO: Optimize to avoid updates with identical data.
		return false
	}
	
	/**
	 * Log the number of purchases that are acknowledge and not acknowledged.
	 *
	 * https://developer.android.com/google/play/billing/billing_library_releases_notes#2_0_acknowledge
	 *
	 * When the purchase is first received, it will not be acknowledge.
	 * This application sends the purchase token to the server for registration. After the
	 * purchase token is registered to an account, the Android app acknowledges the purchase token.
	 * The next time the purchase list is updated, it will contain acknowledged purchases.
	 */
	private fun logAcknowledgementStatus(purchasesList: List<Purchase>) {
		var ack_yes = 0
		var ack_no = 0
		for (purchase in purchasesList) {
			if (purchase.isAcknowledged) {
				ack_yes++
			} else {
				ack_no++
			}
		}
		logDebug(TAG, "logAcknowledgementStatus: acknowledged=$ack_yes unacknowledged=$ack_no")
	}
	
	/**
	 * Launching the UI to make a purchase requires a reference to the Activity.
	 */
	fun launchBillingFlow(activity: Activity, params: BillingFlowParams): Int {
		val sku = params.sku
		val oldSku = params.oldSku
		logInfo(TAG, "Launching billing flow for: sku: $sku, oldSku: $oldSku")
		if (!billingClient.isReady) {
			logError(TAG, "Launching billing flow: BillingClient is not ready")
		}
		val billingResult = billingClient.launchBillingFlow(activity, params)
		val responseCode = billingResult.responseCode
		val debugMessage = billingResult.debugMessage
		logDebug(TAG, "Launching billing flow: BillingResponse ${BillingResponses.getResponseType(responseCode)},  $debugMessage")
		return responseCode
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
	 * Developers can choose to acknowledge purchases from a server using the
	 * Google Play Developer API. The server has direct access to the user database,
	 * so using the Google Play Developer API for acknowledgement might be more reliable.
	 * TODO(134506821): Acknowledge purchases on the server.
	 *
	 * If the purchase token is not acknowledged within 3 days,
	 * then Google Play will automatically refund and revoke the purchase.
	 * This behavior helps ensure that users are not charged for subscriptions unless the
	 * user has successfully received access to the content.
	 * This eliminates a category of issues where users complain to developers
	 * that they paid for something that the app is not giving to them.
	 */
	fun acknowledgePurchase(purchaseToken: String) {
		logDebug(TAG, "acknowledgePurchase")
		val params = AcknowledgePurchaseParams.newBuilder()
			.setPurchaseToken(purchaseToken)
			.build()
		billingClient.acknowledgePurchase(params) { billingResult ->
			val responseCode = billingResult.responseCode
			val debugMessage = billingResult.debugMessage
			logDebug(TAG, "acknowledgePurchase: response ${BillingResponses.getResponseType(responseCode)}, $debugMessage")
		}
	}
	
}