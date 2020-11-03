/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.11.2020 16:51
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.user.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.me.driver.data.core.base.BaseDataSource
import com.mmdev.me.driver.data.core.firebase.asFlow
import com.mmdev.me.driver.data.core.firebase.getAndDeserializeAsFlow
import com.mmdev.me.driver.data.core.firebase.setAsFlow
import com.mmdev.me.driver.data.datasource.user.remote.dto.FirestoreUserDto
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 * [IUserRemoteDataSource] implementation
 */

class UserRemoteDataSourceImpl(private val fs: FirebaseFirestore):
		IUserRemoteDataSource, BaseDataSource() {
	
	private companion object {
		private const val FS_USERS_COLLECTION = "users"
	}
	
	override fun getFirestoreUser(email: String): Flow<SimpleResult<FirestoreUserDto>> =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.getAndDeserializeAsFlow(FirestoreUserDto::class.java)
	
	override fun updateFirestoreUserField(
		email: String, field: String, value: Any
	): Flow<SimpleResult<Void>> =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.update(field, value)
			.asFlow()
	
	override fun writeFirestoreUser(userDtoBackend: FirestoreUserDto): Flow<SimpleResult<Unit>> =
		fs.collection(FS_USERS_COLLECTION)
			.document(userDtoBackend.email)
			.setAsFlow(userDtoBackend)
}