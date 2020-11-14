/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 14.11.2020 15:57
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.subscription

import androidx.annotation.StringRes
import com.mmdev.me.driver.domain.user.SubscriptionType

/**
 * Bound to represent [SubscriptionType] in UI
 */

data class SubscriptionTypeUi(
	val type: SubscriptionType,
	@StringRes val title: Int
)