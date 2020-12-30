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

package com.mmdev.me.driver.data.datasource.vehicle.local.entities

import androidx.room.Embedded
import androidx.room.Entity

/**
 *
 */

@Entity
data class MaintenanceRegulationsEntity(
	@Embedded(prefix = "regulation_insurance_")
	val insurance: RegulationEntity,
	@Embedded(prefix = "regulation_air_filter_")
	val airFilter: RegulationEntity,
	@Embedded(prefix = "regulation_engine_oil_filter_")
	val engineFilterOil: RegulationEntity,
	@Embedded(prefix = "regulation_fuel_filter_")
	val fuelFilter: RegulationEntity,
	@Embedded(prefix = "regulation_cabin_filter_")
	val cabinFilter: RegulationEntity,
	@Embedded(prefix = "regulation_brakes_fluid_")
	val brakesFluid: RegulationEntity,
	@Embedded(prefix = "regulation_grm_kit_")
	val grmKit: RegulationEntity,
	@Embedded(prefix = "regulation_plugs_")
	val plugs: RegulationEntity,
	@Embedded(prefix = "regulation_transmission_oil_filter_")
	val transmissionFilterOil: RegulationEntity
)