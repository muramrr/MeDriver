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

package com.mmdev.me.driver.data.datasource.user.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.me.driver.data.core.base.datasource.BaseDataSource
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