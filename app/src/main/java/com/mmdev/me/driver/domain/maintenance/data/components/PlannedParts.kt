/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 14.11.2020 18:17
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.maintenance.data.components

import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart

/**
 * Enumerated child components for [PLANNED] vehicle system node
 */

enum class PlannedParts: SparePart {
	INSURANCE,
	FILTER_AIR,
	FILTER_OIL,
	FILTER_FUEL,
	FILTER_CABIN,
	BRAKES_FLUID,
	OIL_ENGINE,
	GRM_KIT,
	PLUGS, //свеча (зажигания или накалывания)
	ANTIFREEZE,
	OIL_TRANSMISSION, //масло для кпп
	FILTER_TRANSMISSION, //для акпп
	;
	
	override fun getSparePartName(): String = name
	
	companion object {
		val valuesArray: Array<SparePart> = values() as Array<SparePart>
	}
}