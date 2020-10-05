/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.10.2020 16:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fetching

import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity
import com.mmdev.me.driver.data.datasource.vehicle.remote.dto.VehicleDto
import com.mmdev.me.driver.data.repository.vehicle.mappers.VehicleDbEntityMappers
import com.mmdev.me.driver.data.repository.vehicle.mappers.VehicleDomainMappers
import com.mmdev.me.driver.domain.vehicle.data.Vehicle

/**
 * Mappers used primary to fetch data between local and backend databases
 */

class FetchingMappersFacade {
	
	fun vehicleDbToDomain(entity: VehicleEntity): Vehicle =
		VehicleDbEntityMappers.dbEntityToDomain(entity)
	
	fun vehicleDomainToDb(domain: Vehicle): VehicleEntity =
		VehicleDomainMappers.domainToDbEntity(domain)
	
	fun vehicleDomainToApiDto(domain: Vehicle): VehicleDto =
		VehicleDomainMappers.domainToApiDto(domain)
	
}