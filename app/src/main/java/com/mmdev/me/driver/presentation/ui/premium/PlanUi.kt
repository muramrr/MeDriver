/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.11.2020 20:12
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.premium

import androidx.annotation.StringRes

/**
 * Represents premium plans visual appearance in [PremiumBottomSheet]
 */

data class PlanUi(
	@StringRes val duration: Int,
	val price: String,
	val pricePerMonth: String,
	val savingValue: String
)