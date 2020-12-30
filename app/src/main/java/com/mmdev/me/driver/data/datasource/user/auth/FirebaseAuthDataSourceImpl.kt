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

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.mmdev.me.driver.data.core.base.datasource.BaseDataSource
import com.mmdev.me.driver.data.core.firebase.asFlow
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * [IFirebaseAuthDataSource] implementation
 */

class FirebaseAuthDataSourceImpl(private val auth: FirebaseAuth) :
		BaseDataSource(), IFirebaseAuthDataSource {
	
	override fun resetPassword(email: String) : Flow<SimpleResult<Void>> =
		auth.sendPasswordResetEmail(email).asFlow()
	
	
	override fun sendEmailVerification(email: String): Flow<SimpleResult<Void>> =
		if (auth.currentUser != null) auth.currentUser!!.sendEmailVerification().asFlow()
		else flowOf(ResultState.failure(Exception("User is null")))
	
	
	override fun signIn(email: String, password: String): Flow<SimpleResult<AuthResult>> =
		auth.signInWithEmailAndPassword(email, password).asFlow()
	
	
	override fun signOut() = auth.signOut()
	
	override fun signUp(email: String, password: String): Flow<SimpleResult<AuthResult>> =
		auth.createUserWithEmailAndPassword(email, password).asFlow()
	
}