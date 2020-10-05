/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.10.2020 16:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.user

data class UserData (
	val id: String = "",
	val email: String = "",
	val isEmailVerified: Boolean = false,
	val isPremium: Boolean = false,
	val isSyncEnabled: Boolean = false
)