/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 29.07.20 21:02
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.mycar

import androidx.fragment.app.viewModels
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.FragmentMycarBinding
import com.mmdev.me.driver.presentation.ui.common.base.BaseFragment

/**
 *
 */

class MyCarFragment : BaseFragment<MyCarViewModel, FragmentMycarBinding>(
		layoutId = R.layout.fragment_mycar
) {

	override val viewModel: MyCarViewModel by viewModels()
	override fun setupViews() {
	}


}