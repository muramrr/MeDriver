/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.11.2020 18:00
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.core.base

import androidx.lifecycle.ViewModel
import com.mmdev.me.driver.core.utils.log.logDebug


/**
 * generic class for viewModels
 */

abstract class BaseViewModel: ViewModel() {
	
	protected val TAG = "mylogs_${javaClass.simpleName}"

//	inline fun <T> launchOnViewModelScope(crossinline block: suspend () -> LiveData<T>): LiveData<T> {
//		return liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
//			emitSource(block())
//		}
//	}

//	inline fun <T> launchOnViewModelScope(crossinline block: suspend () -> Unit) {
//		viewModelScope.launch(Dispatchers.IO) {
//			block()
//		}
//	}

	override fun onCleared() {
		logDebug(TAG, "${javaClass.simpleName} on cleared called")
		super.onCleared()
	}
}