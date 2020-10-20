/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 15.10.2020 19:23
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.maintenance.data.components.base

/**
 * Base for all spare parts
 */

interface SparePart {
	fun getSparePartName(): String
	companion object {
		const val OTHER = "OTHER"
	}
}