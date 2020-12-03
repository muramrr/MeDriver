/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.12.2020 19:45
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.prices

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.databinding.FragmentFuelPricesBinding
import com.mmdev.me.driver.domain.fuel.prices.data.Region
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFragment
import com.mmdev.me.driver.presentation.utils.extensions.showSnack
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class FuelPricesFragment : BaseFragment<FuelPricesViewModel, FragmentFuelPricesBinding>(
	layoutId = R.layout.fragment_fuel_prices
) {

	override val mViewModel: FuelPricesViewModel by sharedViewModel()

	private val mPricesAdapter = FuelPricesAdapter()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel.viewState.observe(this, {
			renderState(it)
		})
	}
	
	override fun setupViews() {
		setupRegionsDrop()
		
		binding.rvFuelStations.apply {
			setHasFixedSize(true)
			adapter = mPricesAdapter
			layoutManager = LinearLayoutManager(requireContext())
		}
		
		
//		binding.temp2.setOnClickListener {
//			it.showToast("Clicked")
//		}
	}
	
	private fun setupRegionsDrop() {
		val localizedRegions = resources.getStringArray(R.array.fg_fuel_prices_regions)
		val regionsAdapter: ArrayAdapter<String> = ArrayAdapter(
			requireContext(),
			R.layout.item_drop_single_text,
			R.id.tvDropSingleText,
			localizedRegions,
		)
		
		val regions = Region.values()
		
		binding.dropRegions.setText(
			localizedRegions[MedriverApp.pricesRegion.ordinal]
		)
		
		binding.dropRegions.apply {
			setAdapter(regionsAdapter)
			
			setOnItemClickListener { parent, view, position, id ->
				mViewModel.getFuelPrices(regions[position])
			}
		}
	}
	
	override fun renderState(state: ViewState) {
		
		when (state) {
			is FuelPricesViewState.Success -> {
				if (state.data.isNullOrEmpty())
					binding.root.showSnack(R.string.fg_fuel_prices_empty_list, Snackbar.LENGTH_LONG)
				else mPricesAdapter.setNewData(state.data)
			}
			is FuelPricesViewState.Error -> {
				binding.root.showSnack(
					state.errorMessage ?: getString(R.string.fg_fuel_prices_empty_list),
					Snackbar.LENGTH_LONG
				)
			}
		}
		
	}
	
}