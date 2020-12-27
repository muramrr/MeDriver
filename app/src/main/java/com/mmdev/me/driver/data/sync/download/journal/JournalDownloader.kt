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

package com.mmdev.me.driver.data.sync.download.journal

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.me.driver.data.core.firebase.executeAndDeserializeAsFlow
import com.mmdev.me.driver.data.datasource.fetching.data.ServerOperation
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow

/**
 * [IJournalDownloader] implementation
 */

class JournalDownloader(private val fs: FirebaseFirestore): IJournalDownloader {
	
	private companion object {
		private const val FS_USERS_COLLECTION = "users"
		private const val FS_USER_ACTIONS_JOURNAL = "journal"
		private const val FS_JOURNAL_DATE_ADDED_FIELD = "dateAdded"
	}
	
	override fun getOperations(
		email: String, lastKnownOperationId: Long
	): Flow<SimpleResult<List<ServerOperation>>> =
		fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_USER_ACTIONS_JOURNAL)
			.whereGreaterThan(FS_JOURNAL_DATE_ADDED_FIELD, lastKnownOperationId)
			.executeAndDeserializeAsFlow(ServerOperation::class.java)
	
}