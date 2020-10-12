/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 12.10.2020 18:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
	
	companion object {
		val valuesArray: Array<SparePart> = values() as Array<SparePart>
	}
}