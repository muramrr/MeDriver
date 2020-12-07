/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. medriver
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.me.driver.core.di

import com.mmdev.me.driver.data.sync.download.DataDownloader
import com.mmdev.me.driver.data.sync.download.IDataDownloader
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
	factory<IDataDownloader> { DataDownloader(get(), get(), get(), get()) }
	
	factory<IFuelHistoryUploader> { FuelHistoryUploader(local = get(), server = get(), mappers = get()) }
	factory<IMaintenanceUploader> { MaintenanceUploader(local = get(), server = get(), mappers = get()) }
	factory<IVehicleUploader> { VehicleUploader(local = get(), server = get(), mappers = get()) }
}