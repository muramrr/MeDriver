/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 17.11.2020 16:55
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.vehicle.data

import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import kotlinx.datetime.LocalDate

/**
 *
 */

data class PendingReplacement (
	val distanceRemain: DistanceBound,
	val finalDate: LocalDate
)