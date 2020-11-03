/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.11.2020 17:12
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.dsl.module

/**
 * Generally, useless module.
 * Good to simplify DI via fs = get() rather than fs = FirebaseFirestore.getInstance()
 */

val FirebaseModule = module {
	
	single { Firebase.firestore }
	single { Firebase.auth }
	
}