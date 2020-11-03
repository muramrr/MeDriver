/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.11.2020 17:08
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.data.repository.fuel.history.mappers.FuelHistoryMappersFacade
import com.mmdev.me.driver.data.repository.maintenance.mappers.MaintenanceMappersFacade
import com.mmdev.me.driver.data.repository.vehicle.mappers.VehicleMappersFacade
import org.koin.dsl.module

/**
 *
 */

val MappersModule = module {
	single { MaintenanceMappersFacade() }
	single { VehicleMappersFacade() }
	single { FuelHistoryMappersFacade() }
}