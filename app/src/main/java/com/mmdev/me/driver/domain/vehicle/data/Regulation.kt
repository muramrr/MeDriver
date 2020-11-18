/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.11.2020 00:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.vehicle.data

import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound

/**
 * Regulation bound. Invokes when [time] or [distance] limit reached
 */

data class Regulation(
	val distance: DistanceBound,
	val time: Long
)