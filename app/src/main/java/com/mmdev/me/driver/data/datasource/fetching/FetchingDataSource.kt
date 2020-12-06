/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 06.12.2020 16:47
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
import com.mmdev.me.driver.data.core.firebase.ServerOperation
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

class FetchingDataSource(
	private val fs: FirebaseFirestore
) {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	private companion object {
		private const val FS_USERS_COLLECTION = "users"
		private const val FS_USER_ACTIONS_JOURNAL = "journal"
		private const val FS_USER_ACTIONS_JOURNAL_TIMESTAMP_FIELD = "timestamp"
	}
	
	private var isSnapshotInitiated = false
	
	fun flow(email: String): Flow<List<ServerOperation>> = callbackFlow {
		
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
			
			val source = if (snapshot != null && snapshot.metadata.hasPendingWrites()) "Local"
			else "Server"
			
			if (!snapshot?.documents.isNullOrEmpty() && source == "Server") {
				
				//check if adding operation was performed not from this device
				if (snapshot!!.documentChanges.first().type == DocumentChange.Type.ADDED) {
					logDebug(TAG, "New document: ${snapshot.documentChanges.first().document.data}")
					
					if (snapshot.documents.first().getField<String>("deviceId") != MedriverApp.androidId) {
						logInfo(TAG, "$source data is not from this device")
						
						if (isSnapshotInitiated)
							offer(snapshot.toObjects(ServerOperation::class.java))
					}
				}
				
				//logDebug(TAG, "$source data: ${snapshot.documents}")
				
			} else {
				logDebug(TAG, "$source data: null")
			}
			
			isSnapshotInitiated = true
		}
		
		awaitClose {
			// This block is executed when producer channel is cancelled
			// This function resumes with a cancellation exception.
			
			// Dispose listener
			cancel()
			listener.remove()
		}
	}
	
}