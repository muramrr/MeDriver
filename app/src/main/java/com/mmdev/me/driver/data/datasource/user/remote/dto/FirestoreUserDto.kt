/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. medriver
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.me.driver.data.datasource.user.remote.dto

import com.google.firebase.firestore.PropertyName
import com.mmdev.me.driver.domain.user.SubscriptionData

/**
 * User remote data which is used to be stored on backend
 */

data class FirestoreUserDto (
	@PropertyName("id") val id: String = "",
	@PropertyName("email") val email: String = "",
	@JvmField // use this annotation if your Boolean field is prefixed with 'is'
	@PropertyName("isEmailVerified") val isEmailVerified: Boolean = false,
	@PropertyName("subscription") val subscription: SubscriptionData = SubscriptionData(),
	@PropertyName("installationTokens") val installationTokens: Map<String, String> = emptyMap()
)