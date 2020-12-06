/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 06.12.2020 15:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.core.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import com.mmdev.me.driver.core.MedriverApp

/**
 * Describes data that will be written to server operation journal
 */

data class ServerOperation(
	val type: ServerOperationType = ServerOperationType.UNKNOWN,
	val vin: String = "",
	val dateAdded: Long = 0,
	val documentId: String = "",
	@Exclude val deviceId: String = MedriverApp.androidId,
	@Exclude @ServerTimestamp val timestamp: Timestamp? = null
)
