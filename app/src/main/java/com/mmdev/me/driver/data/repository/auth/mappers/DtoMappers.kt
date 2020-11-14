/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 14.11.2020 15:17
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
 * Used to convert [FirestoreUserDto] into [UserDataInfo] and [UserEntity]
 */

object DtoMappers {
	
	fun toDomain(dto: FirestoreUserDto): UserDataInfo = UserDataInfo(
		id = dto.id,
		email = dto.email,
		isEmailVerified = dto.emailVerified,
		subscription = dto.subscription,
		isSyncEnabled = dto.syncEnabled
	)
	
	fun toEntity(dto: FirestoreUserDto): UserEntity = UserEntity(
		id = dto.id,
		email = dto.email,
		isEmailVerified = dto.emailVerified,
		isPremium = dto.subscription.type != FREE,
		isSyncEnabled = dto.syncEnabled
	)
	
}