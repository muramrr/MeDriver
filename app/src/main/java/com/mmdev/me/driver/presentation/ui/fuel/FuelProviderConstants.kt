/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 13.08.20 18:43
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel

import com.mmdev.me.driver.R

/**
 *
 */

object FuelProviderConstants {


	private val fuelProvidersSlug = listOf(
		"amic",
		"anp",
		"brsm-nafta",
		"glusco",
		"klo",
		"okko",
		"socar",
		"shell",
		"ukrnafta",
		"wog"
	)

	private val fuelProvidersIcons = listOf(
		R.drawable.fuel_station_amic,
		R.drawable.fuel_station_anp,
		R.drawable.fuel_station_brsm,
		R.drawable.fuel_station_glusco,
		R.drawable.fuel_station_klo,
		R.drawable.fuel_station_okko,
		R.drawable.fuel_station_socar,
		R.drawable.fuel_station_shell,
		R.drawable.fuel_station_ukrnafta,
		R.drawable.fuel_station_wog
	)

	internal val fuelProvidersIconMap =
		fuelProvidersSlug.zip(fuelProvidersIcons).toMap().withDefault { 0 }
	
	
}