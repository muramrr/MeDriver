/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.12.2020 17:46
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.vehicle.data

import com.mmdev.me.driver.core.utils.helpers.DateHelper
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound

/**
 * Regulation bound. Invokes when [time] or [distance] limit reached
 */

data class Regulation(
	val distance: DistanceBound =  DistanceBound(),
	val time: Long = DateHelper.YEAR_DURATION
)