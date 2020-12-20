/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. medriver
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.me.driver.presentation.ui.fuel.prices

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.mmdev.me.driver.R
import com.mmdev.me.driver.R.string
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.databinding.FragmentFuelPricesBinding
import com.mmdev.me.driver.domain.fuel.prices.data.Region
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFragment
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
				binding.rvFuelStations.smoothScrollToPosition(0)
			}
		}
	}
	
	override fun renderState(state: ViewState) {
		
		when (state) {
			is FuelPricesViewState.Success -> {
				if (state.data.isNullOrEmpty()) showActivitySnack(string.fg_fuel_prices_empty_list)
				else mPricesAdapter.setNewData(state.data)
			}
			is FuelPricesViewState.Error -> showActivitySnack(state.errorMessage ?: getString(
				string.fg_fuel_prices_empty_list))
			
		}
		
	}
	
}