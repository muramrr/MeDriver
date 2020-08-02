/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.08.20 16:48
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel

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