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

package com.mmdev.me.driver.presentation.ui

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.Purchase.PurchaseState
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.domain.billing.IBillingRepository
import com.mmdev.me.driver.domain.billing.SubscriptionType
import com.mmdev.me.driver.domain.fetching.IFetchingRepository
import com.mmdev.me.driver.domain.user.AuthStatus
import com.mmdev.me.driver.domain.user.IAuthFlowProvider
import com.mmdev.me.driver.domain.user.UserDataInfo
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.utils.extensions.combineWith
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * [SharedViewModel] used in every fragment in app. Parent is MainActivity
 * Main responsibilities: Handle LOADING dialog appearance
 * Handle user [AuthStatus]
 * [currentVehicle] mutable value shared across all application
 */

class SharedViewModel(
	authProvider: IAuthFlowProvider,
	private val billing: IBillingRepository,
	private val fetcher: IFetchingRepository
) : BaseViewModel() {
	
	companion object {
		var uploadWorkerExecuted = false
	}
	
	private var fetcherJob: Job? = null
	
	val purchases = billing.getPurchasesFlow().asLiveData()
	private val _userDataInfo = authProvider.getUserFlow().asLiveData()
	
	val currentVehicle = MutableLiveData<Vehicle?>()
	init {
		getSavedVehicle(MedriverApp.currentVehicleVinCode)
	}
	
	val userDataInfo = _userDataInfo.combineWith(purchases) { user, purchases ->
		val subscription =
			if (purchases.isNullOrEmpty()) SubscriptionType.FREE
			else parseSku(purchases.last().sku)
		
		val userWithPurchase =
			if (!purchases.isNullOrEmpty()
			    && purchases.last().purchaseState == PurchaseState.PURCHASED
			    && purchases.last().accountIdentifiers!!.obfuscatedAccountId == user?.id)
			user?.copy(subscriptionType = subscription)
		else user
		
		if (userWithPurchase?.isPro() == true) {
			fetcherJob = viewModelScope.launch { fetcher.listenForUpdates(userWithPurchase.email) }
		}
		else fetcherJob?.cancel()
		
		userWithPurchase
	}
	
	fun getSavedVehicle(vin: String) {
		viewModelScope.launch {
			currentVehicle.postValue(fetcher.getSavedVehicle(vin))
		}
	}
	
	/**
	 * Used in different parts of application
	 * For example: when user adds new fuel history entry or some maintenance changes
	 * this function is triggered to update actual info.
	 */
	fun updateVehicle(user: UserDataInfo?, vehicle: Vehicle) {
		logInfo(TAG, "Updating vehicle..")
		viewModelScope.launch {
			fetcher.updateVehicle(user, vehicle).collect { result ->
				result.fold(
					success = { currentVehicle.postValue(vehicle) },
					failure = { logError(TAG, "$it")}
				)
			}
		}
	}
	
	fun launchBillingFlow(activity: Activity, identifier: Int) =
		billing.launchPurchase(activity, identifier, userDataInfo.value!!.id)
	
	
	private fun parseSku(sku: String): SubscriptionType {
		val identifiers = sku.split("_")
		return when (identifiers.first()) {
			"premium" -> SubscriptionType.PREMIUM
			"pro" -> SubscriptionType.PRO
			else -> SubscriptionType.FREE
		}
	}
	
}