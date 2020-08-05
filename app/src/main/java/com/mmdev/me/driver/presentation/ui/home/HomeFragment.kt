/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.08.20 16:15
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
import com.mmdev.me.driver.presentation.ui.home.HomeViewModel.VINViewState
import com.mmdev.me.driver.presentation.utils.setDebounceClick
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.random.Random

/**
 *
 */

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>(layoutId = R.layout.fragment_home) {

	override val mViewModel: HomeViewModel by viewModel()

//	private val viewModel by lazy {
//		requireParentFragment().getViewModel<MyViewModel>()
//	}

	override fun setupViews() {

		//viewModel.getVehicleByVIN("WF0FXXWPDF3K73412")
		
		mViewModel.vehicleByVIN.observe(this, Observer {
			renderState(it)
		})

		binding.pbOilUsageLeft.setDebounceClick (1000L) {
			updateProgress(Random.nextInt(0, 100).toFloat())
		}
	}

	override fun renderState(state: ViewState) {
		when (state) {
			is VINViewState.Success -> {
				logWtf(message = "${state.data}")
			}
			is VINViewState.Loading -> {
				logWtf(message = "loading")

			}
			is VINViewState.Error -> {
				logWtf(message = state.errorMessage)
			}
		}

	}


}