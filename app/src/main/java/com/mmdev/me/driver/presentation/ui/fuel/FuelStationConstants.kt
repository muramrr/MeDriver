/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.08.2020 14:55
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel

import com.mmdev.me.driver.R
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStation

/**
 *
 */

object FuelStationConstants {


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
		"wog"
	)
	
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
		"WOG"
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
		R.drawable.fuel_station_wog
	)

	internal val fuelStationIconMap =
		fuelStationSlug.zip(fuelStationIcons).toMap().withDefault { 0 }
	
	
	
	internal val fuelStationList = arrayListOf(
		FuelStation(brandTitle = fuelStationTitle[0], slug = fuelStationSlug[0]),
		FuelStation(brandTitle = fuelStationTitle[1], slug = fuelStationSlug[1]),
		FuelStation(brandTitle = fuelStationTitle[2], slug = fuelStationSlug[2]),
		FuelStation(brandTitle = fuelStationTitle[3], slug = fuelStationSlug[3]),
		FuelStation(brandTitle = fuelStationTitle[4], slug = fuelStationSlug[4]),
		FuelStation(brandTitle = fuelStationTitle[5], slug = fuelStationSlug[5]),
		FuelStation(brandTitle = fuelStationTitle[6], slug = fuelStationSlug[6]),
		FuelStation(brandTitle = fuelStationTitle[7], slug = fuelStationSlug[7]),
		FuelStation(brandTitle = fuelStationTitle[8], slug = fuelStationSlug[8]),
		FuelStation(brandTitle = fuelStationTitle[9], slug = fuelStationSlug[9])
	)
	
	
}