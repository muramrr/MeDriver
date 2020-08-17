/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 17.08.2020 20:45
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.prices

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.utils.logWtf
import com.mmdev.me.driver.databinding.FragmentFuelPricesBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFragment
import com.mmdev.me.driver.presentation.ui.common.LoadingState
import com.mmdev.me.driver.presentation.ui.common.custom.decorators.LinearItemDecoration
import com.mmdev.me.driver.presentation.ui.fuel.prices.FuelPricesViewModel.FuelViewState
import org.koin.androidx.viewmodel.ext.android.sharedViewModel




class FuelFragmentPrices : BaseFragment<FuelPricesViewModel, FragmentFuelPricesBinding>(
	R.layout.fragment_fuel_prices
) {

	override val mViewModel: FuelPricesViewModel by sharedViewModel()

	private val mPricesAdapter = PricesAdapter()
	
	override fun setupViews() {
		binding.rvFuelProviders.apply {
			adapter = mPricesAdapter
			layoutManager = LinearLayoutManager(requireContext())
			addItemDecoration(LinearItemDecoration())
		}
		
		mViewModel.getFuelPrices()
	
		mViewModel.fuelPrices.observe(this, Observer {
			renderState(it)
		})
	}
	
	override fun renderState(state: ViewState) {
		when (state) {
			is FuelViewState.Success -> {
				logWtf(TAG,"${state.data}")
				mPricesAdapter.setNewData(state.data)
				sharedViewModel.showLoading.value = LoadingState.HIDE
			}
			is FuelViewState.Loading -> {
				logWtf(TAG, "loading")
				sharedViewModel.showLoading.value = LoadingState.SHOW
			}
			is FuelViewState.Error -> {
				sharedViewModel.showLoading.value = LoadingState.HIDE
				logWtf(TAG, state.errorMessage)
			}
		}
		
	}
	

	
	
}