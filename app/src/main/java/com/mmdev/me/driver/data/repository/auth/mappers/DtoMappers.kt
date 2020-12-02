/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.12.2020 16:12
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.auth.mappers

import com.mmdev.me.driver.data.datasource.user.local.entities.UserEntity
import com.mmdev.me.driver.data.datasource.user.remote.dto.FirestoreUserDto
import com.mmdev.me.driver.domain.user.SubscriptionType.FREE
import com.mmdev.me.driver.domain.user.UserDataInfo

/**
 * In [FirestoreUserDto] -> Out: [UserDataInfo], [UserEntity]
 */

object DtoMappers {
	
	fun toDomain(dto: FirestoreUserDto): UserDataInfo = UserDataInfo(
		id = dto.id,
		email = dto.email,
		isEmailVerified = dto.isEmailVerified,
		subscription = dto.subscription,
		isSyncEnabled = dto.isSyncEnabled
	)
	
	fun toEntity(dto: FirestoreUserDto): UserEntity = UserEntity(
		id = dto.id,
		email = dto.email,
		isEmailVerified = dto.isEmailVerified,
		isPremium = dto.subscription.type != FREE,
		isSyncEnabled = dto.isSyncEnabled
	)
	
}