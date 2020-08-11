/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.08.20 15:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel

import androidx.annotation.LayoutRes
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.utils.logWtf
import com.mmdev.me.driver.databinding.FragmentFuelBinding
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.model.FuelPrice
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import com.mmdev.me.driver.presentation.ui.common.BaseAdapter
import com.mmdev.me.driver.presentation.ui.common.LoadingState
import com.mmdev.me.driver.presentation.ui.common.custom.decorators.LinearItemDecoration
import com.mmdev.me.driver.presentation.ui.fuel.FuelViewModel.FuelViewState
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


/**
 *
 */

class FuelFragment : BaseFlowFragment<FuelViewModel, FragmentFuelBinding>(
		layoutId = R.layout.fragment_fuel
) {

	override val mViewModel: FuelViewModel by sharedViewModel()

	private val mFuelProvidersAdapter = FuelProvidersAdapter()
	
	override fun setupViews() {
		binding.rvFuelProviders.apply {
			adapter = mFuelProvidersAdapter
			layoutManager = LinearLayoutManager(requireContext())
			addItemDecoration(LinearItemDecoration())
		}
		
		mViewModel.getFuelInfo(FuelType.A95)
	
		mViewModel.fuelInfo.observe(this, Observer {
			renderState(it)
		})
	}
	
	override fun renderState(state: ViewState) {
		when (state) {
			is FuelViewState.Success -> {
				logWtf(message = "${state.data}")
				mFuelProvidersAdapter.setNewData(state.data)
				sharedViewModel.showLoading.value = LoadingState.HIDE
			}
			is FuelViewState.Loading -> {
				logWtf(message = "loading")
				sharedViewModel.showLoading.value = LoadingState.SHOW
			}
			is FuelViewState.Error -> {
				sharedViewModel.showLoading.value = LoadingState.HIDE
				logWtf(message = state.errorMessage)
			}
		}
		
	}
	

	private class FuelProvidersAdapter (data: List<FuelPrice> = emptyList(),
	                                    @LayoutRes layoutId: Int = R.layout.item_fuel_provider) :
			BaseAdapter<FuelPrice>(data, layoutId)
	
}