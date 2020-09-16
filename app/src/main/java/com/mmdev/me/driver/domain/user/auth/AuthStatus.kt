/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 15.09.2020 21:15
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.user.auth

/**
 * [UNAUTHENTICATED] - Initial state, the user needs to authenticate
 * [AUTHENTICATED] - The user has authenticated successfully
 */

enum class AuthStatus {
	UNAUTHENTICATED,
	AUTHENTICATED,
}