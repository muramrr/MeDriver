/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. medriver
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.me.driver.presentation.ui.fuel

import com.mmdev.me.driver.R
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStation

/**
 * Basic [FuelStation] related constants
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
		fuelStationSlug.zip(fuelStationIcons).toMap().withDefault { R.drawable.fuel_station_unknown_24 }
	
	val fuelStationList = fuelStationTitle.zip(fuelStationSlug).map {
		FuelStation(it.first, it.second)
	}
	
}