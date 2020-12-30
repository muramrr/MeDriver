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
 * Enumerated child components for [SUSPENSION] vehicle system node
 */

enum class SuspensionParts: SparePart {
	SHOCK_ABSORBER_FRONT,
	SHOCK_ABSORBER_BACK,
	SHOCK_ABSORBER_SUPPORT, //опора армотизатора
	SPRING, //пружина
	SILENTBLOCK,
	SPHERICAL_BEARING, //шаровая опора
	WHEEL_HUB, //ступица
	WHEEL_HUB_BEARING, //подшипник ступицы колеса
	SLEEVE, //втулка
	ARM, //рычаг
	RACK_STABILIZER, //стойка стабилизатора
	OTHER
	;
	
	override fun getSparePartName(): String = name
	override fun getSparePartOrdinal(): Int = ordinal
	
	companion object {
		val valuesArray: Array<SparePart> = values() as Array<SparePart>
	}
}