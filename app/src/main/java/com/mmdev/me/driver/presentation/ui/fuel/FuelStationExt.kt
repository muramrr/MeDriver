/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.10.2020 17:53
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel

import com.mmdev.me.driver.domain.fuel.prices.model.FuelStation

/**
 * Basically this property should be stored inside [FuelStation] class body
 * But due to specific [FirebaseFirestore] serialization mechanism
 * This field
 */

fun FuelStation.brandIcon(): Int = FuelStationConstants.fuelStationIconMap.getValue(slug)