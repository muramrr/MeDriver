/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.09.2020 17:09
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.user.remote.model

/**
 * User remote model which is used to be stored on backend
 */

data class FirestoreUser (
	var id: String = "",
	var email: String = "",
	var emailVerified: Boolean = false,
	var premium: Boolean = false
)