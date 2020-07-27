/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.07.20 20:38
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.utils

import androidx.appcompat.app.AppCompatDelegate
import com.mmdev.me.driver.presentation.utils.ThemeHelper.ThemeMode.*

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