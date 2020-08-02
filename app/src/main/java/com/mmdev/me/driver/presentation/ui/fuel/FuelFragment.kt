/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.08.20 20:44
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel

import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.lifecycle.Observer
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.utils.logWtf
import com.mmdev.me.driver.databinding.FragmentFuelBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFragment
import com.mmdev.me.driver.presentation.ui.fuel.FuelViewModel.FuelViewState
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 *
 */

class FuelFragment : BaseFragment<FuelViewModel, FragmentFuelBinding>(
		layoutId = R.layout.fragment_fuel
) {

	override val viewModel: FuelViewModel by viewModel()
	
	override fun setupViews() {
		
		//viewModel.getFuelInfo("2020-08-02", 3)
		
		binding.btnFuelType100.setOnClickListener {
			it.isSelected = true
			val anim: Animation = ScaleAnimation(1f, 1f,
			                                     1.5f, 1.5f)
			anim.fillAfter = true // Needed to keep the result of the animation
			
			it.startAnimation(anim)
		}
	
		viewModel.fuelInfo.observe(this, Observer {
			renderState(it)
		})
	}
	
	override fun renderState(state: ViewState) {
		when (state) {
			is FuelViewState.Success -> {
				logWtf(message = "${state.data}")
			}
			is FuelViewState.Loading -> {
				logWtf(message = "loading")
				
			}
			is FuelViewState.Error -> {
				logWtf(message = state.errorMessage)
			}
		}
		
	}
	
}