/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.10.2020 19:43
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance

import android.os.Bundle
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.DialogMaintenanceAddBinding
import com.mmdev.me.driver.presentation.core.base.BaseDialogFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 *
 */

class MaintenanceAddDialog: BaseDialogFragment<MaintenanceViewModel, DialogMaintenanceAddBinding>(
	layoutId = R.layout.dialog_maintenance_add
)  {
	
	override val mViewModel: MaintenanceViewModel by lazy { requireParentFragment().getViewModel() }
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(STYLE_NORMAL, R.style.My_Dialog_FullScreen)
	}
	
	
	
	override fun setupViews() {
	
	}
	
}