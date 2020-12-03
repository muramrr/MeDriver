/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.12.2020 18:48
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.auth.mappers

import com.mmdev.me.driver.data.datasource.user.local.entities.UserEntity
import com.mmdev.me.driver.data.datasource.user.remote.dto.FirestoreUserDto
import com.mmdev.me.driver.domain.user.UserDataInfo

/**
 * In [UserEntity] -> Out: [UserDataInfo], [FirestoreUserDto]
 */

object EntityMappers {
	
	fun toDto(entity: UserEntity): FirestoreUserDto =
		FirestoreUserDto(
			id = entity.id,
			email = entity.email,
			isEmailVerified = entity.isEmailVerified,
			//subscription = entity.isPremium
		)
	
	fun toDomain(entity: UserEntity): UserDataInfo =
		UserDataInfo(
			id = entity.id,
			email = entity.email,
			isEmailVerified = entity.isEmailVerified,
			//subscription = entity.isPremium
		)
	
}