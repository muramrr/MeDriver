/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.11.2020 17:08
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.core.firebase

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.core.utils.log.logWarn
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/**
 * Firebase extensions
 */

private const val TAG = "mylogs_FirestoreExtensions"

fun <T> DocumentReference.getAndDeserializeAsFlow(clazz: Class<T>): Flow<SimpleResult<T>> = flow {
	logDebug(TAG, "Trying to retrieve document `$path` from backend...")
	this@getAndDeserializeAsFlow.get().asFlow().collect { result ->
		result.fold(
			success = { snapshot ->
				logInfo(TAG, "Document retrieve success")
				logDebug(TAG, "is from cache? ${snapshot?.metadata?.isFromCache}, " +
				              "has pending writes? ${snapshot?.metadata?.hasPendingWrites()}")
				
				if (snapshot.exists() && snapshot.data != null) {
					logDebug(TAG, "Data is not null, deserialization in process...")
					val serializedDoc: T = snapshot.toObject(clazz)!!
					logInfo(TAG, "Deserialization to ${clazz.simpleName} succeed...")
					emit(ResultState.success(serializedDoc))
				}
				else {
					logError(TAG, "No such document.")
					emit(ResultState.failure(NoSuchElementException("No such document.")))
				}
				
			},
			failure = {
				logError(TAG, "Failed to retrieve document from backend...")
				emit(ResultState.failure(it))
			}
		)
	}
}

fun <T> Query.executeAndDeserializeAsFlow(clazz: Class<T>): Flow<SimpleResult<List<T>>> = flow {
	logDebug(TAG, "Trying to execute given $this@executeAndDeserializeAsFlow query...")
	this@executeAndDeserializeAsFlow.get().asFlow().collect { result ->
		result.fold(
			success = { querySnapshot ->
				logInfo(TAG, "Query execute successfully")
				if (!querySnapshot.isEmpty) {
					logDebug(TAG, "Query result is not empty, deserialization in process...")
					val resultList = querySnapshot.map { it.toObject(clazz) }
					logInfo(TAG, "Deserialization to ${clazz.simpleName} succeed...")
					emit(ResultState.success(resultList))
				}
				else {
					logWarn(TAG, "Query result is empty.")
					emit(ResultState.success(emptyList<T>()))
				}
				
			},
			failure = {
				logError(TAG, "Failed to retrieve document from backend...")
				emit(ResultState.failure(it))
			}
		)
	}
}



fun <T> DocumentReference.setAsFlow(dataClass: T): Flow<SimpleResult<Unit>> = flow {
	this@setAsFlow.set(dataClass!!).asFlow().collect{ result ->
		result.fold(
			success = {
				logInfo(TAG, "Set $dataClass as document successfully")
				emit(ResultState.success(Unit))
			},
			failure = {
				logError(TAG, "set $dataClass as document error, $it")
				emit(ResultState.failure(it))
			}
		)
		
	}
}
