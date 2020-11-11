/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.11.2020 19:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.data.sync.download.fuel.FuelHistoryDownloader
import com.mmdev.me.driver.data.sync.download.fuel.IFuelHistoryDownloader
import com.mmdev.me.driver.data.sync.download.maintenance.IMaintenanceDownloader
import com.mmdev.me.driver.data.sync.download.maintenance.MaintenanceDownloader
import com.mmdev.me.driver.data.sync.download.vehicle.IVehicleDownloader
import com.mmdev.me.driver.data.sync.download.vehicle.VehicleDownloader
import com.mmdev.me.driver.data.sync.upload.fuel.FuelHistoryUploader
import com.mmdev.me.driver.data.sync.upload.fuel.IFuelHistoryUploader
import com.mmdev.me.driver.data.sync.upload.maintenance.IMaintenanceUploader
import com.mmdev.me.driver.data.sync.upload.maintenance.MaintenanceUploader
import com.mmdev.me.driver.data.sync.upload.vehicle.IVehicleUploader
import com.mmdev.me.driver.data.sync.upload.vehicle.VehicleUploader
import org.koin.dsl.module

/**
 *
 */

val SyncModule = module {
	
	single<IFuelHistoryDownloader> { FuelHistoryDownloader(local = get(), server = get(), mappers = get()) }
	single<IMaintenanceDownloader> { MaintenanceDownloader(local = get(), server = get(), mappers = get()) }
	single<IVehicleDownloader> { VehicleDownloader(local = get(), server = get(), mappers = get()) }
	
	single<IFuelHistoryUploader> { FuelHistoryUploader(local = get(), server = get(), mappers = get()) }
	single<IMaintenanceUploader> { MaintenanceUploader(local = get(), server = get(), mappers = get()) }
	single<IVehicleUploader> { VehicleUploader(local = get(), server = get(), mappers = get()) }
}