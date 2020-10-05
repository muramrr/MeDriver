/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.10.2020 19:41
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance

import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.utils.currentTimeAndDate
import com.mmdev.me.driver.core.utils.log.logWtf
import com.mmdev.me.driver.databinding.FragmentMaintenanceBinding
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.maintenance.data.components.VehicleSystemNodeType
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *
 */

class MaintenanceFragment : BaseFlowFragment<MaintenanceViewModel, FragmentMaintenanceBinding>(
	layoutId = R.layout.fragment_maintenance
) {

	override val mViewModel: MaintenanceViewModel by viewModel()
	
	override fun setupViews() {
		
		binding.fabAddMaintenance.setOnClickListener {
			MaintenanceAddDialog()
				.show(childFragmentManager, MaintenanceAddDialog::class.java.canonicalName)
		}
		
		val a = VehicleSparePart(
			systemNode = VehicleSystemNodeType.ENGINE,
			systemNodeComponent = "Cylinder head bolt",
			vehicleVinCode = "",
			date = currentTimeAndDate()
		)
		
		val b = a.systemNode
		val c = a.systemNodeComponent
		logWtf(TAG, "node = $b")
		logWtf(TAG, "part = $c.")
	}

	override fun renderState(state: ViewState) {

	}
}