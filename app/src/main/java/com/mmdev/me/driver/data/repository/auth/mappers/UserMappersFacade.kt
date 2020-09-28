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

import com.google.firebase.auth.FirebaseUser
import com.mmdev.me.driver.data.datasource.user.local.entities.UserEntity
import com.mmdev.me.driver.data.datasource.user.remote.dto.FirestoreUserDto
import com.mmdev.me.driver.domain.user.UserModel

/**
 * Mapping between user DTOs/entities
 */

class UserMappersFacade {
	
	// domains
	fun userDomainToEntity(domain: UserModel): UserEntity = UserDomainMappers.userDomainToEntity(domain)
	fun userDomainToDto(domain: UserModel): FirestoreUserDto = UserDomainMappers.userDomainToDto(domain)
	
	// entities
	fun userEntityToDto(entity: UserEntity): FirestoreUserDto =
		UserEntityMappers.userEntityToDto(entity)
	
	fun userEntityToDomain(entity: UserEntity): UserModel = UserEntityMappers.userEntityToDomain(entity)
	
	
	
	// dtos
	fun userDtoToDomain(dto: FirestoreUserDto): UserModel = FirestoreUserMappers.userDtoToDomain(dto)
	
	fun userDtoToEntity(dto: FirestoreUserDto): UserEntity = FirestoreUserMappers.userDtoToEntity(dto)


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