/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 13.09.2020 00:30
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
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import com.mmdev.me.driver.presentation.ui.common.LoadingStatus
import com.mmdev.me.driver.presentation.ui.home.HomeViewModel.VINViewState
import com.mmdev.me.driver.presentation.utils.setDebounceOnClick
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.random.Random

/**
 *
 */

class HomeFragment : BaseFlowFragment<HomeViewModel, FragmentHomeBinding>(layoutId = R.layout.fragment_home) {

	override val mViewModel: HomeViewModel by viewModel()

//	private val viewModel by lazy {
//		requireParentFragment().getViewModel<MyViewModel>()
//	}

	override fun setupViews() {

		//viewModel.getVehicleByVIN("WF0FXXWPDF3K73412")
		
		mViewModel.vehicleByVIN.observe(this, Observer {
			renderState(it)
		})

		binding.pbOilUsageLeft.setDebounceOnClick (1000L) {
			updateProgress(Random.nextInt(0, 100).toFloat())
		}
	}

	override fun renderState(state: ViewState) {
		when (state) {
			is VINViewState.Success -> {
				logWtf(message = "${state.data}")
				sharedViewModel.showLoading.value = LoadingStatus.HIDE
			}
			is VINViewState.Loading -> {
				logWtf(message = "loading")
				sharedViewModel.showLoading.value = LoadingStatus.SHOW
			}
			is VINViewState.Error -> {
				logWtf(message = state.errorMessage)
				sharedViewModel.showLoading.value = LoadingStatus.HIDE
			}
		}

	}


}