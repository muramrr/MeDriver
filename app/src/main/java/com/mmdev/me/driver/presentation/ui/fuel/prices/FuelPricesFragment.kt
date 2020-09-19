/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.09.2020 04:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.prices

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.databinding.FragmentFuelPricesBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFragment
import com.mmdev.me.driver.presentation.ui.common.custom.decorators.LinearItemDecoration
import com.mmdev.me.driver.presentation.utils.showSnack
import org.koin.androidx.viewmodel.ext.android.sharedViewModel



class FuelPricesFragment : BaseFragment<FuelPricesViewModel, FragmentFuelPricesBinding>(
	R.layout.fragment_fuel_prices
) {

	override val mViewModel: FuelPricesViewModel by sharedViewModel()

	private val mPricesAdapter = FuelPricesAdapter()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel.getFuelPrices()
		
		mViewModel.fuelPricesState.observe(this, {
			renderState(it)
		})
	}
	
	override fun setupViews() {
		binding.rvFuelStations.apply {
			adapter = mPricesAdapter
			layoutManager = LinearLayoutManager(requireContext())
			addItemDecoration(LinearItemDecoration())
		}
	}
	
	override fun renderState(state: ViewState) {
		super.renderState(state)
		when (state) {
			is FuelPricesViewState.Success -> {
				logInfo(TAG,"Loaded FuelStations: ${state.data.size}")
				mPricesAdapter.setNewData(state.data)
				if (state.data.isNullOrEmpty())
					binding.root.showSnack(getString(R.string.fg_fuel_prices_empty_list))
			}
			is FuelPricesViewState.Error -> {
				logError(TAG, state.errorMessage)
			}
		}
		
	}
	
}