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
import com.mmdev.me.driver.data.core.firebase.ServerOperation
import com.mmdev.me.driver.data.core.firebase.setAsFlow

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
	
	protected fun addToJournal(email: String, serverOperation: ServerOperation) =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_USER_ACTIONS_JOURNAL)
			.document("${serverOperation.type}_${serverOperation.dateAdded}")
			.setAsFlow(serverOperation)
	
}