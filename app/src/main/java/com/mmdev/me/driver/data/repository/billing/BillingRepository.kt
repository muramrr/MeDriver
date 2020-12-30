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
import android.app.Application
import com.android.billingclient.api.BillingFlowParams.ProrationMode
import com.android.billingclient.api.BillingFlowParams.ProrationMode.IMMEDIATE_WITHOUT_PRORATION
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.domain.billing.IBillingRepository
import com.mmdev.me.driver.domain.user.UserDataInfo
import com.mmdev.me.driver.presentation.ui.MainActivity
import com.revenuecat.purchases.EntitlementInfo
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PeriodType.TRIAL
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.PurchasesErrorCode.PurchaseNotAllowedError
import com.revenuecat.purchases.UpgradeInfo
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.getPurchaserInfoWith
import com.revenuecat.purchases.identifyWith
import com.revenuecat.purchases.purchaseProductWith
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 *
 */

class BillingRepository(app: Application): IBillingRepository {
	
	private companion object {
		private const val PROJECT_KEY = "SgtCVjFwRHKOtBRbhELBHxciRMpTsgcP"
		private const val TAG = "mylogs_Billing"
		
		private const val PREMIUM_SKU = "premium_annual"
		private const val PRO_SKU = "pro_annual"
		
	}
	
	private lateinit var packages: List<Package>
	
	private val purchasesMutable: MutableStateFlow<List<EntitlementInfo>> = MutableStateFlow(emptyList())
	override fun getPurchasesFlow(): StateFlow<List<EntitlementInfo>> = purchasesMutable.asStateFlow()
	
	private val purchaseError: MutableStateFlow<PurchasesError?> = MutableStateFlow(null)
	override fun getPurchaseError(): StateFlow<PurchasesError?> = purchaseError.asStateFlow()
	
	init {
		Purchases.debugLogsEnabled = MedriverApp.debug.isEnabled
		Purchases.configure(app, PROJECT_KEY, MainActivity.currentUser?.id)
		getProducts()
		getPurchases()
	}
	
	private fun getProducts() = Purchases.sharedInstance.getOfferingsWith(
		onError = { error -> purchaseError.tryEmit(error) },
		onSuccess = { offerings ->
			packages = offerings.current!!.availablePackages.sortedBy { it.identifier }
		})
	
	private fun getPurchases() = Purchases.sharedInstance.getPurchaserInfoWith(
		onError = { error -> purchaseError.tryEmit(error) },
		onSuccess = { purchaserInfo ->
			purchasesMutable.tryEmit(purchaserInfo.entitlements.active.values.toList())
		}
	)
	
	
	override fun launchPurchase(activity: Activity, skuPos: Int, accountId: String) {
		Purchases.isBillingSupported(activity) { isSupported ->
			if (isSupported) {
				val oldProduct = purchasesMutable.value.find { it.isActive }
				if (oldProduct != null) {
					val identifier = oldProduct.productIdentifier
					val isTrial = oldProduct.periodType == TRIAL
					logInfo(TAG, "Launching update purchase for old: $oldProduct")
					
					/* https://developer.android.com/google/play/billing/subscriptions#proration-recommendations */
					Purchases.sharedInstance.purchaseProductWith(
						activity = activity,
						skuDetails = packages[skuPos].product,
						upgradeInfo = UpgradeInfo(
							identifier,
							when (identifier) {
								/* downgrading */
								PRO_SKU -> if (isTrial) IMMEDIATE_WITHOUT_PRORATION
								else ProrationMode.DEFERRED
								/* upgrading */
								PREMIUM_SKU -> if (isTrial) IMMEDIATE_WITHOUT_PRORATION
								else ProrationMode.IMMEDIATE_AND_CHARGE_PRORATED_PRICE
								else -> IMMEDIATE_WITHOUT_PRORATION
							}
						),
						onError = { error, userCancelled -> purchaseError.tryEmit(error) },
						onSuccess = { product, purchaserInfo ->
							purchasesMutable.tryEmit(purchaserInfo.entitlements.active.values.toList())
						}
					)
				}
				else Purchases.sharedInstance.purchaseProductWith(
					activity = activity,
					skuDetails = packages[skuPos].product,
					onError = { error, userCancelled -> purchaseError.tryEmit(error) },
					onSuccess = { product, purchaserInfo ->
						purchasesMutable.tryEmit(purchaserInfo.entitlements.active.values.toList())
					}
				)
			}
			else {
				purchaseError.tryEmit(PurchasesError(PurchaseNotAllowedError))
				return@isBillingSupported
			}
		}
		
	}
	
	override fun setUser(userDataInfo: UserDataInfo?) = if (userDataInfo == null) {
		Purchases.sharedInstance.reset()
	}
	else {
		// Later log in provided user Id
		Purchases.sharedInstance.identifyWith(
			userDataInfo.id,
			onError = { error -> purchaseError.tryEmit(error) },
			onSuccess = { purchaserInfo ->
				Purchases.sharedInstance.setEmail(userDataInfo.email)
				purchasesMutable.tryEmit(purchaserInfo.entitlements.active.values.toList())
			}
		)
	}
	
	
}