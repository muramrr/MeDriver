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

package com.mmdev.me.driver.presentation.ui.fuel

import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.FragmentFuelBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import com.mmdev.me.driver.presentation.ui.fuel.history.FuelHistoryFragment
import com.mmdev.me.driver.presentation.ui.fuel.prices.FuelPricesFragment
import com.mmdev.me.driver.presentation.ui.fuel.prices.FuelPricesViewModel
import com.mmdev.me.driver.presentation.ui.fuel.prices.FuelPricesViewState
import com.mmdev.me.driver.presentation.utils.extensions.visibleIf
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 *
 */

class FuelFragment: BaseFlowFragment<Nothing, FragmentFuelBinding>(
	layoutId = R.layout.fragment_fuel
) {
	
	private val fuelPrices: FuelPricesViewModel by sharedViewModel()
	
	
	override fun setupViews() {
		fuelPrices.viewState.observe(this, { renderState(it) })
		
		binding.viewPagerContainer.apply {
			adapter = FuelPagerAdapter(this@FuelFragment)
		}
		
		TabLayoutMediator(
			binding.tabLayoutContainer,
			binding.viewPagerContainer
		) { tab: TabLayout.Tab, position: Int ->
			when (position){
				0 -> tab.text = getString(R.string.fg_fuel_tab_history)
				1 -> tab.text = getString(R.string.fg_fuel_tab_prices)
			}
		}.attach()
	}
	
	override fun renderState(state: ViewState) {
		binding.viewLoading.visibleIf(otherwise = View.GONE) {
			state == FuelPricesViewState.Loading
		}
	}
	
	private class FuelPagerAdapter (fragment: Fragment): FragmentStateAdapter(fragment) {
		
		
		override fun createFragment(position: Int): Fragment =
			if (position == 0) FuelHistoryFragment()
			else FuelPricesFragment()
		
		override fun getItemCount(): Int = 2
		
	}
	

}