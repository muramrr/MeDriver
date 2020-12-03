/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.12.2020 17:11
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.subscription

import androidx.annotation.ColorRes

/**
 * Represents premium plans visual appearance in [SubscriptionBottomSheet]
 */

data class PlanUi(
	@ColorRes val backgroundColor: Int,
	val features: List<FeatureUi>,
	val isChosen: Boolean,
	val pricePerMonth: String,
	val subscription: SubscriptionTypeUi
)