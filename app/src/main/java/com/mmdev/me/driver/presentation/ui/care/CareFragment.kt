/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 29.07.20 21:02
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.care

import androidx.fragment.app.viewModels
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.FragmentCareBinding
import com.mmdev.me.driver.presentation.ui.common.base.BaseFragment

/**
 *
 */

class CareFragment : BaseFragment<CareViewModel, FragmentCareBinding>(
		layoutId = R.layout.fragment_care
) {

	override val viewModel: CareViewModel by viewModels()
	override fun setupViews() {
	}


}