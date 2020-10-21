/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.10.2020 19:28
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance.add.child

import android.annotation.SuppressLint
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.utils.log.logWtf
import com.mmdev.me.driver.databinding.ItemMaintenanceChildEditBinding
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.presentation.core.base.BaseFragment
import com.mmdev.me.driver.presentation.ui.maintenance.add.MaintenanceAddViewModel
import com.mmdev.me.driver.presentation.utils.extensions.hideKeyboard
import com.mmdev.me.driver.presentation.utils.extensions.setupDatePicker
import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 *
 */

class ChildEditFragment(
	private val child: Child
): BaseFragment<MaintenanceAddViewModel, ItemMaintenanceChildEditBinding>(
	R.layout.item_maintenance_child_edit
) {
	
	override val mViewModel: MaintenanceAddViewModel by lazy { requireParentFragment().getViewModel() }
	
	private var lastReplacedEntry: VehicleSparePart? = null
	
	override fun setupViews() {
		
		logWtf(TAG, "$child")
		
		binding.root.setOnTouchListener { rootView, _ ->
			rootView.performClick()
			rootView.hideKeyboard(rootView)
		}
		
		setupLastReplaced()
		setupFillForm()
	}
	
	@SuppressLint("SetTextI18n")
	private fun setupLastReplaced() {
		lastReplacedEntry = mViewModel.lastReplacedChildren[child.sparePart]
		
		lastReplacedEntry?.let {
			binding.tvLastReplacedDate.text = it.date.toString()
			
			if (it.vendor.isNotEmpty() || it.articulus.isNotEmpty())
				binding.tvLastReplacedDetail.text = "${it.vendor} ${it.articulus}"
		}
		
	}
 
	private fun setupFillForm() {
		binding.tvComponentTitle.text = child.title
		binding.etInputCustomComponent.setText(child.title)
		
		binding.layoutInputCustomComponent.isEnabled = child.sparePart.getSparePartName() == SparePart.OTHER
		
		binding.btnChildDatePicker.btnDatePicker.setupDatePicker {}
	}
	
}