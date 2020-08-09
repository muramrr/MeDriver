/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.08.20 17:05
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.mappers

import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelProviderAndPrices
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelSummaryEntity
import com.mmdev.me.driver.domain.fuel.FuelProvider
import com.mmdev.me.driver.domain.fuel.FuelSummary

/**
 * FuelDataMappersFacade for multiple mappers used in FuelRepository
 */

class FuelDataMappersFacade (
	//network dto -> db entity
	val mapResponseModelToLocal: (response) -> List<FuelProviderAndPrices>,
	//network dto -> db entity
	val mapResponseModelToLocalSummary: (response) -> List<FuelSummaryEntity>,
	//db dto -> dm
	val mapDbSummariesToDomain: (List<FuelSummaryEntity>) -> List<FuelSummary>,
	//db dto -> dm
	val mapDbFuelInfoToDomain: (List<FuelProviderAndPrices>) -> List<FuelProvider>
)

