/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.07.20 21:06
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.home

import androidx.lifecycle.Observer
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.utils.logWtf
import com.mmdev.me.driver.databinding.FragmentHomeBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *
 */

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>(layoutId = R.layout.fragment_home) {

	override val viewModel: HomeViewModel by viewModel()
	override fun setupViews() {
		binding.pbOilUsageLeft.updateProgress(21f)
//		Log.wtf("mylogs", "${viewModel.vehicleByVIN}")
		viewModel.getVehicleByVIN("2S3DA417576128786")
		viewModel.vehicleByVIN.observe(this, Observer {
			when (it) {
				is ViewState.Success -> {
					logWtf(message = "${it.data}")
				}
				is ViewState.Loading -> {
					logWtf(message = "loading")

				}
				is ViewState.Error -> {
					logWtf(message = it.errorMessage)
				}
			}
			//renderState(it)
		})

	}


}