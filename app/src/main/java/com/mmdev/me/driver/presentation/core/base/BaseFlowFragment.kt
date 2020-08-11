/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.08.20 16:31
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.core.base

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding

/**
 * This is the documentation block about the class
 */

abstract class BaseFlowFragment<VM: BaseViewModel, Binding: ViewDataBinding>(
	@LayoutRes layoutId: Int
) : BaseFragment<VM, Binding>(layoutId) {
	
	override val mViewModel: VM? = null
	
}