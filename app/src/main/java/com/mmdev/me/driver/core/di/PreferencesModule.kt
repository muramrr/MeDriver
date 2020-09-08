/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.09.2020 19:41
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import android.app.Application
import com.cioccarellia.ksprefs.KsPrefs
import org.koin.dsl.module

/**
 * [PREFERENCES_NAME] defines how preferences will be named
 */

private const val PREFERENCES_NAME = "settings"

val PreferencesModule = module {
	
	single { providePreferences(app = get()) }
	
}

private fun providePreferences(app: Application): KsPrefs = KsPrefs(app, PREFERENCES_NAME)