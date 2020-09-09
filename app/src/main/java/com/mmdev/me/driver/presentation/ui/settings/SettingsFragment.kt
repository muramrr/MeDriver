/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.09.2020 20:35
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.settings

import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.MetricSystem.KILOMETERS
import com.mmdev.me.driver.core.utils.MetricSystem.MILES
import com.mmdev.me.driver.databinding.FragmentSettingsBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *
 */

class SettingsFragment: BaseFlowFragment<SettingsViewModel, FragmentSettingsBinding>(
		layoutId = R.layout.fragment_settings
) {

	override val mViewModel: SettingsViewModel by viewModel()

	override fun setupViews() {
		initThemeSwitcher()
		initMetricSystemCheckable()
		
		
	}

	override fun renderState(state: ViewState) {

	}
	
	
	private fun initThemeSwitcher() {
		//init default switcher position
		binding.switchTheme.isChecked(!MedriverApp.isLightMode)
		
		//add callback to switcher toggle
		binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
			mViewModel.setThemeMode(isChecked)
		}
	}
	
	private fun initMetricSystemCheckable() {
		//init default checked button
		when (MedriverApp.metricSystem) {
			KILOMETERS -> binding.radioMetricSystem.check(binding.btnSystemKM.id)
			MILES -> binding.radioMetricSystem.check(binding.btnSystemMI.id)
		}
		
		//add callback to check what button is being checked
		binding.radioMetricSystem.addOnButtonCheckedListener { _, checkedId, isChecked ->
			
			when {
				checkedId == binding.btnSystemKM.id && isChecked -> mViewModel.setMetricSystem(KILOMETERS)
				checkedId == binding.btnSystemMI.id && isChecked-> mViewModel.setMetricSystem(MILES)
			}
		
		}
	}
}