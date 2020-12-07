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

package com.mmdev.me.driver.data.repository.auth.mappers

import com.google.firebase.auth.FirebaseUser
import com.mmdev.me.driver.data.datasource.user.remote.dto.FirestoreUserDto
import com.mmdev.me.driver.domain.user.UserDataInfo

/**
 * Mapping between user DTOs/entities/domain data classes
 */

class UserMappersFacade {
	
	// domains
	fun domainToDto(domain: UserDataInfo): FirestoreUserDto = FirestoreUserDto(
		id = domain.id,
		email = domain.email,
		isEmailVerified = domain.isEmailVerified,
		subscription = domain.subscription
	)
	
	// dtos
	fun dtoToDomain(dto: FirestoreUserDto): UserDataInfo = UserDataInfo(
		id = dto.id,
		email = dto.email,
		isEmailVerified = dto.isEmailVerified,
		subscription = dto.subscription
	)
	
	// framework based
	fun firebaseUserToDto(firebaseUser: FirebaseUser): FirestoreUserDto = FirestoreUserDto(
		id = firebaseUser.uid,
		email = firebaseUser.email!!,
		isEmailVerified = firebaseUser.isEmailVerified
	)
	
}