/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.07.20 20:38
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.settings

import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.FragmentSettingsBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFragment
import com.mmdev.me.driver.presentation.utils.ThemeHelper

/**
 *
 */

class SettingsFragment: BaseFragment<SettingsViewModel, FragmentSettingsBinding>(
		layoutId = R.layout.fragment_settings
) {

	override val mViewModel: SettingsViewModel = SettingsViewModel()

	override fun setupViews() {
		binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
			if (isChecked) ThemeHelper.applyTheme(ThemeHelper.ThemeMode.DARK_MODE)
			else ThemeHelper.applyTheme(ThemeHelper.ThemeMode.LIGHT_MODE)
		}
	}

	override fun renderState(state: ViewState) {

	}
}