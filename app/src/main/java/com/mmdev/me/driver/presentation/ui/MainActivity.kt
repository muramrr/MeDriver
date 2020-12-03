/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.12.2020 18:48
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.android.billingclient.api.BillingFlowParams
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.sync.UploadWorker
import com.mmdev.me.driver.core.utils.ConnectionManager
import com.mmdev.me.driver.core.utils.helpers.LocaleHelper
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.core.utils.log.logWtf
import com.mmdev.me.driver.databinding.ActivityMainBinding
import com.mmdev.me.driver.domain.user.UserDataInfo
import com.mmdev.me.driver.domain.user.auth.AuthStatus.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity: AppCompatActivity() {
	
	companion object {
		const val USER_KEY = "USER_KEY"
	}
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	
	private val sharedViewModel: SharedViewModel by viewModel()
	private var _binding: ActivityMainBinding? = null
	
	private val binding: ActivityMainBinding
		get() = _binding ?: throw IllegalStateException(
			"Trying to access the binding outside of the view lifecycle."
		)
	
	//used to force chosen language as base context
	override fun attachBaseContext(base: Context) {
		super.attachBaseContext(LocaleHelper.newLocaleContext(base, MedriverApp.appLanguage))
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {

		window.apply {
			addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
				setDecorFitsSystemWindows(false)
			}
			else {
				decorView.systemUiVisibility =
					View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				//status bar and navigation bar colors assigned in style file
			}
		}

		super.onCreate(savedInstanceState)
		_binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		setupBottomNavigation()
		
		observeVehicle()
		observeUserData()
		
		
		setupNetworkListener()
		
		MedriverApp.appBillingClient.skuListWithDetails.observe(this, {
			logInfo(TAG, "sku = $it")
		})
		
		MedriverApp.appBillingClient.purchaseUpdateEvent.observe(this, {
			logInfo(TAG, "purchase event = $it")
		})
		
		MedriverApp.appBillingClient.purchases.observe(this, {
			logInfo(TAG, "purchases = $it")
		})
	}
	
	private fun setupBottomNavigation() {
		val navController = findNavController(R.id.navHostMain)
		binding.bottomNavMain.setOnNavigationItemSelectedListener {
			val previousItem = binding.bottomNavMain.selectedItemId
			val nextItem = it.itemId
			
			if (previousItem != nextItem) {
				
				when (nextItem) {
					R.id.bottomNavHome -> {
						navController.popBackStack()
						navController.navigate(R.id.actionBottomNavHome)
					}
					R.id.bottomNavMaintenance -> {
						navController.popBackStack()
						navController.navigate(R.id.actionBottomNavMaintenance)
					}
					R.id.bottomNavVehicle -> {
						navController.popBackStack()
						navController.navigate(R.id.actionBottomNavVehicle)
						
					}
					R.id.bottomNavFuel -> {
						navController.popBackStack()
						navController.navigate(R.id.actionBottomNavFuel)
					}
					R.id.bottomNavSettings -> {
						navController.popBackStack()
						navController.navigate(R.id.actionBottomNavSettings)
					}
				}
			}
			
			return@setOnNavigationItemSelectedListener true
		}
	}
	
	fun navigateTo(destination: Int) { binding.bottomNavMain.selectedItemId = destination }
	
	private fun startFetchingWorker(user: UserDataInfo) {
		if (user.isSubscriptionValid()) {
			val constraints = Constraints.Builder()
				.setRequiredNetworkType(NetworkType.CONNECTED)
				.build()
			
			val uploadWorkRequest: WorkRequest =
				OneTimeWorkRequestBuilder<UploadWorker>()
					.setConstraints(constraints)
					.setInputData(workDataOf(USER_KEY to user.email))
					.build()
			
			
			WorkManager
				.getInstance(applicationContext)
				.enqueue(uploadWorkRequest)
		}
		
	}
	
	private fun setupNetworkListener() {
		ConnectionManager(this, this) { isConnected ->
			if (MedriverApp.isNetworkAvailable != isConnected) {
				MedriverApp.isNetworkAvailable = isConnected
				if (MedriverApp.isNetworkAvailable) {
					//todo: start sync worker while internet is available again
					//MedriverApp.currentUser?.let { startFetchingWorker(it) }
				}
			}
			
			logWtf(TAG, "Is network available? -${MedriverApp.isNetworkAvailable}")
		}
	}
	
	private fun observeUserData() {
		sharedViewModel.userDataInfo.observe(this, {
			if (it != null) {
				logDebug(TAG, "authStatus = $AUTHENTICATED")
				startFetchingWorker(it)
				//				Purchases.sharedInstance.identifyWith(it.id) { purchaserInfo ->
				//					//binding.root.showToast("offerings = $purchaserInfo")
				//				}
			}
			else {
				logDebug(TAG, "authStatus = $UNAUTHENTICATED")
				//Purchases.sharedInstance.reset()
			}
			
			MedriverApp.currentUser = it
			
		})
	}
	
	/**
	 * Load saved vehicle from db by vin code which was saved before in sharedPrefs:
	 * - every time we change vehicle from fragment designed for such purposes
	 * we update vin code inside shared prefs and current chosen vehicle;
	 *
	 * - while app starts up -> read saved code and retrieve corresponded vehicle from db.
	 */
	private fun observeVehicle() {
		sharedViewModel.currentVehicle.observe(this, { vehicle ->
			logDebug(TAG, "current vehicle = $vehicle")
			MedriverApp.currentVehicle = vehicle
		})
	}
	
	
	fun launchPurchaseFlow(identifier: String) {
		val flowParams = BillingFlowParams.newBuilder()
			.setObfuscatedAccountId(MedriverApp.currentUser!!.id)
			.setSkuDetails(MedriverApp.appBillingClient.skuListWithDetails.value!![identifier]!!)
			.build()
		MedriverApp.appBillingClient.launchBillingFlow(this, flowParams)

	}
	
	override fun onDestroy() {
		binding.unbind()
		_binding = null
		super.onDestroy()
	}
}