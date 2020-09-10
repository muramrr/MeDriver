/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.09.2020 17:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel.history.model

import kotlin.math.roundToInt

/**
 * Used Primary to wrap kilometers and miles into one object and switch between them
 */

data class DistanceBound(val kilometers: Int = 0, val miles: Int = 0) {
	constructor(kilometers: Int, miles: Int? = null) : this(kilometers, (kilometers / 1.609).roundToInt())
	constructor(kilometers: Int? = null, miles: Int) : this((miles * 1.609).roundToInt(), miles)
}