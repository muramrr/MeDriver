/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.10.2020 19:24
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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