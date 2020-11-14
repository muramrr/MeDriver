/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 14.11.2020 17:41
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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