/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 23.11.2020 18:05
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.vehicle.data

/**
 *
 */

data class Expenses (
	val maintenance: Double = 0.0,
	val fuel: Double = 0.0
) {
	fun getTotal() = maintenance + fuel
}