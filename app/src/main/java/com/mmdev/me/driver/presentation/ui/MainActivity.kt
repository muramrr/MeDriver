/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.09.2020 19:12
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
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.helpers.LocaleHelper
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.databinding.ActivityMainBinding
import com.mmdev.me.driver.domain.user.auth.AuthStatus.AUTHENTICATED
import com.mmdev.me.driver.domain.user.auth.AuthStatus.UNAUTHENTICATED
import com.mmdev.me.driver.presentation.ui.common.LoadingDialogFragment
import com.mmdev.me.driver.presentation.ui.common.LoadingStatus
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.concurrent.schedule

class MainActivity: AppCompatActivity() {
	
	private val sharedViewModel: SharedViewModel by viewModel()
	
	private val loadingDialog = LoadingDialogFragment()
	
	//used to force chosen language as base context
	override fun attachBaseContext(base: Context) {
		super.attachBaseContext(LocaleHelper.newLocationContext(base, MedriverApp.appLanguage))
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
		val binding = ActivityMainBinding.inflate(layoutInflater)
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

	

		sharedViewModel.showLoading.observe(this, {
			if (it == LoadingStatus.SHOW) showLoadingDialog()
			else Timer().schedule(1000) { hideLoadingDialog() }
		})
		
		sharedViewModel.userModel.observe(this, {
			if (it != null)
				logDebug("mylogs_MainActivity", "authStatus = $AUTHENTICATED")
			else logDebug("mylogs_MainActivity", "authStatus = $UNAUTHENTICATED")
			
		})
		
	}
	
	private fun hideLoadingDialog() {
		if (supportFragmentManager.findFragmentByTag(LoadingDialogFragment::class.java.canonicalName) != null) {
			supportFragmentManager.beginTransaction().remove(loadingDialog).commitAllowingStateLoss()
		}
		
	}
	
	private fun showLoadingDialog() {
		
		val ft: FragmentTransaction = supportFragmentManager.beginTransaction().apply {
			val prev = supportFragmentManager.findFragmentByTag(
				LoadingDialogFragment::class.java.canonicalName
			)
			prev?.let { remove(it) }
			addToBackStack(null)
		}
		
		loadingDialog.show(ft, LoadingDialogFragment::class.java.canonicalName)
	}
	
}