/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.09.2020 17:42
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.utils

import androidx.appcompat.app.AppCompatDelegate
import com.mmdev.me.driver.core.utils.ThemeHelper.ThemeMode.BATTERY_SAVE_MODE
import com.mmdev.me.driver.core.utils.ThemeHelper.ThemeMode.DARK_MODE
import com.mmdev.me.driver.core.utils.ThemeHelper.ThemeMode.DEFAULT_MODE
import com.mmdev.me.driver.core.utils.ThemeHelper.ThemeMode.LIGHT_MODE

object ThemeHelper {


	fun applyTheme(theme: ThemeMode) {

		when (theme) {
			LIGHT_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
			DARK_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
			DEFAULT_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
			BATTERY_SAVE_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
		}
	}

	enum class ThemeMode { LIGHT_MODE, DARK_MODE, DEFAULT_MODE, BATTERY_SAVE_MODE }
}