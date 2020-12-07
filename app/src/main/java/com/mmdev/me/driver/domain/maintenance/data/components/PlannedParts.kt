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

package com.mmdev.me.driver.domain.maintenance.data.components

import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart

/**
 * Enumerated child components for [PLANNED] vehicle system node
 */

enum class PlannedParts: SparePart {
	INSURANCE,
	FILTER_AIR,
	ENGINE_OIL_FILTER,
	FILTER_FUEL,
	FILTER_CABIN,
	BRAKES_FLUID,
	GRM_KIT,
	PLUGS, //свеча (зажигания или накалывания)
	TRANSMISSION_OIL_FILTER,
	;
	
	override fun getSparePartName(): String = name
	override fun getSparePartOrdinal(): Int = ordinal
	
	companion object {
		val valuesArray: Array<SparePart> = values() as Array<SparePart>
	}
}