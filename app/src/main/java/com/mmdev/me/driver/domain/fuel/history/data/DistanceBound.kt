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

package com.mmdev.me.driver.domain.fuel.history.data

import kotlin.math.roundToInt

/**
 * Used Primary to wrap kilometers and miles into one object and switch between them
 */

data class DistanceBound(val kilometers: Int = 0, val miles: Int = 0) {
	constructor(kilometers: Int, miles: Int? = null) : this(kilometers, (kilometers / 1.609).roundToInt())
	constructor(kilometers: Int? = null, miles: Int) : this((miles * 1.609).roundToInt(), miles)
}