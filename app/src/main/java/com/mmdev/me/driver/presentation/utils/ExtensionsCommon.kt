package com.mmdev.me.driver.presentation.utils

import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * This is the documentation block about the class
 */

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
	observe(lifecycleOwner, object : Observer<T> {
		override fun onChanged(t: T?) {
			observer.onChanged(t)
			removeObserver(this)
		}
	})
}

/**
 * Extension method to provide simpler access to {@link View#getResources()#getString(int)}.
 */
fun View.getStringRes(@StringRes stringRes: Int): String = resources.getString(stringRes)


inline fun <T: View> T.setDebounceClick(debounceTime: Long = 1000L,
                                        crossinline block: T.() -> Unit) =
	setOnClickListener {
		when {
			tag != null && (tag as Long) > System.currentTimeMillis() -> return@setOnClickListener
			else -> {
				tag = System.currentTimeMillis() + debounceTime
				block()
			}
		}
	}

inline fun <T: View> T.setSingleClick(crossinline block: T.() -> Unit) = setOnClickListener {
	block()
	it.isClickable = false
}