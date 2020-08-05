/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.08.20 16:19
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui

import androidx.lifecycle.MutableLiveData
import com.mmdev.me.driver.presentation.core.base.BaseViewModel

/**
 * This is the documentation block about the class
 */

class SharedViewModel : BaseViewModel() {
	
	val showLoading: MutableLiveData<Boolean> = MutableLiveData()
	
}