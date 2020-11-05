/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.11.2020 16:35
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.user.remote.dto

import com.mmdev.me.driver.domain.user.SubscriptionData

/**
 * User remote model which is used to be stored on backend
 */

data class FirestoreUserDto (
	var id: String = "",
	var email: String = "",
	var emailVerified: Boolean = false,
	var subscription: SubscriptionData = SubscriptionData(),
	var syncEnabled: Boolean = false
)