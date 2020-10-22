/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.10.2020 19:41
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance.add.child

import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart

/**
 * Data class that holds info about single child selected from children adapter
 */

data class Child(
	val title: String,
	val sparePart: SparePart
)