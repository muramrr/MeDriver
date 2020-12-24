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
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.core.utils.log.logWtf
import com.mmdev.me.driver.domain.billing.IBillingRepository
import com.qonversion.android.sdk.Qonversion
import com.qonversion.android.sdk.QonversionError
import com.qonversion.android.sdk.QonversionLaunchCallback
import com.qonversion.android.sdk.QonversionPermissionsCallback
import com.qonversion.android.sdk.dto.QLaunchResult
import com.qonversion.android.sdk.dto.QPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 *
 */

class BillingRepository(app: Application): IBillingRepository {
	
	private companion object {
		private const val PROJECT_KEY = "25bLjKW-OkbhzZ9NSSmQgYZnzkqzu2_i"
		private const val TAG = "mylogs_Billing"
		
		private const val PREMIUM_SKU = "premium_3_month"
		private const val PRO_SKU = "pro_3_months"
		
	}
	
	private val productIdentifiers = listOf(PREMIUM_SKU, PRO_SKU)
	
	private val permissionsMutable: MutableStateFlow<List<QPermission>> = MutableStateFlow(emptyList())
	override fun getPermissionsFlow(): StateFlow<List<QPermission>> = permissionsMutable.asStateFlow()
	
	init {
		Qonversion.launch(app, PROJECT_KEY, false, object : QonversionLaunchCallback {
			override fun onSuccess(launchResult: QLaunchResult) {
				logWtf(TAG, "Qonversion launched, result = $launchResult")
				getPermissions()
			}
			
			override fun onError(error: QonversionError) {
				logError(TAG, "Qonversion launch error: $error")
			}
		})
	}
	
	private val permissionsCallback = object: QonversionPermissionsCallback {
		override fun onSuccess(permissions: Map<String, QPermission>) {
			logWtf(TAG, "permissions = $permissions")
			permissionsMutable.tryEmit(permissions.values.toList())
		}
		
		override fun onError(error: QonversionError) {
			logError(TAG, "check permissions error: $error")
		}
	}
	
	private fun getPermissions() = Qonversion.checkPermissions(permissionsCallback)
	
	
	override fun launchPurchase(activity: Activity, skuPos: Int, accountId: String) {
		val oldProduct = permissionsMutable.value.find { it.isActive() }?.productID
		if (!oldProduct.isNullOrBlank()) {
			/* https://developer.android.com/google/play/billing/subscriptions#proration-recommendations */
			//purchaseFlowParams.setReplaceSkusProrationMode(
			//	if (oldSku == PRO_SKU) ProrationMode.DEFERRED //downgrading
			//	else ProrationMode.IMMEDIATE_AND_CHARGE_PRORATED_PRICE //upgrading
			//)
			
			logInfo(TAG, "Launching update purchase for old: $oldProduct")
			Qonversion.updatePurchase(
				context = activity,
				productId = productIdentifiers[skuPos],
				oldProductId = oldProduct,
				callback = permissionsCallback
			)
		}
		else Qonversion.purchase(activity, productIdentifiers[skuPos], permissionsCallback)
	}
	
}