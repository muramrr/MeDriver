/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 25.11.2020 19:02
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.utils.extensions.domain

import com.mmdev.me.driver.domain.fuel.prices.data.FuelStation
import com.mmdev.me.driver.presentation.ui.fuel.FuelStationConstants

/**
 * Extension to get fuel station icon based on its slug
 */

fun FuelStation.brandIcon(): Int = FuelStationConstants.fuelStationIconMap.getValue(slug)