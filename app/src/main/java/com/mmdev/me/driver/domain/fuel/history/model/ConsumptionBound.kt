/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.09.2020 22:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel.history.model

import com.mmdev.me.driver.core.utils.roundTo

/**
 * Wrapping fuel consumption value
 */

data class ConsumptionBound (
	val consumptionPer100KM: Double = 0.0,
	val consumptionPer100MI: Double = 0.0
) {
	
	constructor(consumptionKM: Double, consumptionMI: Double? = null) :
			this(consumptionKM, (consumptionKM * 1.609).roundTo(2))
	
	constructor(consumptionKM: Double? = null, consumptionMI: Double) :
			this((consumptionMI / 1.609).roundTo(2), consumptionMI)
}