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
import com.mmdev.me.driver.domain.user.auth.SubscriptionDuration.TRIAL

data class UserDataInfo(
	val id: String = "",
	val email: String = "",
	val isEmailVerified: Boolean = false,
	val subscription: SubscriptionData = SubscriptionData(),
	val isSyncEnabled: Boolean = false
) {
	//todo: check subscription time actuality
	fun isSubscriptionValid(): Boolean =
		subscription.type != NONE && subscription.duration != TRIAL
}