/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 18.11.2020 17:34
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance.add.child

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.convertToLocalDateTime
import com.mmdev.me.driver.core.utils.currentTimeAndDate
import com.mmdev.me.driver.core.utils.helpers.LocaleHelper
import com.mmdev.me.driver.databinding.ItemMaintenanceChildEditBinding
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFragment
import com.mmdev.me.driver.presentation.ui.maintenance.add.MaintenanceAddViewModel
import com.mmdev.me.driver.presentation.ui.maintenance.add.MaintenanceAddViewState
import com.mmdev.me.driver.presentation.utils.extensions.domain.getOdometerFormatted
import com.mmdev.me.driver.presentation.utils.extensions.domain.getOdometerValue
import com.mmdev.me.driver.presentation.utils.extensions.hideKeyboard
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
import com.mmdev.me.driver.presentation.utils.extensions.setupDatePicker
import com.mmdev.me.driver.presentation.utils.extensions.showSnack
import com.mmdev.me.driver.presentation.utils.extensions.text
import kotlinx.datetime.LocalDateTime
import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 *
 */

class ChildEditFragment: BaseFragment<MaintenanceAddViewModel, ItemMaintenanceChildEditBinding>(
	R.layout.item_maintenance_child_edit
) {
	
	override val mViewModel: MaintenanceAddViewModel by lazy { requireParentFragment().getViewModel() }
	
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
	
	private var lastReplacedEntry: VehicleSparePart? = null
	
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		argPosition = arguments?.getInt(POSITION_KEY) ?: 0
		findSelectedChildByPosition()
	}
	
	override fun setupViews() {
		with(mViewModel.viewStateMap) {
			if (argPosition < size) {
				this[argPosition]!!.observe(this@ChildEditFragment, {
					renderState(it)
				})
			}
		}
		
		setupInputFields()
		
		binding.apply {
			root.setOnTouchListener { rootView, _ ->
				rootView.performClick()
				return@setOnTouchListener rootView.hideKeyboard(rootView)
			}
			
			fabChildAdd.setDebounceOnClick {
				
				if (checkAreInputCorrect())
					
					mViewModel.addMaintenanceEntry(
						position = argPosition,
						user = MedriverApp.currentUser,
						dateInput = pickedDate,
						vendorInput = etInputVendor.text(),
						articulusInput = etInputArticulus.text(),
						componentSelected = child.sparePart,
						searchCriteria = if (child.sparePart.getSparePartName() != SparePart.OTHER)
							LocaleHelper.getStringFromAllLocales(requireContext(), child.title).map { it.value }
						else listOf(etInputCustomComponent.text()),
						commentaryInput = etInputCommentary.text(),
						priceInput = etInputPrice.text().toDouble(),
						odometerInput = etInputOdometer.text().toInt(),
						vin = MedriverApp.currentVehicleVinCode
					)
			}
		}
	}
	
	private fun setupInputFields() {
		binding.etInputCustomComponent.doOnTextChanged { text, start, before, count ->
			if (text.isNullOrBlank()) binding.layoutInputCustomComponent.error = getString(R.string.input_empty_error)
			else binding.layoutInputCustomComponent.error = null
		}
		
		binding.etInputPrice.doOnTextChanged { text, start, before, count ->
			if (text.isNullOrBlank()) binding.layoutInputPrice.error = getString(R.string.input_empty_error)
			else binding.layoutInputPrice.error = null
		}
		
		binding.etInputOdometer.doOnTextChanged { text, start, before, count ->
			if (text.isNullOrBlank()) binding.layoutInputOdometer.error = getString(R.string.input_empty_error)
			else binding.layoutInputOdometer.error = null
		}
	}
	
	/** return true if all required fields have been inputted correctly  */
	private fun checkAreInputCorrect(): Boolean {
		if (binding.etInputCustomComponent.text.isNullOrBlank())
			binding.layoutInputCustomComponent.error = getString(R.string.input_empty_error)
		
		if (binding.etInputPrice.text.isNullOrBlank())
			binding.layoutInputPrice.error = getString(R.string.input_empty_error)
		
		if (binding.etInputOdometer.text.isNullOrBlank())
			binding.layoutInputOdometer.error = getString(R.string.input_empty_error)
			
		return !binding.etInputCustomComponent.text.isNullOrBlank() &&
		       !binding.etInputPrice.text.isNullOrBlank() &&
		       !binding.etInputOdometer.text.isNullOrBlank()
	}
	
	override fun renderState(state: ViewState) {
		when(state) {
			is MaintenanceAddViewState.Success -> {
				// if only one entry planned to add -> dismiss automatically dialog
				// else show successful snack message
				if (mViewModel.selectedChildren.value!!.size == 1) (requireParentFragment() as BottomSheetDialogFragment).dialog!!.dismiss()
				else {
					binding.root.rootView.showSnack(
						R.string.item_maintenance_add_operation_successful
					)
					binding.fabChildAdd.isEnabled = false
				}
				if (state.odometerBound.getOdometerValue() > MedriverApp.currentVehicle!!.odometerValueBound.getOdometerValue()) {
					//update vehicle with new odometer value
					sharedViewModel.updateVehicle(
						MedriverApp.currentUser,
						MedriverApp.currentVehicle!!.copy(odometerValueBound = state.odometerBound)
					)
				}
			}
			is MaintenanceAddViewState.Error -> {
				binding.root.rootView.showSnack(
					state.errorMessage ?:
					getString(R.string.item_maintenance_add_operation_successful)
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
	//todo: temporary fix backpressure
	private fun setupLastReplaced(child: Child) {
		lastReplacedEntry = mViewModel.lastReplacedChildren[child.sparePart]
		
		if (lastReplacedEntry != null) {
			
			binding.apply {
				binding.tvLastReplacedDate.text = lastReplacedEntry!!.date.date.toString()
				
				binding.tvLastReplacedOdometer.text = lastReplacedEntry!!.odometerValueBound.getOdometerFormatted(requireContext())
				
				if (lastReplacedEntry!!.vendor.isNotEmpty() || lastReplacedEntry!!.articulus.isNotEmpty())
					binding.tvLastReplacedDetail.text = "${lastReplacedEntry!!.vendor} " +
					                                    "${lastReplacedEntry!!.articulus}"
				else binding.tvLastReplacedDetail.text = getString(R.string.item_maintenance_not_defined_vendor_articulus)
			}
		}
		else {
			binding.tvLastReplacedOdometer.text = getString(R.string.default_OdometerValue)
			binding.tvLastReplacedDetail.text = getString(R.string.item_maintenance_not_defined_vendor_articulus)
			binding.tvLastReplacedDate.text = getString(R.string.item_maintenance_child_edit_last_replaced_value_never)
		}
		
	}
 
	private fun setupFillForm(child: Child) {
		binding.tvComponentTitle.text = getString(child.title)
		binding.etInputCustomComponent.setText(child.title)
		
		binding.layoutInputCustomComponent.isEnabled = child.sparePart.getSparePartName() == SparePart.OTHER
		
		binding.btnChildDatePicker.btnDatePicker.setupDatePicker {
			pickedDate = convertToLocalDateTime(this.timeInMillis)
		}
	}
	
	
	
}