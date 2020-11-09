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

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.mmdev.me.driver.core.utils.log.MyLogger.Debug.logWarn
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.user.UserDataInfo
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow


private const val TAG = "mylogs_FirebaseTaskFlow"

/**
 * Convert a google [Task] to a flow by adding a success listener and
 * failure listener and emitting only once when the Task succeed before completing or cancelling
 * if the Task failed
 */
fun <TResult> Task<TResult>.asFlow() = callbackFlow<SimpleResult<TResult>> {
	addOnSuccessListener {
		safeOffer(ResultState.success(it))
		logDebug(TAG, "Task is successful, result = $it")
		close()
	}
	addOnFailureListener { e ->
		safeOffer(ResultState.failure(e))
		logError(TAG, e.message ?: "Failure invoked inside asFlow() extension")
		
		cancel(e.message ?: "", e)
		close(e)
	}
	addOnCanceledListener {
		logWarn(TAG, "Task canceled inside asFlow() extension")
		
		cancel("Task canceled")
	}
	awaitClose()
}




/**
 * Convert [FirebaseUser] to a domain [UserDataInfo]
 * Used in different classes, so it was made as an extension
 */
fun FirebaseUser.mapToUserModel(): UserDataInfo = UserDataInfo(
	id = this.uid,
	email = this.email!!,
	isEmailVerified = this.isEmailVerified
)