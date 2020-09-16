/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.09.2020 17:09
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.auth.mappers

import com.google.firebase.auth.FirebaseUser
import com.mmdev.me.driver.data.datasource.user.local.entities.UserEntity
import com.mmdev.me.driver.data.datasource.user.remote.model.FirestoreUser
import com.mmdev.me.driver.domain.user.UserModel

/**
 * Mapping between user model DTOs
 */

class UserMappersFacade {
	
	fun mapLocalUserToFirestoreUser(userEntity: UserEntity): FirestoreUser =
		FirestoreUser(
			id = userEntity.id,
			email = userEntity.email,
			emailVerified = userEntity.isEmailVerified,
			premium = userEntity.isPremium
		)
	
	fun mapLocalUserToDomainUser(userEntity: UserEntity): UserModel =
		UserModel(
			id = userEntity.id,
			email = userEntity.email,
			isEmailVerified = userEntity.isEmailVerified,
			isPremium = userEntity.isPremium
		)
	
	
	
	fun mapFirestoreUserToUserModel(firestoreUser: FirestoreUser): UserModel = UserModel(
		id = firestoreUser.id,
		email = firestoreUser.email,
		isEmailVerified = firestoreUser.emailVerified,
		isPremium = firestoreUser.premium
	)
	
	fun mapFirestoreUserToLocalUser(firestoreUser: FirestoreUser): UserEntity = UserEntity(
		id = firestoreUser.id,
		email = firestoreUser.email,
		isEmailVerified = firestoreUser.emailVerified,
		isPremium = firestoreUser.premium
	)


	fun mapFirebaseUserToFirestoreUser(firebaseUser: FirebaseUser): FirestoreUser = FirestoreUser(
		id = firebaseUser.uid,
		email = firebaseUser.email!!,
		emailVerified = firebaseUser.isEmailVerified
	)
	
	fun mapFirebaseUserToLocalUser(firebaseUser: FirebaseUser): UserEntity = UserEntity(
		id = firebaseUser.uid,
		email = firebaseUser.email!!,
		isEmailVerified = firebaseUser.isEmailVerified,
		isPremium = false
	)
	
}