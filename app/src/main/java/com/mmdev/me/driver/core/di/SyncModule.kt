/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 20:33
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.data.sync.download.fuel.FuelHistoryDownloader
import com.mmdev.me.driver.data.sync.download.fuel.IFuelHistoryDownloader
import com.mmdev.me.driver.data.sync.download.journal.IJournalDownloader
import com.mmdev.me.driver.data.sync.download.journal.JournalDownloader
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
	
	factory<IJournalDownloader> { JournalDownloader(fs = get()) }
	factory<IFuelHistoryDownloader> { FuelHistoryDownloader(local = get(), server = get(), mappers = get()) }
	factory<IMaintenanceDownloader> { MaintenanceDownloader(local = get(), server = get(), mappers = get()) }
	factory<IVehicleDownloader> { VehicleDownloader(local = get(), server = get(), mappers = get()) }
	
	factory<IFuelHistoryUploader> { FuelHistoryUploader(local = get(), server = get(), mappers = get()) }
	factory<IMaintenanceUploader> { MaintenanceUploader(local = get(), server = get(), mappers = get()) }
	factory<IVehicleUploader> { VehicleUploader(local = get(), server = get(), mappers = get()) }
}