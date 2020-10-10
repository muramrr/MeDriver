/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.10.2020 14:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.auth.mappers

import com.google.firebase.auth.FirebaseUser
import com.mmdev.me.driver.data.datasource.user.local.entities.UserEntity
import com.mmdev.me.driver.data.datasource.user.remote.dto.FirestoreUserDto
import com.mmdev.me.driver.domain.user.UserData

/**
 * Mapping between user DTOs/entities
 */

class UserMappersFacade {
	
	// domains
	fun userDomainToEntity(domain: UserData): UserEntity = DomainMappers.toEntity(domain)
	fun userDomainToDto(domain: UserData): FirestoreUserDto = DomainMappers.toDto(domain)
	
	// entities
	fun userEntityToDto(entity: UserEntity): FirestoreUserDto = EntityMappers.toDto(entity)
	
	fun userEntityToDomain(entity: UserEntity): UserData = EntityMappers.toDomain(entity)
	
	
	
	// dtos
	fun userDtoToDomain(dto: FirestoreUserDto): UserData = DtoMappers.toDomain(dto)
	
	fun userDtoToEntity(dto: FirestoreUserDto): UserEntity = DtoMappers.toEntity(dto)


	// framework based
	fun mapFirebaseUserToUserDto(firebaseUser: FirebaseUser): FirestoreUserDto = FirestoreUserDto(
		id = firebaseUser.uid,
		email = firebaseUser.email!!,
		emailVerified = firebaseUser.isEmailVerified
	)
	
	fun mapFirebaseUserToEntity(firebaseUser: FirebaseUser): UserEntity = UserEntity(
		id = firebaseUser.uid,
		email = firebaseUser.email!!,
		isEmailVerified = firebaseUser.isEmailVerified,
		isPremium = false,
		isSyncEnabled = false
	)
	
}