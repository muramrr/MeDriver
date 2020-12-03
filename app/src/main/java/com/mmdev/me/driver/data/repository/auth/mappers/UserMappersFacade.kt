/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.12.2020 18:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.auth.mappers

import com.google.firebase.auth.FirebaseUser
import com.mmdev.me.driver.data.datasource.user.local.entities.UserEntity
import com.mmdev.me.driver.data.datasource.user.remote.dto.FirestoreUserDto
import com.mmdev.me.driver.domain.user.UserDataInfo

/**
 * Mapping between user DTOs/entities/domain data classes
 */

class UserMappersFacade {
	
	// domains
	fun domainToEntity(domain: UserDataInfo): UserEntity = DomainMappers.toEntity(domain)
	fun domainToDto(domain: UserDataInfo): FirestoreUserDto = DomainMappers.toDto(domain)
	
	// entities
	fun userEntityToDto(entity: UserEntity): FirestoreUserDto = EntityMappers.toDto(entity)
	
	fun userEntityToDomain(entity: UserEntity): UserDataInfo = EntityMappers.toDomain(entity)
	
	
	
	// dtos
	fun userDtoToDomain(dto: FirestoreUserDto): UserDataInfo = DtoMappers.toDomain(dto)
	
	fun userDtoToEntity(dto: FirestoreUserDto): UserEntity = DtoMappers.toEntity(dto)


	// framework based
	fun firebaseUserToUserDto(firebaseUser: FirebaseUser): FirestoreUserDto = FirestoreUserDto(
		id = firebaseUser.uid,
		email = firebaseUser.email!!,
		isEmailVerified = firebaseUser.isEmailVerified
	)
	
	fun firebaseUserToEntity(firebaseUser: FirebaseUser): UserEntity = UserEntity(
		id = firebaseUser.uid,
		email = firebaseUser.email!!,
		isEmailVerified = firebaseUser.isEmailVerified,
		isPremium = false
	)
	
}