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

package com.mmdev.me.driver.data.core.base.datasource.server

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.me.driver.data.core.base.datasource.BaseDataSource
import com.mmdev.me.driver.data.core.firebase.asFlow
import com.mmdev.me.driver.data.core.firebase.setAsFlow
import com.mmdev.me.driver.data.datasource.fetching.data.ServerDocumentType
import com.mmdev.me.driver.data.datasource.fetching.data.ServerDocumentType.VEHICLE
import com.mmdev.me.driver.data.datasource.fetching.data.ServerOperation
import com.mmdev.me.driver.domain.core.ResultState.Companion.toUnit
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 *
 */

abstract class BaseServerDataSource(
	private val fs: FirebaseFirestore
): BaseDataSource() {
	
	private companion object {
		private const val FS_USERS_COLLECTION = "users"
		private const val FS_USER_ACTIONS_JOURNAL = "journal"
	}
	
	protected fun addOperationToJournal(email: String, serverOperation: ServerOperation) =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_USER_ACTIONS_JOURNAL)
			.document(
				"${serverOperation.documentType}_${
					if (serverOperation.documentType == VEHICLE) serverOperation.vin
					else serverOperation.dateAdded
				}"
			)
			.setAsFlow(serverOperation)
	
	protected fun deleteOperationFromJournal(
		email: String,
		type: ServerDocumentType,
		id: String
	): Flow<SimpleResult<Unit>> =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_USER_ACTIONS_JOURNAL)
			.document("${type}_${id}")
			.delete()
			.asFlow()
			.map { it.toUnit() }
}