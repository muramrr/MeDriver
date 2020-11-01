/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 01.11.2020 15:44
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.vehicle

/**
 * Used to be displayed in drop down list
 */

data class VehicleUi(val icon: Int, val title: String, val titleRes: Int? = null, val vin: String)