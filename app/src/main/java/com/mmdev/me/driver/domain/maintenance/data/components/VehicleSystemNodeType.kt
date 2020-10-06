/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 06.10.2020 19:20
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.maintenance.data.components

/**
 * Enumerated vehicle system nodes
 */

enum class VehicleSystemNodeType {
	ENGINE, TRANSMISSION, ELECTRICS, SUSPENSION, BRAKES, BODY, PLANNED, OTHERS
}