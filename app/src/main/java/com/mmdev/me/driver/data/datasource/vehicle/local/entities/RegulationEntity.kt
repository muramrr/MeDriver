/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.11.2020 17:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.vehicle.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound

/**
 *
 */

@Entity
data class RegulationEntity(
	@Embedded(prefix = "regulations_distance_")
	val distance: DistanceBound,
	val time: Long
)