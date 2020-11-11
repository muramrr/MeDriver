/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.11.2020 16:40
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.data.sync.fuel.download.FuelHistoryDownloader
import com.mmdev.me.driver.data.sync.fuel.download.IFuelHistoryDownloader
import com.mmdev.me.driver.data.sync.maintenance.download.IMaintenanceDownloader
import com.mmdev.me.driver.data.sync.maintenance.download.MaintenanceDownloader
import com.mmdev.me.driver.data.sync.vehicle.download.IVehicleDownloader
import com.mmdev.me.driver.data.sync.vehicle.download.VehicleDownloader
import org.koin.dsl.module

/**
 *
 */

val SyncModule = module {
	
	single<IFuelHistoryDownloader> { FuelHistoryDownloader(local = get(), server = get(), mappers = get()) }
	single<IMaintenanceDownloader> { MaintenanceDownloader(local = get(), server = get(), mappers = get()) }
	single<IVehicleDownloader> { VehicleDownloader(local = get(), server = get(), mappers = get()) }
}