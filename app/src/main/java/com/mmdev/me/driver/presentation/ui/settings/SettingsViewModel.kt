/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.09.2020 20:22
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.settings

import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.MetricSystem
import com.mmdev.me.driver.core.utils.ThemeHelper.ThemeMode.DARK_MODE
import com.mmdev.me.driver.core.utils.ThemeHelper.ThemeMode.LIGHT_MODE
import com.mmdev.me.driver.presentation.core.base.BaseViewModel

/**
 *
 */

class SettingsViewModel: BaseViewModel() {
	
	fun setThemeMode(isChecked: Boolean) {
		if (isChecked) MedriverApp.toggleThemeMode(DARK_MODE)
		else MedriverApp.toggleThemeMode(LIGHT_MODE)
	}
	
	fun setMetricSystem(metricSystem: MetricSystem) {
		MedriverApp.toggleMetricSystem(metricSystem)
	}
	
}