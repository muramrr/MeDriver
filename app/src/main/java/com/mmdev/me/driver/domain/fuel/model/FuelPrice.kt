/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.08.20 18:36
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel.model

import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.presentation.ui.fuel.FuelProviders

/**
 * Domain model
 */

data class FuelPrice (
	val fuelProviderTitle: String,
	val fuelProviderSlug: String,
	val price: String,
	val type: FuelType,
	val updatedDate: String
) {

	//constructor which accepts code of FuelType
	constructor(title: String, slug: String, price: String, type: Int, date: String):
		this(fuelProviderTitle = title,
		     fuelProviderSlug = slug,
		     price = price,
		     type = FuelType.values().find { it.code == type } ?: FuelType.A95,
		     updatedDate = date)
	
	val brandIcon: Int =
		if (FuelProviders.fuelProvidersMap.containsKey(fuelProviderSlug))
			FuelProviders.fuelProvidersMap.getValue(fuelProviderSlug)
		else 0

}

