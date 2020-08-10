/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.08.20 18:31
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.mappers

import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelSummaryEntity
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.model.FuelPrice
import com.mmdev.me.driver.domain.fuel.model.FuelSummary

/**
 * FuelDataMappersFacade for multiple mappers used in FuelRepository
 */

class FuelDataMappersFacade (
	//network dto -> db entity
	val mapResponsePriceToDb: (response) -> List<FuelPriceEntity>,
	//network dto -> db entity
	val mapResponseSummaryToDb: (response) -> List<FuelSummaryEntity>,
	//network dto -> dm
	val mapResponsePriceToDm: (response, fuelType: FuelType) -> List<FuelPrice>,
	//db dto -> dm
	val mapDbSummariesToDm: (List<FuelSummaryEntity>) -> List<FuelSummary>,
	//db dto -> dm
	val mapDbFuelPriceToDm: (List<FuelPriceEntity>) -> List<FuelPrice>
)

