/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.09.2020 19:10
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.core.firebase

import com.google.firebase.firestore.DocumentReference
import com.mmdev.me.driver.core.utils.logDebug
import com.mmdev.me.driver.core.utils.logError
import com.mmdev.me.driver.core.utils.logWarn
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
	logWarn(TAG, "Trying to retrieve document from backend...")
	this@getAndDeserialize.get().asFlow().collect { result ->
		result.fold(
			success = { snapshot ->
				logDebug(TAG, "Document retrieve success")
				if (snapshot.exists() && snapshot.data != null) {
					logDebug(TAG, "Data is not null, deserialization in process...")
					val serializedDoc: T = snapshot.toObject(clazz)!!
					logDebug(TAG, "Deserialization succeed...")
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

fun <T> DocumentReference.setAsFlow(dataClass: T): Flow<SimpleResult<Unit>> =
	this@setAsFlow.set(dataClass!!).asFlow().map { it.toUnit() }
