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

package com.mmdev.me.driver.core.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import org.koin.dsl.module

/**
 * Generally, useless module.
 * Good to simplify DI via fs = get() rather than fs = FirebaseFirestore.getInstance()
 */

val FirebaseModule = module {
	
	single {
		Firebase.firestore.apply {
			firestoreSettings {
				isPersistenceEnabled = true
				cacheSizeBytes = FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED
			}
		}
	}
	single { Firebase.auth }
	
}