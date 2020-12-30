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
 * Enumerated child components for [ENGINE] vehicle system node
 */

enum class EngineParts: SparePart {
	ANTIFREEZE,
	MOUNT, //подушка двигателя
	SILENCER_CORRUGATION, //гофра глушителя
	TIMING_CHAIN_KIT, //комплект цепи грм, меняется редко (может прослужить даже до 350к км)
	
	CYLINDER_UNIT_HEADS, //гбц
	CYLINDER_UNIT_HEADS_GASKET, //прокладка гбц
	CYLINDER_HEAD_BOLTS, //болты гбц
	
	CYLINDER, //цилиндр
	CYLINDER_SHELL, //гильза цилиндра
	CYLINDER_UNIT_LINER, //вкладыши блока цилиндров
	CYLINDER_UNIT, //блок цилиндров
	
	BELT_GENERATOR,
	BELT_AIR_CONDITIONER,
	
	TURBO, //турбина
	INTERCOOLER,
	COMPRESSOR,
	BELT_COMPRESSOR,
	
	PISTON_RINGS, //комплект поршневых колец
	PISTON, //поршень
	
	PUMP_FUEL, //бензонасос
	FUEL_BURNER, //топливная форсунка
	BELT_PUMP_FUEL, //топливный ремень
	PIPE_FUEL, //топливный шланг
	
	RADIATOR, //радиатор для двигателя
	PUMP_WATER, //водяная помпа
	
	DAMPER, //демпфер
	VALVE, //клапан
	ROLLER, //ролик натяжителя ремня
	CONNECTING_ROD, //шатун
	
	CAMSHAFT, //распредвал
	CRANKSHAFT, //коленвал
	
	OTHER;

	override fun getSparePartName(): String = name
	override fun getSparePartOrdinal(): Int = ordinal
	
	companion object {
		val valuesArray: Array<SparePart> = values() as Array<SparePart>
	}
}