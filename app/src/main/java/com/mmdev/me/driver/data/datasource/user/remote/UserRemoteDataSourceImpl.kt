/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.09.2020 01:58
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.user.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.me.driver.data.core.base.BaseDataSource
import com.mmdev.me.driver.data.core.firebase.asFlow
import com.mmdev.me.driver.data.core.firebase.getAndDeserialize
import com.mmdev.me.driver.data.core.firebase.setAsFlow
import com.mmdev.me.driver.data.datasource.user.remote.dto.FirestoreUserDTO
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 * [IUserRemoteDataSource] implementation
 */

class UserRemoteDataSourceImpl (private val fs: FirebaseFirestore):
		IUserRemoteDataSource, BaseDataSource() {
	
	private companion object {
		private const val FS_USERS_COLLECTION = "users"
	}
	
	override fun getFirestoreUser(email: String): Flow<SimpleResult<FirestoreUserDTO>> =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.getAndDeserialize(FirestoreUserDTO::class.java)
	
	override fun updateFirestoreUserField(
		email: String, field: String, value: Any
	): Flow<SimpleResult<Void>> =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.update(field, value)
			.asFlow()
	
	override fun writeFirestoreUser(userDTOBackend: FirestoreUserDTO): Flow<SimpleResult<Unit>> =
		fs.collection(FS_USERS_COLLECTION)
			.document(userDTOBackend.email)
			.setAsFlow(userDTOBackend)
}