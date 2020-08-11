/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.08.20 20:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.mappers

import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelProviderAndPrices
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelSummaryEntity
import com.mmdev.me.driver.domain.fuel.model.FuelProvider
import com.mmdev.me.driver.domain.fuel.model.FuelSummary

/**
 * FuelDataMappersFacade for multiple mappers used in FuelRepository
 */

class FuelDataMappersFacade (
	//network dto -> db entity
	val mapFuelResponseToDb: (response) -> List<FuelProviderAndPrices>,
	//network dto -> db entity
	val mapFuelResponseSummaryToDb: (response) -> List<FuelSummaryEntity>,
	//network dto -> dm
	val mapFuelResponseToDm: (response) -> List<FuelProvider>,
	//db dto -> dm
	val mapDbSummariesToDm: (List<FuelSummaryEntity>) -> List<FuelSummary>,
	//db dto -> dm
	val mapDbFuelProviderToDm: (List<FuelProviderAndPrices>) -> List<FuelProvider>
)

