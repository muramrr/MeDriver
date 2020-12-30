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

package com.mmdev.me.driver.data.datasource.fetching

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.getField
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.data.core.firebase.safeOffer
import com.mmdev.me.driver.data.datasource.fetching.data.ServerOperation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * For the first time snapshot listener is attached, it invokes all changes, including
 * [DocumentChange.Type.ADDED]
 * @see https://firebase.google.com/docs/firestore/query-data/listen#view_changes_between_snapshots
 *
 * [isSnapshotInitiated] flag is made for exclude emitting items at init process
 */

class FetchingDataSource(private val fs: FirebaseFirestore) {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	private companion object {
		private const val FS_USERS_COLLECTION = "users"
		private const val FS_USER_ACTIONS_JOURNAL = "journal"
		private const val FS_USER_ACTIONS_JOURNAL_TIMESTAMP_FIELD = "timestamp"
		private const val DEVICE_ID_FIELD = "deviceId"
		
		private const val SOURCE_SERVER = "Server"
		private const val SOURCE_LOCAL = "Local"
	}
	
	private var isSnapshotInitiated = false
	
	fun journalFlow(email: String): Flow<List<ServerOperation>> = callbackFlow {
		
		val journalCollection = fs.collection(FS_USERS_COLLECTION)
			.document(email)
			.collection(FS_USER_ACTIONS_JOURNAL)
			.orderBy(FS_USER_ACTIONS_JOURNAL_TIMESTAMP_FIELD, Query.Direction.DESCENDING)
			.limit(1)
			
		
		// Register listener
		val listener = journalCollection.addSnapshotListener { snapshot, e ->
			e?.let {
				logError(TAG, "Listen failed, error: $e")
				// If exception occurs, cancel this scope with exception message.
				// offer(error(it))
			}
			
			val source = if (snapshot != null && snapshot.metadata.hasPendingWrites()) SOURCE_LOCAL
			else SOURCE_SERVER
			
			if (!snapshot?.documents.isNullOrEmpty() && source == SOURCE_SERVER) {
				
				snapshot!!.documentChanges.forEach { documentChange ->
					// ADDED case is obvious, MODIFIED case appears only when we update vehicle document
					if (documentChange.type in arrayOf(DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED)) {
						
						val document = snapshot.documents.first()
						if (document.getField<String>(DEVICE_ID_FIELD) != MedriverApp.androidId) {
							logInfo(TAG, "$source data is not from this device")
							
							if (isSnapshotInitiated) safeOffer(snapshot.toObjects(ServerOperation::class.java))
						}
						
					}
				}
			}
			else {
				logDebug(TAG, "Data comes from $source source")
			}
			
			isSnapshotInitiated = true
		}
		
		awaitClose {
			// This block is executed when producer channel is cancelled
			// This function resumes with a cancellation exception.
			
			// Dispose listener
			cancel()
			listener.remove()
			logDebug(TAG, "Journal listener removed.")
		}
	}
	
}