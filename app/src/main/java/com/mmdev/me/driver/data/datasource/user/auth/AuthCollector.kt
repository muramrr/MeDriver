/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.11.2020 17:06
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.user.auth

import com.google.firebase.auth.FirebaseAuth
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.data.core.firebase.safeOffer
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Class used only to provide auth callbacks as [Flow]
 */

class AuthCollector(private val auth: FirebaseAuth) {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	
	val firebaseAuthFlow: Flow<FirebaseAuth> = callbackFlow {
		
		val listener = FirebaseAuth.AuthStateListener { safeOffer(it) }
		
		auth.addAuthStateListener(listener)
		logInfo(TAG, "AuthListener attached.")
		
		awaitClose {
			logInfo(TAG, "AuthListener removed.")
			auth.removeAuthStateListener(listener)
			cancel()
		}
	}
	
}