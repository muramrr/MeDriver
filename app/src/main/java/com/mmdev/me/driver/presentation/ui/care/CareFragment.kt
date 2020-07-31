/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.07.20 20:38
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.care

import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.FragmentCareBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFragment

/**
 *
 */

class CareFragment : BaseFragment<CareViewModel, FragmentCareBinding>(
		layoutId = R.layout.fragment_care
) {

	override val viewModel: CareViewModel = CareViewModel()
	override fun setupViews() {
	}

	override fun renderState(state: ViewState) {

	}
}