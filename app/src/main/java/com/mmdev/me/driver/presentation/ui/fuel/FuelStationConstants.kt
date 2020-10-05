/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.10.2020 16:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel

import com.mmdev.me.driver.R
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStation

/**
 * Basic [FuelStation] related constants
 *
 * note: This should not be placed inside domain package due to [R] dependent vars
 */

object FuelStationConstants {
	
	private val fuelStationTitle = listOf(
		"AMIC",
		"ANP",
		"БРСМ-Нафта",
		"GLUSCO",
		"KLO",
		"OKKO",
		"SOCAR",
		"Shell",
		"Укрнафта",
		"UPG",
		"WOG"
	)
	
	private val fuelStationSlug = listOf(
		"amic",
		"anp",
		"brsm-nafta",
		"glusco",
		"klo",
		"okko",
		"socar",
		"shell",
		"ukrnafta",
		"upg",
		"wog"
	)

	private val fuelStationIcons = listOf(
		R.drawable.fuel_station_amic,
		R.drawable.fuel_station_anp,
		R.drawable.fuel_station_brsm,
		R.drawable.fuel_station_glusco,
		R.drawable.fuel_station_klo,
		R.drawable.fuel_station_okko,
		R.drawable.fuel_station_socar,
		R.drawable.fuel_station_shell,
		R.drawable.fuel_station_ukrnafta,
		R.drawable.fuel_station_upg,
		R.drawable.fuel_station_wog
	)

	val fuelStationIconMap =
		fuelStationSlug.zip(fuelStationIcons).toMap().withDefault { 0 }
	
	val fuelStationList = fuelStationTitle.zip(fuelStationSlug).map {
		FuelStation(it.first, it.second)
	}
	
}