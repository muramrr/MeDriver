/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.11.2020 16:35
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.user

import com.mmdev.me.driver.domain.user.SubscriptionType.NONE
import com.mmdev.me.driver.domain.user.auth.SubscriptionDuration
import com.mmdev.me.driver.domain.user.auth.SubscriptionDuration.TRIAL

/**
 * @param startFrom is an epoch millis when subscription starts work
 * @param validUntil contains epoch millis seconds until subscription is valid
 */

data class SubscriptionData(
	val type: SubscriptionType = NONE,
	val duration: SubscriptionDuration = TRIAL,
	val startFrom: Long = 0,
	val validUntil: Long = 0
)