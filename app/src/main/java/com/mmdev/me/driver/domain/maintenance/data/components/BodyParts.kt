/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 28.11.2020 16:42
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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