/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.09.2020 04:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver

import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.domain.fuel.FuelType.A100
import com.mmdev.me.driver.domain.fuel.FuelType.A95


object FuelConstants {
	
	val fuelStationEntityOkko = FuelStationEntity("OKKO", "okko", "01-01-2020")
	val fuelStationEntityWog = FuelStationEntity("WOG", "wog", "01-01-2020")
	
	val fuelPriceEntityOkko100 = FuelPriceEntity("okko", 19.0, A100.code)
	val fuelPriceEntityOkko95 = FuelPriceEntity("okko", 15.0, A95.code)
	
	val fuelPriceEntityWog100 = FuelPriceEntity("wog", 21.0, A100.code)
	val fuelPriceEntityWog95 = FuelPriceEntity("wog", 14.0, A95.code)
	
}