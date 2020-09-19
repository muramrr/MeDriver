/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.09.2020 04:29
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.vehicle

import androidx.lifecycle.MutableLiveData
import com.mmdev.me.driver.domain.vehicle.IVehicleRepository
import com.mmdev.me.driver.presentation.core.base.BaseViewModel

/**
 *
 */

class VehicleViewModel (private val repository: IVehicleRepository) : BaseViewModel() {
	
	val vehicle: MutableLiveData<VehicleFragment.CarInDropDown> = MutableLiveData()
	
}