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
 * In [UserDataInfo] -> Out: [UserEntity], [FirestoreUserDto]
 */

object DomainMappers {
	
	fun toEntity(domain: UserDataInfo): UserEntity =
		UserEntity(
			id = domain.id,
			email = domain.email,
			isEmailVerified = domain.isEmailVerified,
			isPremium = domain.subscription.type != FREE,
			isSyncEnabled = domain.isSyncEnabled
		)
	
	fun toDto(domain: UserDataInfo): FirestoreUserDto =
		FirestoreUserDto(
			id = domain.id,
			email = domain.email,
			isEmailVerified = domain.isEmailVerified,
			subscription = domain.subscription,
			isSyncEnabled = domain.isSyncEnabled
		)
	
	
}