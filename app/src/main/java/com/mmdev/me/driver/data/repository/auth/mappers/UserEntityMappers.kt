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
 * Used to convert [UserEntity] into [UserModel] and [FirestoreUserDto]
 */

object UserEntityMappers {
	
	fun userEntityToDto(entity: UserEntity): FirestoreUserDto =
		FirestoreUserDto(
			id = entity.id,
			email = entity.email,
			emailVerified = entity.isEmailVerified,
			premium = entity.isPremium,
			syncEnabled = entity.isSyncEnabled
		)
	
	fun userEntityToDomain(entity: UserEntity): UserModel =
		UserModel(
			id = entity.id,
			email = entity.email,
			isEmailVerified = entity.isEmailVerified,
			isPremium = entity.isPremium,
			isSyncEnabled = entity.isSyncEnabled
		)
	
}