/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 29.07.20 20:12
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.common.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * generic class for viewModels
 */

abstract class BaseViewModel: ViewModel() {

	protected val TAG = "mylogs_" + javaClass.simpleName

//	inline fun <T> launchOnViewModelScope(crossinline block: suspend () -> LiveData<T>): LiveData<T> {
//		return liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
//			emitSource(block())
//		}
//	}

	inline fun <T> launchOnViewModelScope(crossinline block: suspend () -> Unit) {
		viewModelScope.launch(Dispatchers.IO) {
			block()
		}
	}

	override fun onCleared() {
		Log.wtf(TAG, "${javaClass.simpleName} on cleared called")
		super.onCleared()
	}
}