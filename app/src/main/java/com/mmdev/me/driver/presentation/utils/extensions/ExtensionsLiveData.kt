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

package com.mmdev.me.driver.presentation.utils.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

/*
 * combine result of 2 LiveData
 */
inline fun <A, B, R> LiveData<A>.combineWith(
	otherLiveData: LiveData<B>,
	crossinline onChange: (A?, B?) -> R
): LiveData<R> {
	val result = MediatorLiveData<R>()
	//add first source
	result.addSource(this) { result.value = onChange(this.value, otherLiveData.value) }
	
	//add second source
	result.addSource(otherLiveData) { result.value = onChange(this.value, otherLiveData.value) }
	return result
}


/*
 * observe value change only once
 */
fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
	observe(lifecycleOwner, object : Observer<T> {
		override fun onChanged(t: T?) {
			observer.onChanged(t)
			removeObserver(this)
		}
	})
}