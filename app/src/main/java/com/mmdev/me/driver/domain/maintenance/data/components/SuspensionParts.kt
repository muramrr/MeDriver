/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.10.2020 15:53
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
}