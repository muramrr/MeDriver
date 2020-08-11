/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.08.20 15:50
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.settings

import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.FragmentSettingsBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import com.mmdev.me.driver.presentation.utils.ThemeHelper
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *
 */

class SettingsFragment: BaseFlowFragment<SettingsViewModel, FragmentSettingsBinding>(
		layoutId = R.layout.fragment_settings
) {

	override val mViewModel: SettingsViewModel by viewModel()

	override fun setupViews() {
		binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
			if (isChecked) ThemeHelper.applyTheme(ThemeHelper.ThemeMode.DARK_MODE)
			else ThemeHelper.applyTheme(ThemeHelper.ThemeMode.LIGHT_MODE)
		}
	}

	override fun renderState(state: ViewState) {

	}
}