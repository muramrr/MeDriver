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
 * Enumerated child components for [TRANSMISSION] vehicle system node
 */

enum class TransmissionParts: SparePart {
	SPEED_SENSOR,
	CLUTCH_KIT, //комплект
	CLUTCH_BEARING, //подшипник
	CLUTCH_FLYWHEEL, //маховик
	HOMOKINETIC_JOINT, // "граната"
	HOMOKINETIC_JOINT_DUSTER, //пыльник "гранаты"
	CROSS_STEERING, //крестовина рулевого
	CROSS_GEAR, //крестовина привода
	SEMIAXIS, //полуось
	OTHER
	;
	
	override fun getSparePartName(): String = name
	override fun getSparePartOrdinal(): Int = ordinal
	
	companion object {
		val valuesArray: Array<SparePart> = values() as Array<SparePart>
	}
}