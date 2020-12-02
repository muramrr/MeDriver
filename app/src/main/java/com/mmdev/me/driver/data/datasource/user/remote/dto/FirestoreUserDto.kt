/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.12.2020 16:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.user.remote.dto

import com.google.firebase.firestore.PropertyName
import com.mmdev.me.driver.domain.user.SubscriptionData

/**
 * User remote model which is used to be stored on backend
 */

data class FirestoreUserDto (
	@PropertyName("id")
	var id: String = "",
	@PropertyName("email")
	var email: String = "",
	@PropertyName("isEmailVerified")
	var isEmailVerified: Boolean = false,
	@PropertyName("subscription")
	var subscription: SubscriptionData = SubscriptionData(),
	@PropertyName("isSyncEnabled")
	var isSyncEnabled: Boolean = false
)