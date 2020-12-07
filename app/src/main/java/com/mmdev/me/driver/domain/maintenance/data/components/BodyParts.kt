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
 * Enumerated child components for [BODY] vehicle system node
 */

enum class BodyParts: SparePart {
	WIPER_FRONT,
	WIPER_BACK,
	WINDSHIELD_FRONT,
	WINDSHIELD_BACK,
	HOOD,
	HOOD_DAMPER,
	TRUNK_DAMPER,
	HEADLIGHT_LEFT,
	HEADLIGHT_RIGHT,
	TAILLIGHT_LEFT,
	TAILLIGHT_RIGHT,
	FOGLIGHT_LEFT,
	FOGLIGHT_RIGHT,
	RADIATOR_GRILL,
	FRONT_BUMPER_GRILL,
	FRONT_BUMPER,
	BACK_BUMPER,
	FRONT_LEFT_FENDER,
	FRONT_RIGHT_FENDER,
	BACK_LEFT_FENDER,
	BACK_RIGHT_FENDER,
	SILL_LEFT,
	SILL_RIGHT,
	WASHER_RESERVOIR,
	OTHER
	;
	
	override fun getSparePartName(): String = name
	override fun getSparePartOrdinal(): Int = ordinal
	
	companion object {
		val valuesArray: Array<SparePart> = values() as Array<SparePart>
	}
}