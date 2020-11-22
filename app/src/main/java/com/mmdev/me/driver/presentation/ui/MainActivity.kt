/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 00:34
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
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.sync.UploadWorker
import com.mmdev.me.driver.core.utils.ConnectionManager
import com.mmdev.me.driver.core.utils.helpers.LocaleHelper
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logWtf
import com.mmdev.me.driver.databinding.ActivityMainBinding
import com.mmdev.me.driver.domain.user.UserDataInfo
import com.mmdev.me.driver.domain.user.auth.AuthStatus.AUTHENTICATED
import com.mmdev.me.driver.domain.user.auth.AuthStatus.UNAUTHENTICATED
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.identifyWith
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
	
	
	//private var loadingShowingTime: Long = 0
	
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
		
		
//		Timer().schedule(
//			// if loading showing less than 500 millis (half of second) -> delay, else no delay
//			if (currentEpochTime() - loadingShowingTime < 500) 500 else 0
//		) { hideLoadingDialog() }

		
		sharedViewModel.userDataInfo.observe(this, {
			if (it != null) {
				logDebug(TAG, "authStatus = $AUTHENTICATED")
				startFetchingWorker(it)
				Purchases.sharedInstance.identifyWith(it.id) { purchaserInfo ->
					//binding.root.showToast("offerings = $purchaserInfo")
				}
			}
			else {
				logDebug(TAG, "authStatus = $UNAUTHENTICATED")
				Purchases.sharedInstance.reset()
			}
			
			MedriverApp.currentUser = it
			
		})
		
		/**
		 * Load saved vehicle from db by vin code which was saved before in sharedPrefs:
		 * - every time we change vehicle from fragment designed for such purposes
		 * we update vin code inside shared prefs and current chosen vehicle;
		 *
		 * - while app starts up -> read saved code and retrieve corresponded vehicle from db.
		 */
		sharedViewModel.currentVehicle.observe(this, { vehicle ->
			logDebug(TAG, "current vehicle = $vehicle")
			
			MedriverApp.currentVehicle = vehicle
			MedriverApp.changeCurrentVehicleVinCode(vehicle?.vin ?: "")
		
		})
		
		setListeners()
		
		Purchases.sharedInstance.getOfferingsWith(
			onError = { error ->
				//binding.root.showToast("error = ${error.message}")
			},
			onSuccess = { offerings ->
				//binding.root.showToast("offerings = ${offerings.all}")
			}
		)
	}
	
	private fun startFetchingWorker(user: UserDataInfo) {
		if (user.isSyncEnabled && user.isSubscriptionValid()) {
			val constraints = Constraints.Builder()
				.setRequiresBatteryNotLow(true)
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
	
	private fun setListeners() {
		ConnectionManager(MedriverApp.appContext, this) { isConnected ->
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
	
	fun navigateTo(destination: Int) { binding.bottomNavMain.selectedItemId = destination }
	
	override fun onDestroy() {
		binding.unbind()
		_binding = null
		super.onDestroy()
	}
}