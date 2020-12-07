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

package com.mmdev.me.driver.data.datasource.user.auth

import com.google.firebase.auth.FirebaseAuth
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.data.core.firebase.safeOffer
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Class used only to provide [FirebaseAuth] callbacks as [Flow]
 */

class AuthCollector(private val auth: FirebaseAuth) {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	
	val firebaseAuthFlow: Flow<FirebaseAuth> = callbackFlow {
		
		val listener = FirebaseAuth.AuthStateListener { safeOffer(it) }
		
		auth.addAuthStateListener(listener)
		logDebug(TAG, "AuthListener attached.")
		
		awaitClose {
			logDebug(TAG, "AuthListener removed.")
			auth.removeAuthStateListener(listener)
			cancel()
		}
	}
	
}