/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.09.2020 18:48
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
import com.mmdev.me.driver.data.datasource.user.remote.model.FirestoreUser
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 * [IUserRemoteDataSource] implementation
 */

internal class UserRemoteDataSourceImpl (private val db: FirebaseFirestore):
		IUserRemoteDataSource, BaseDataSource() {
	
	companion object {
		private const val DB_USERS_COLLECTION = "users"
	}
	
	override fun getFirestoreUser(email: String): Flow<SimpleResult<FirestoreUser>> =
		db.collection(DB_USERS_COLLECTION)
			.document(email)
			.getAndDeserialize(FirestoreUser::class.java)
	
	override fun <T> updateFirestoreUserField(
		userId: String, field: String, value: T
	): Flow<SimpleResult<Void>> =
		db.collection(DB_USERS_COLLECTION)
			.document(userId)
			.update(field, value)
			.asFlow()
	
	override fun writeFirestoreUser(userBackend: FirestoreUser): Flow<SimpleResult<Unit>> =
		db.collection(DB_USERS_COLLECTION)
			.document(userBackend.email)
			.setAsFlow(userBackend)
}