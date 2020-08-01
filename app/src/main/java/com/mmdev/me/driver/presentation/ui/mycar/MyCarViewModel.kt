/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 01.08.20 19:51
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.mycar

import androidx.lifecycle.MutableLiveData
import com.mmdev.me.driver.presentation.core.base.BaseViewModel

/**
 *
 */

class MyCarViewModel : BaseViewModel() {
	
	val myCar: MutableLiveData<MyCarFragment.CarInDropDown> = MutableLiveData()
	
}