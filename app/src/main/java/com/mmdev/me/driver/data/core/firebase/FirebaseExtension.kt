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

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
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
		close()
	}
	addOnFailureListener { e ->
		safeOffer(ResultState.failure(e))
		cancel(e.message ?: "Extension failure", e)
		close(e)
	}
	addOnCanceledListener {
		cancel("Task canceled")
	}
	awaitClose()
}




/**
 * Convert [FirebaseUser] to a domain [UserDataInfo]
 * Used in different classes, so it was made as an extension
 */
fun FirebaseUser.mapToDomainUserData(): UserDataInfo = UserDataInfo(
	id = this.uid,
	email = this.email!!,
	isEmailVerified = this.isEmailVerified
)