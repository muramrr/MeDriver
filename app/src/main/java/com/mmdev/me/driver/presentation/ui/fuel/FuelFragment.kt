/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 18.09.2020 17:32
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
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

/**
 *
 */

internal class FuelFragment :
		BaseFlowFragment<FuelPricesViewModel, FragmentFuelBinding>(R.layout.fragment_fuel) {
	
	override fun setupViews() {
		binding.viewPagerContainer.apply {
			adapter = FuelPagerAdapter(childFragmentManager, lifecycle)
		}
		
		TabLayoutMediator(
			binding.tabLayoutContainer,
			binding.viewPagerContainer
		) { tab: TabLayout.Tab, position: Int ->
			when (position){
				0 -> tab.text = getString(R.string.fg_fuel_tab_prices)
				1 -> tab.text = getString(R.string.fg_fuel_tab_history)
			}
		}.attach()
		
	}
	
	override fun renderState(state: ViewState) {}
	
	private class FuelPagerAdapter (fm: FragmentManager, lifecycle: Lifecycle) :
			FragmentStateAdapter(fm, lifecycle) {
		
		
		override fun createFragment(position: Int): Fragment =
			if (position == 0) FuelPricesFragment()
			else FuelHistoryFragment()
		
		override fun getItemCount(): Int = 2
		
	}
	

}