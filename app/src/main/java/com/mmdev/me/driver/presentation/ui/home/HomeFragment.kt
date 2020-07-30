/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.07.20 18:40
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.home

import android.util.Log
import androidx.fragment.app.viewModels
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.FragmentHomeBinding
import com.mmdev.me.driver.presentation.ui.common.base.BaseFragment

/**
 *
 */

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>(layoutId = R.layout.fragment_home) {

	override val viewModel: HomeViewModel by viewModels()
	override fun setupViews() {
		binding.pbOilUsageLeft.updateProgress(21f)
		Log.wtf("mylogs", "${viewModel.vehicleByVIN}")
	}


}