/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.08.20 20:38
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
		R.drawable.gas_station_amic,
		R.drawable.gas_station_anp,
		R.drawable.gas_station_brsm,
		R.drawable.gas_station_glusco,
		R.drawable.gas_station_klo,
		R.drawable.gas_station_okko,
		R.drawable.gas_station_socar,
		R.drawable.gas_station_shell,
		R.drawable.gas_station_ukrnafta,
		R.drawable.gas_station_wog
	)

	internal val fuelProvidersMap = fuelProvidersSlug.zip(fuelProvidersIcons).toMap()

}