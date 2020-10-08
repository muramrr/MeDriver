/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.10.2020 21:33
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.auth.mappers

import com.mmdev.me.driver.data.datasource.user.local.entities.UserEntity
import com.mmdev.me.driver.data.datasource.user.remote.dto.FirestoreUserDto
import com.mmdev.me.driver.domain.user.UserData

/**
 * Used to convert [UserEntity] into [UserData] and [FirestoreUserDto]
 */

object EntityMappers {
	
	fun toDto(entity: UserEntity): FirestoreUserDto =
		FirestoreUserDto(
			id = entity.id,
			email = entity.email,
			emailVerified = entity.isEmailVerified,
			premium = entity.isPremium,
			syncEnabled = entity.isSyncEnabled
		)
	
	fun toDomain(entity: UserEntity): UserData =
		UserData(
			id = entity.id,
			email = entity.email,
			isEmailVerified = entity.isEmailVerified,
			isPremium = entity.isPremium,
			isSyncEnabled = entity.isSyncEnabled
		)
	
}