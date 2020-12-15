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

package com.mmdev.me.driver.data.datasource.fetching.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.data.datasource.fetching.data.ServerOperationType.UNKNOWN

/**
 * Describes data that will be written to server operation journal
 */

data class ServerOperation(
	val operationType: ServerOperationType = UNKNOWN,
	val vin: String = "",
	val dateAdded: Long = 0,
	val documentId: String = "",
	val documentType: ServerDocumentType = ServerDocumentType.UNKNOWN,
	@Exclude val deviceId: String = MedriverApp.androidId,
	@Exclude @ServerTimestamp val timestamp: Timestamp? = null
)
