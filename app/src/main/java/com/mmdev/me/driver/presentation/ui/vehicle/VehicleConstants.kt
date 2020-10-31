/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.10.2020 19:06
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.vehicle

import com.mmdev.me.driver.R

/**
 *
 */

object VehicleConstants {
	
	private val vehicleBrandVariations: List<List<String>> = listOf(
		listOf("acura", "акура"),
		listOf("alfa", "alfa romeo", "alfaromeo", "альфа", "альфа ромео", "альфаромео"),
		listOf("aston martin", "астон мартин"),
		listOf("audi", "ауди"),
		listOf("bentley", ""),
		listOf("bmw", ""),
		listOf("chevrolet", ""),
		listOf("chrysler", ""),
		listOf("dacia", ""),
		listOf("daewoo", ""),
		listOf("daihatsu", ""),
		listOf("ferrari", ""),
		listOf("fiat", ""),
		listOf("ford", ""),
		listOf("gmc", ""),
		listOf("honda", ""),
		listOf("hummer", ""),
		listOf("hyundai", ""),
		listOf("infiniti", ""),
		listOf("isuzu", ""),
		listOf("jaguar", ""),
		listOf("jeep", ""),
		listOf("kia", ""),
		listOf("lada", ""),
		listOf("land rover", ""),
		listOf("lexus", ""),
		listOf("mercedes", ""),
		listOf("mini", ""),
		listOf("mitsubishi", ""),
		listOf("nissan", ""),
		listOf("opel", ""),
		listOf("peugeot", ""),
		listOf("porsche", ""),
		listOf("saab", ""),
		listOf("skoda", ""),
		listOf("subaru", ""),
		listOf("tesla", ""),
		listOf("toyota", ""),
		listOf("volkswagen", ""),
		listOf("volvo", "")
	)
	
	private val vehicleIconList: List<Int> = listOf(
		R.drawable.ic_car_logo_acura_24,
		R.drawable.ic_car_logo_alfa_24,
		R.drawable.ic_car_logo_aston_24,
		R.drawable.ic_car_logo_audi_24,
		R.drawable.ic_car_logo_bentley_24,
		R.drawable.ic_car_logo_bmw_24,
		R.drawable.ic_car_logo_chevrolet_24,
		R.drawable.ic_car_logo_chrysler_24,
		R.drawable.ic_car_logo_dacia_24,
		R.drawable.ic_car_logo_daewoo_24,
		R.drawable.ic_car_logo_daihatsu_24,
		R.drawable.ic_car_logo_ferrari_24,
		R.drawable.ic_car_logo_fiat_24,
		R.drawable.ic_car_logo_ford_24,
		R.drawable.ic_car_logo_gmc_24,
		R.drawable.ic_car_logo_honda_24,
		R.drawable.ic_car_logo_hummer_24,
		R.drawable.ic_car_logo_hyundai_24,
		R.drawable.ic_car_logo_infiniti_24,
		R.drawable.ic_car_logo_isuzu_24,
		R.drawable.ic_car_logo_jaguar_24,
		R.drawable.ic_car_logo_jeep_24,
		R.drawable.ic_car_logo_kia_24,
		R.drawable.ic_car_logo_lada_24,
		R.drawable.ic_car_logo_land_rover,
		R.drawable.ic_car_logo_lexus_24,
		R.drawable.ic_car_logo_mercedes_24,
		R.drawable.ic_car_logo_mini_24,
		R.drawable.ic_car_logo_mitsubishi_24,
		R.drawable.ic_car_logo_nissan_24,
		R.drawable.ic_car_logo_opel_24,
		R.drawable.ic_car_logo_peugeot_24,
		R.drawable.ic_car_logo_porsche_24,
		R.drawable.ic_car_logo_saab_24,
		R.drawable.ic_car_logo_skoda_24,
		R.drawable.ic_car_logo_subaru_24,
		R.drawable.ic_car_logo_tesla_24,
		R.drawable.ic_car_logo_toyota_24,
		R.drawable.ic_car_logo_volkswagen_24,
		R.drawable.ic_car_logo_volvo_24
	)
	
	
	val vehicleBrandIconMap: Map<List<String>, Int> = vehicleBrandVariations.zip(vehicleIconList).toMap()
}