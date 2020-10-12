/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 12.10.2020 20:57
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance.add.child

import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.FragmentMaintenanceChildEditBinding
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.presentation.core.base.BaseFragment

/**
 *
 */

class EditChildFragment(
	private val item: Pair<String, SparePart>
): BaseFragment<Nothing, FragmentMaintenanceChildEditBinding>(
	R.layout.fragment_maintenance_child_edit
) {
	
	override val mViewModel = null
	
	override fun setupViews() {
		binding.tvComponentTitle.text = item.first
		binding.layoutInputCustomComponent.isEnabled = item.second.getSparePartName() == "OTHER"
	}
	
}