/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 28.11.2020 22:48
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
	
	val vehicleBrands: List<String> = listOf(
		"Acura",
		"Alfa Romeo",
		"Aston Martin",
		"Audi",
		"Bentley",
		"BMW",
		"Chery",
		"Chevrolet",
		"Chrysler",
		"Citroen",
		"Dacia",
		"Daewoo",
		"Daihatsu",
		"Ferrari",
		"Fiat",
		"Ford",
		"Geely",
		"GMC",
		"Honda",
		"Hummer",
		"Hyundai",
		"Infiniti",
		"Isuzu",
		"Jaguar",
		"Jeep",
		"KIA",
		"Lada",
		"Land Rover",
		"Lexus",
		"Mazda",
		"Mercedes-Benz",
		"Mini",
		"Mitsubishi",
		"Nissan",
		"Opel",
		"Peugeot",
		"Porsche",
		"Saab",
		"Skoda",
		"Subaru",
		"Suzuki",
		"Tesla",
		"Toyota",
		"Volkswagen",
		"Volvo"
	)
	
	private val vehicleIconList: List<Int> = listOf(
		R.drawable.car_logo_acura,
		R.drawable.car_logo_alfa,
		R.drawable.car_logo_aston,
		R.drawable.car_logo_audi,
		R.drawable.car_logo_bentley,
		R.drawable.car_logo_bmw,
		R.drawable.car_logo_chery,
		R.drawable.car_logo_chevrolet,
		R.drawable.car_logo_chrysler,
		R.drawable.car_logo_citroen,
		R.drawable.car_logo_dacia,
		R.drawable.car_logo_daewoo,
		R.drawable.car_logo_daihatsu,
		R.drawable.car_logo_ferrari,
		R.drawable.car_logo_fiat,
		R.drawable.car_logo_ford,
		R.drawable.car_logo_geely,
		R.drawable.car_logo_gmc,
		R.drawable.car_logo_honda,
		R.drawable.car_logo_hummer,
		R.drawable.car_logo_hyundai,
		R.drawable.car_logo_infiniti,
		R.drawable.car_logo_isuzu,
		R.drawable.car_logo_jaguar,
		R.drawable.car_logo_jeep,
		R.drawable.car_logo_kia,
		R.drawable.car_logo_lada,
		R.drawable.car_logo_land_rover,
		R.drawable.car_logo_lexus,
		R.drawable.car_logo_mazda,
		R.drawable.car_logo_mercedes,
		R.drawable.car_logo_mini,
		R.drawable.car_logo_mitsubishi,
		R.drawable.car_logo_nissan,
		R.drawable.car_logo_opel,
		R.drawable.car_logo_peugeot,
		R.drawable.car_logo_porsche,
		R.drawable.car_logo_saab,
		R.drawable.car_logo_skoda,
		R.drawable.car_logo_subaru,
		R.drawable.car_logo_suzuki,
		R.drawable.car_logo_tesla,
		R.drawable.car_logo_toyota,
		R.drawable.car_logo_volkswagen,
		R.drawable.car_logo_volvo
	)
	
	
	val vehicleBrandIconMap: Map<String, Int> =
		vehicleBrands.zip(vehicleIconList).toMap().withDefault { 0 }
}