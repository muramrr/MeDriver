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
 * Used to convert [FirestoreUserDto] into [UserModel] and [UserEntity]
 */

object FirestoreUserMappers {
	
	fun userDtoToDomain(dto: FirestoreUserDto): UserModel = UserModel(
		id = dto.id,
		email = dto.email,
		isEmailVerified = dto.emailVerified,
		isPremium = dto.premium,
		isSyncEnabled = dto.syncEnabled
	)
	
	fun userDtoToEntity(dto: FirestoreUserDto): UserEntity = UserEntity(
		id = dto.id,
		email = dto.email,
		isEmailVerified = dto.emailVerified,
		isPremium = dto.premium,
		isSyncEnabled = dto.syncEnabled
	)
	
}