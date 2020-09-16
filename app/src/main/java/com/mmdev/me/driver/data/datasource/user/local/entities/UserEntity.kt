/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.09.2020 17:16
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.user.local.entities

import kotlinx.serialization.Serializable

/**
 *
 */

@Serializable
data class UserEntity(
	val id: String,
	val email: String,
	val isEmailVerified: Boolean,
	val isPremium: Boolean,
//	val premiumValidUntil: Long
)