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

package com.mmdev.me.driver.core.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 *
 */

class FlowObserver<T> (
	private val lifecycleOwner: LifecycleOwner,
	private val flow: Flow<T>,
	private val collector: suspend (T) -> Unit
) : LifecycleObserver {
	
	private var job: Job? = null
	
	init {
		lifecycleOwner.lifecycle.addObserver(this)
	}
	
	@OnLifecycleEvent(Lifecycle.Event.ON_START)
	private fun onStart() {
		job = lifecycleOwner.lifecycleScope.launch {
			flow.collect(collector)
		}
	}
	
	@OnLifecycleEvent(Lifecycle.Event.ON_STOP)
	private fun onStop() {
		job?.cancel()
		job = null
	}
	
	
}