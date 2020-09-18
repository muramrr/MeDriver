/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 18.09.2020 16:22
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.common

import android.content.Context
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes

/**
 * Generic adapter for Drop Down Lists used in AutoCompleteTextViews
 */

abstract class DropAdapter<T>(
	context: Context,
	@LayoutRes private val layoutId: Int,
	private val data: List<T>
): ArrayAdapter<T>(context, layoutId, data) {
	
	override fun getItem(position: Int): T = data[position]
	
	override fun getCount(): Int = data.size
}