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
	logDebug(TAG, "Trying to execute given query...")
	this@executeAndDeserializeAsFlow.get().asFlow().collect { result ->
		result.fold(
			success = { querySnapshot ->
				if (!querySnapshot.isEmpty) {
					logInfo(TAG, "Query executed successfully, printing first 5 documents...")
					querySnapshot.documents.take(5).forEach {
						logInfo(TAG, it.reference.path)
						it.reference.path
					}
					logDebug(TAG, "Query deserialization to ${clazz.simpleName} in process...")
					val resultList = querySnapshot.toObjects(clazz)
					logInfo(TAG, "Deserialization to ${clazz.simpleName} succeed...")
					emit(ResultState.success(resultList))
				}
				else {
					logWarn(TAG, "Query result is empty.")
					emit(ResultState.success(emptyList<T>()))
				}
				
			},
			failure = {
				logError(TAG, "Failed to execute given query...")
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
