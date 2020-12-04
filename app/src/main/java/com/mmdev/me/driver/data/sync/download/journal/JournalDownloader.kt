/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 20:14
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.sync.download.journal

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.me.driver.data.core.firebase.ServerOperation
import com.mmdev.me.driver.data.core.firebase.executeAndDeserializeAsFlow
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 * [IJournalDownloader] implementation
 */

class JournalDownloader(private val fs: FirebaseFirestore): IJournalDownloader {
	
	private companion object {
		private const val FS_USERS_COLLECTION = "users"
		private const val FS_USER_ACTIONS_JOURNAL = "journal"
	}
	
	override fun getOperations(
		email: String, lastKnownOperationId: Long
	): Flow<SimpleResult<List<ServerOperation>>> =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_USER_ACTIONS_JOURNAL)
			.whereGreaterThan("dateAdded", lastKnownOperationId)
			.executeAndDeserializeAsFlow(ServerOperation::class.java)
	
}