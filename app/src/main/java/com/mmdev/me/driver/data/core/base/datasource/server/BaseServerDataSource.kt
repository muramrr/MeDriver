/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 18:09
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
			.document(serverOperation.dateAdded.toString())
			.setAsFlow(serverOperation)
	
}