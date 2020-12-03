/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 00:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.core.firebase

/**
 * Describes data that will be written to server operation journal
 */

data class ServerOperation(
	val type: ServerOperationType,
	val vin: String,
	val dateAdded: Long,
	val documentId: String
)
