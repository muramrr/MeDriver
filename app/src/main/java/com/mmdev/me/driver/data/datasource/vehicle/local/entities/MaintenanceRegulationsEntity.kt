/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 18.11.2020 15:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.vehicle.local.entities

import androidx.room.Embedded
import androidx.room.Entity

/**
 *
 */

@Entity
data class MaintenanceRegulationsEntity(
	@Embedded(prefix = "regulation_air_filter_")
	val airFilter: RegulationEntity?,
	@Embedded(prefix = "regulation_engine_oil_filter_")
	val engineFilterOil: RegulationEntity?,
	@Embedded(prefix = "regulation_cabin_filter_")
	val cabinFilter: RegulationEntity?,
	@Embedded(prefix = "regulation_breaks_fluid_")
	val breaksFluid: RegulationEntity?,
	@Embedded(prefix = "regulation_fuel_filter_")
	val fuelFilter: RegulationEntity?,
	@Embedded(prefix = "regulation_grm_kit_")
	val grmKit: RegulationEntity?,
	@Embedded(prefix = "regulation_tires_wear_")
	val tires: RegulationEntity?
)