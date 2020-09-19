/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.09.2020 02:43
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.user.remote.dto

/**
 * User remote model which is used to be stored on backend
 */

data class FirestoreUserDTO (
	var id: String = "",
	var email: String = "",
	var emailVerified: Boolean = false,
	var premium: Boolean = false
)