/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.09.2020 19:20
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.dsl.module

/**
 * Generally, useless module.
 * Good to simplify DI via fs = get() rather than fs = FirebaseFirestore.getInstance()
 */

val FirebaseModule = module {
	
	single { FirebaseFirestore.getInstance() }
	single { FirebaseAuth.getInstance() }
	
}