/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.08.20 16:19
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
import com.mmdev.me.driver.domain.fuel.FuelProviderUI
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.toUI
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFragment
import com.mmdev.me.driver.presentation.ui.common.BaseAdapter
import com.mmdev.me.driver.presentation.ui.common.custom.decorators.LinearItemDecoration
import com.mmdev.me.driver.presentation.ui.fuel.FuelViewModel.FuelViewState
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 *
 */

class FuelFragment : BaseFragment<FuelViewModel, FragmentFuelBinding>(
		layoutId = R.layout.fragment_fuel
) {

	override val mViewModel: FuelViewModel by viewModel()

	private val mFuelProvidersAdapter = FuelProvidersAdapter()
	
	override fun setupViews() {
		binding.rvFuelProviders.apply {
			adapter = mFuelProvidersAdapter
			layoutManager = LinearLayoutManager(requireContext())
			addItemDecoration(LinearItemDecoration())
		}
		
		//mViewModel.getFuelInfo("2020-08-03")
	
		mViewModel.fuelInfo.observe(this, Observer {
			renderState(it)
		})
	}
	
	override fun renderState(state: ViewState) {
		when (state) {
			is FuelViewState.Success -> {
				logWtf(message = "${state.data}")
				mFuelProvidersAdapter.setNewData(
						(state.data[FuelType.A100] ?: error("")).result.fuelProviders.map { it.toUI() }
				)
				sharedViewModel.showLoading.value = false

			}
			is FuelViewState.Loading -> {
				logWtf(message = "loading")
				sharedViewModel.showLoading.value = true
			}
			is FuelViewState.Error -> {
				sharedViewModel.showLoading.value = false
				logWtf(message = state.errorMessage)
			}
		}
		
	}

	private class FuelProvidersAdapter (data: List<FuelProviderUI> = emptyList(),
	                                    @LayoutRes layoutId: Int = R.layout.item_fuel_provider) :
			BaseAdapter<FuelProviderUI>(data, layoutId)
	
}