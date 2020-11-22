/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 16:17
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.home

import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.FragmentHomeBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *
 */

class HomeFragment : BaseFlowFragment<HomeViewModel, FragmentHomeBinding>(
	layoutId = R.layout.fragment_home
) {

	override val mViewModel: HomeViewModel by viewModel()
	

	override fun setupViews() {
		binding.btnCrash.setOnClickListener {
			throw RuntimeException("Test Crash") // Force a crash
		}
		//SubscriptionBottomSheet().show(childFragmentManager, SubscriptionBottomSheet::class.java.canonicalName)

	}

	override fun renderState(state: ViewState) {
	
	}


}