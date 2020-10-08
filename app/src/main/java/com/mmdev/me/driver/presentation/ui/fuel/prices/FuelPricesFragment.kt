/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.10.2020 19:24
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.prices

import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.FragmentFuelPricesBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFragment
import com.mmdev.me.driver.presentation.ui.common.custom.decorators.LinearItemDecoration
import com.mmdev.me.driver.presentation.utils.extensions.showSnack
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class FuelPricesFragment : BaseFragment<FuelPricesViewModel, FragmentFuelPricesBinding>(
	R.layout.fragment_fuel_prices
) {

	override val mViewModel: FuelPricesViewModel by sharedViewModel()

	private val mPricesAdapter = FuelPricesAdapter()
	
	override fun setupViews() {
		mViewModel.fuelPricesState.observe(this, {
			renderState(it)
		})
		
		binding.rvFuelStations.apply {
			setHasFixedSize(true)
			adapter = mPricesAdapter
			layoutManager = LinearLayoutManager(requireContext())
			addItemDecoration(LinearItemDecoration())
		}
	}
	
	override fun renderState(state: ViewState) {
		super.renderState(state)
		when (state) {
			is FuelPricesViewState.Success -> {
				if (state.data.isNullOrEmpty())
					binding.root.showSnack(R.string.fg_fuel_prices_empty_list, Snackbar.LENGTH_LONG)
				else mPricesAdapter.setNewData(state.data)
			}
			is FuelPricesViewState.Error -> {
				binding.root.showSnack(R.string.fg_fuel_prices_empty_list, Snackbar.LENGTH_LONG)
			}
		}
		
	}
	
}