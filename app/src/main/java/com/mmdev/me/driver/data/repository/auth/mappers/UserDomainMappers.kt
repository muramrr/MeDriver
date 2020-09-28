/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 28.09.2020 18:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.auth.mappers

import com.mmdev.me.driver.data.datasource.user.local.entities.UserEntity
import com.mmdev.me.driver.data.datasource.user.remote.dto.FirestoreUserDto
import com.mmdev.me.driver.domain.user.UserModel

/**
 * Used to convert domain [UserModel] into [UserEntity] and [FirestoreUserDto]
 */

object UserDomainMappers {
	
	fun userDomainToEntity(domain: UserModel): UserEntity =
		UserEntity(
			id = domain.id,
			email = domain.email,
			isEmailVerified = domain.isEmailVerified,
			isPremium = domain.isPremium,
			isSyncEnabled = domain.isSyncEnabled
		)
	
	fun userDomainToDto(domain: UserModel): FirestoreUserDto =
		FirestoreUserDto(
			id = domain.id,
			email = domain.email,
			emailVerified = domain.isEmailVerified,
			premium = domain.isPremium,
			syncEnabled = domain.isSyncEnabled
		)
	
	
}