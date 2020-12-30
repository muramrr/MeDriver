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

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.updatePadding
import androidx.navigation.findNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo.State.SUCCEEDED
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.google.android.material.snackbar.Snackbar
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.sync.DownloadWorker
import com.mmdev.me.driver.core.sync.UploadWorker
import com.mmdev.me.driver.core.utils.ConnectionManager
import com.mmdev.me.driver.core.utils.extensions.currentEpochTime
import com.mmdev.me.driver.core.utils.helpers.LocaleHelper
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logWtf
import com.mmdev.me.driver.databinding.ActivityMainBinding
import com.mmdev.me.driver.domain.user.AuthStatus.*
import com.mmdev.me.driver.domain.user.UserDataInfo
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity: AppCompatActivity() {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	companion object {
		private const val USER_KEY = "USER_KEY" // used in upload and download workers
		private const val DATA_IMPORTED_KEY = "IS_DATA_IMPORTED"
		@Volatile
		var currentUser: UserDataInfo? = null
			private set
		
		var currentVehicle: Vehicle? = null
			set(value) {
				field = value
				MedriverApp.currentVehicleVinCode = value?.vin ?: ""
			}
	}
	
	private val sharedViewModel: SharedViewModel by viewModel()
	private var _binding: ActivityMainBinding? = null
	
	private val binding: ActivityMainBinding
		get() = _binding ?: throw IllegalStateException(
			"Trying to access the binding outside of the view lifecycle."
		)
	
	//empty instance for null safety
	private var currentWindowInsets: WindowInsetsCompat = WindowInsetsCompat.Builder().build()
	
	
	//used to force chosen language as base context
	override fun attachBaseContext(base: Context) {
		super.attachBaseContext(LocaleHelper.newLocaleContext(base, MedriverApp.appLanguage))
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		
		WindowCompat.setDecorFitsSystemWindows(window, false)

		super.onCreate(savedInstanceState)
		_binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
			currentWindowInsets = windowInsets
			setupInsets()
		}
		
		
		setupNetworkListener()
		
		setupBottomNavigation()
		
		observeVehicle()
		observeUserData()
		observePurchases()
		observePurchaseError()
		
	}
	
	private fun setupInsets(): WindowInsetsCompat {
		val insetsType = Type.systemBars()
		val insets = currentWindowInsets.getInsets(insetsType)
		
		binding.mainContainer.updatePadding(insets.left, insets.top, insets.right, 0)
		binding.bottomNavMain.updatePadding(insets.left, 0, insets.right, insets.bottom)
		
		return WindowInsetsCompat.Builder()
			.setInsets(insetsType, insets)
			.build()
	}
	
	private fun setupBottomNavigation() {
		val navController = findNavController(R.id.navHostMain)
		//select start destination
		binding.bottomNavMain.selectedItemId = R.id.bottomNavVehicle
		binding.bottomNavMain.setOnNavigationItemSelectedListener {
			val previousItem = binding.bottomNavMain.selectedItemId
			val nextItem = it.itemId
			
			if (previousItem != nextItem) {
				
				when (nextItem) {
					R.id.bottomNavGarage -> {
						navController.popBackStack()
						navController.navigate(R.id.actionBottomNavGarage)
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
	
	private fun startUploadWorker(user: UserDataInfo) {
		if (user.isPro()) {
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
	
	private fun startDownloadWorker(user: UserDataInfo) {
		if (user.isPro()) {
			val constraints = Constraints.Builder()
				.setRequiredNetworkType(NetworkType.CONNECTED)
				.build()
			
			val downloadWorkRequest: WorkRequest =
				OneTimeWorkRequestBuilder<DownloadWorker>()
					.setConstraints(constraints)
					.setInputData(workDataOf(
						USER_KEY to user.email,
						DATA_IMPORTED_KEY to MedriverApp.isDataImported
					))
					.build()
			
			
			WorkManager
				.getInstance(applicationContext)
				.enqueue(downloadWorkRequest)
			
			WorkManager
				.getInstance(applicationContext)
				.getWorkInfoByIdLiveData(downloadWorkRequest.id)
				.observe(this, {
					if (it.state == SUCCEEDED) {
						MedriverApp.lastSyncedDate = currentEpochTime()
						//update vehicle stored in static
						sharedViewModel.getSavedVehicle(MedriverApp.currentVehicleVinCode)
					}
				})
		}
		
	}
	
	
	private fun setupNetworkListener() {
		ConnectionManager(this, this) { isConnected ->
			if (MedriverApp.isNetworkAvailable != isConnected) {
				MedriverApp.isNetworkAvailable = isConnected
				//only on available network start uploading worker
				if (MedriverApp.isNetworkAvailable) {
					//if network available again
					currentUser?.let {
						startDownloadWorker(it)
						startUploadWorker(it)
					}
				}
				
				logDebug(TAG, "Is network available? -${MedriverApp.isNetworkAvailable}")
			}
		}
	}
	
	private fun observeUserData() {
		sharedViewModel.userDataInfo.observe(this, {
			if (it != null) {
				logDebug(TAG, "authStatus = $AUTHENTICATED")
				startDownloadWorker(it)
				startUploadWorker(it)
				MedriverApp.savedUserEmail = it.email
			}
			else {
				logDebug(TAG, "authStatus = $UNAUTHENTICATED")
			}
			currentUser = it
		})
		
	}
	
	private fun observePurchases() = sharedViewModel.purchases.observe(this, {
		logWtf(TAG, "$it")
	})
	
	private fun observePurchaseError() = sharedViewModel.purchasesError.observe(this, {
		if (it != null) Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
	})
	
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
			currentVehicle = vehicle
		})
	}
	
	fun showSnack(message: String) =
		Snackbar
			.make(binding.root, message, Snackbar.LENGTH_LONG)
			.setAction("OK") {}
			.setAnchorView(binding.bottomNavMain)
			.show()
	
	fun launchPurchaseFlow(identifier: Int) = sharedViewModel.launchBillingFlow(
		this,
		identifier
	)
	
	override fun onDestroy() {
		binding.unbind()
		_binding = null
		super.onDestroy()
	}
	
}