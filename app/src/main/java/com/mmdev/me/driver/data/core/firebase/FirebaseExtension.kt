/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 18.09.2020 17:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.core.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.mmdev.me.driver.core.utils.MyDispatchers
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.user.UserModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn


/**
 * Convert a google [Task] to a flow by adding a success listener and
 * failure listener and emitting only once when the Task succeed before completing or cancelling
 * if the Task failed
 */
fun <TResult> Task<TResult>.asFlow() = callbackFlow<SimpleResult<TResult>> {
	addOnSuccessListener {
		safeOffer(ResultState.success(it))
		close()
	}
	addOnFailureListener { e ->
		safeOffer(ResultState.failure(e))
		logError("mylogs_firebaseFlow", e.message ?:
		                                "Failure listener invoked inside asFlow() function")
		
		cancel(e.message ?: "", e)
		//close(e)
	}
	awaitClose()
}.flowOn(MyDispatchers.io())




/**
 * Convert [FirebaseUser] to a domain [UserModel]
 * Used in different classes, so it was made as an extension
 */
fun FirebaseUser.mapToUserModel(): UserModel = UserModel(
	id = this.uid,
	email = this.email!!,
	isEmailVerified = this.isEmailVerified
)