/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.10.2020 19:00
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance.add.child

import android.annotation.SuppressLint
import android.os.Bundle
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.convertToLocalDateTime
import com.mmdev.me.driver.core.utils.currentTimeAndDate
import com.mmdev.me.driver.databinding.ItemMaintenanceChildEditBinding
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.presentation.core.base.BaseFragment
import com.mmdev.me.driver.presentation.ui.maintenance.add.MaintenanceAddViewModel
import com.mmdev.me.driver.presentation.utils.extensions.hideKeyboard
import com.mmdev.me.driver.presentation.utils.extensions.setupDatePicker
import com.mmdev.me.driver.presentation.utils.extensions.text
import kotlinx.datetime.LocalDateTime
import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 *
 */

class ChildEditFragment: BaseFragment<MaintenanceAddViewModel, ItemMaintenanceChildEditBinding>(
	R.layout.item_maintenance_child_edit
) {
	
	private var argPosition = 0
	private lateinit var child: Child
	
	private var pickedDate: LocalDateTime = currentTimeAndDate()
	
	companion object {
		private const val POSITION_KEY = "POSITION"
		
		fun newInstance(position: Int): ChildEditFragment =
			ChildEditFragment().apply {
				arguments = Bundle().also { it.putInt(POSITION_KEY, position) }
			}
	}
	
	override val mViewModel: MaintenanceAddViewModel by lazy { requireParentFragment().getViewModel() }
	
	private var lastReplacedEntry: VehicleSparePart? = null
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		argPosition = arguments?.getInt(POSITION_KEY) ?: 0
		findSelectedChildByPosition()
	}
	
	override fun setupViews() {
		
		binding.apply {
			root.setOnTouchListener { rootView, _ ->
				rootView.performClick()
				rootView.hideKeyboard(rootView)
			}
			
			fabChildAdd.setOnClickListener {
				mViewModel.addMaintenanceEntry(
					user = MedriverApp.currentUser,
					dateInput = pickedDate,
					vendorInput = etInputVendor.text(),
					articulusInput = etInputArticulus.text(),
					componentSelected = child.sparePart,
					customComponentInput =
					if (child.sparePart.getSparePartName() != SparePart.OTHER)
						child.sparePart.getSparePartName()
					else etInputCustomComponent.text(),
					commentaryInput = etInputCommentary.text(),
					priceInput = etInputPrice.text().toDouble(),
					odometerInput = etInputOdometer.text().toInt(),
					vin = MedriverApp.currentVehicleVinCode
				)
			}
		}
	}
	
	private fun findSelectedChildByPosition(){
		mViewModel.selectedChildren.observe(this, {
			it?.let {
				//if first selection was 3 and than u return and choose 2
				//operator fun get will throw indexOutOfRange exception
				if (argPosition < it.size) {
					child = it[argPosition]
					setupLastReplaced(child)
					setupFillForm(child)
				}
				
			}
		})
	}
	
	@SuppressLint("SetTextI18n")
	private fun setupLastReplaced(child: Child) {
		lastReplacedEntry = mViewModel.lastReplacedChildren[child.sparePart]
		
		lastReplacedEntry?.let {
			binding.tvLastReplacedDate.text = it.date.toString()
			
			if (it.vendor.isNotEmpty() || it.articulus.isNotEmpty())
				binding.tvLastReplacedDetail.text = "${it.vendor} ${it.articulus}"
		}
		
	}
 
	private fun setupFillForm(child: Child) {
		binding.tvComponentTitle.text = child.title
		binding.etInputCustomComponent.setText(child.title)
		
		binding.layoutInputCustomComponent.isEnabled = child.sparePart.getSparePartName() == SparePart.OTHER
		
		binding.btnChildDatePicker.btnDatePicker.setupDatePicker {
			pickedDate = convertToLocalDateTime(this.timeInMillis)
		}
	}
	
	
	
}