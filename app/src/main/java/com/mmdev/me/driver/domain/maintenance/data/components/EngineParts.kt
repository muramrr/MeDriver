/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 28.11.2020 16:34
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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