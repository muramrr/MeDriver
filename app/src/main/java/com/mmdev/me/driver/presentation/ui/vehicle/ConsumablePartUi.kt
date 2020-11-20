/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.11.2020 21:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.vehicle

import androidx.annotation.StringRes
import com.mmdev.me.driver.domain.vehicle.data.PendingReplacement
import com.mmdev.me.driver.domain.vehicle.data.Regulation

/**
 *
 */

data class ConsumablePartUi(
	@StringRes val title: Int = 0,
	val replacement: PendingReplacement? = null,
	val regulation: Regulation? = null
)