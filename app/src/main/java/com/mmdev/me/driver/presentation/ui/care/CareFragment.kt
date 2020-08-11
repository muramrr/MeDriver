/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.08.20 15:50
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.care

import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.FragmentCareBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *
 */

class CareFragment : BaseFlowFragment<CareViewModel, FragmentCareBinding>(
		layoutId = R.layout.fragment_care
) {

	override val mViewModel: CareViewModel by viewModel()
	override fun setupViews() {
	}

	override fun renderState(state: ViewState) {

	}
}