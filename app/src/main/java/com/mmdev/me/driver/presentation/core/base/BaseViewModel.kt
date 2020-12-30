/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. medriver
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
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