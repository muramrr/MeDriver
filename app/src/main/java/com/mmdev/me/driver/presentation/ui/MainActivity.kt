/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.08.20 15:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.ActivityMainBinding
import com.mmdev.me.driver.presentation.ui.common.LoadingState
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.concurrent.schedule

class MainActivity: AppCompatActivity() {
	
	private val sharedViewModel: SharedViewModel by viewModel()
	
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
					R.id.bottomNavMainHome -> {
						navController.popBackStack()
						navController.navigate(R.id.bottomNavMainHome)
					}
					R.id.bottomNavMainCare -> {
						navController.popBackStack()
						navController.navigate(R.id.bottomNavMainCare)
					}
					R.id.bottomNavMainMyCar -> {
						navController.popBackStack()
						navController.navigate(R.id.bottomNavMainMyCar)

					}
					R.id.bottomNavMainFuel -> {
						navController.popBackStack()
						navController.navigate(R.id.bottomNavMainFuel)
					}
					R.id.bottomNavMainSettings -> {
						navController.popBackStack()
						navController.navigate(R.id.bottomNavMainSettings)
					}
				}
			}

			return@setOnNavigationItemSelectedListener true
		}

		val loadingDialog = setLoadingDialog(this@MainActivity)

		sharedViewModel.showLoading.observe(this, Observer {
			if (it == LoadingState.SHOW) loadingDialog.show()
			else Timer("Hide loading", false).schedule(700) {
				loadingDialog.dismiss()
			}
		})
	}
	
	private fun setLoadingDialog(context: Context): Dialog {
		val dialog = Dialog(context)
		
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
		dialog.setCancelable(false)
		dialog.setContentView(R.layout.dialog_loading)
		dialog.window?.apply {
			setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
			setDimAmount(0.9f)
		}
		return dialog
	}
}