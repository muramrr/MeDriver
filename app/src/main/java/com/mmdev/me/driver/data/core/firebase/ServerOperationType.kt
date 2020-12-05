/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.12.2020 14:05
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.core.firebase

/**
 * Describes types of server operation to be written in journal
 */

enum class ServerOperationType {
	VEHICLE, FUEL_HISTORY, MAINTENANCE, UNKNOWN
}