/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.09.2020 03:39
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
import com.mmdev.me.driver.domain.core.ResultState.Companion.toUnit
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Firebase extensions
 */

private const val TAG = "mylogs_FirestoreExtensions"

fun <T> DocumentReference.getAndDeserialize(clazz: Class<T>): Flow<SimpleResult<T>> = flow {
	logDebug(TAG, "Trying to retrieve document from backend...")
	this@getAndDeserialize.get().asFlow().collect { result ->
		result.fold(
			success = { snapshot ->
				logInfo(TAG, "Document retrieve success")
				if (snapshot.exists() && snapshot.data != null) {
					logInfo(TAG, "Data is not null, deserialization in process...")
					val serializedDoc: T = snapshot.toObject(clazz)!!
					logInfo(TAG, "Deserialization succeed...")
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

fun <T> Query.executeAndDeserialize(clazz: Class<T>): Flow<SimpleResult<List<T>>> = flow {
	logDebug(TAG, "Trying to execute given query...")
	this@executeAndDeserialize.get().asFlow().collect { result ->
		result.fold(
			success = { querySnapshot ->
				logInfo(TAG, "Query execute successfully")
				if (!querySnapshot.isEmpty) {
					logInfo(TAG, "Query result is not empty, deserialization in process...")
					val resultList = querySnapshot.map { it.toObject(clazz) }
					logInfo(TAG, "Deserialization succeed...")
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



fun <T> DocumentReference.setAsFlow(dataClass: T): Flow<SimpleResult<Unit>> =
	this@setAsFlow.set(dataClass!!).asFlow().map { it.toUnit() }
