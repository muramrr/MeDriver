/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.08.20 18:05
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.mappers

/**
 * fuel model mappers factory
 */

object FuelDataMappersFacadeFactory {
	
	fun create() : FuelDataMappersFacade = FuelDataMappersFacade(
		
		mapResponsePriceToDb = mapFuelPriceResponseToEntity(),
		mapResponseSummaryToDb = mapSummaryResponseToEntity(),
		mapResponsePriceToDm = mapFuelPriceResponseToDm(),
		mapDbSummariesToDm = mapFuelSummaryEntityToDm(),
		mapDbFuelPriceToDm = mapFuelPriceEntityToDm()
	
	)

}