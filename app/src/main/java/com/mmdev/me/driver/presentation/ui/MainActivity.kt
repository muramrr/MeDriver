/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 29.07.20 20:58
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.ActivityMainBinding

class MainActivity: AppCompatActivity() {
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


	}
}