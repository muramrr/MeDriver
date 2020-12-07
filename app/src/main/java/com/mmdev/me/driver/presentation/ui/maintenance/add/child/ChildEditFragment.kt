/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. medriver
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.me.driver.presentation.ui.maintenance.add.child

import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.extensions.convertToLocalDateTime
import com.mmdev.me.driver.core.utils.extensions.currentTimeAndDate
import com.mmdev.me.driver.core.utils.helpers.LocaleHelper
import com.mmdev.me.driver.databinding.ItemMaintenanceChildEditBinding
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFragment
import com.mmdev.me.driver.presentation.ui.MainActivity
import com.mmdev.me.driver.presentation.ui.maintenance.add.MaintenanceAddViewModel
import com.mmdev.me.driver.presentation.utils.extensions.domain.getOdometerFormatted
import com.mmdev.me.driver.presentation.utils.extensions.domain.getValue
import com.mmdev.me.driver.presentation.utils.extensions.hideKeyboard
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
import com.mmdev.me.driver.presentation.utils.extensions.setupDatePicker
import com.mmdev.me.driver.presentation.utils.extensions.showSnack
import com.mmdev.me.driver.presentation.utils.extensions.text
import kotlinx.datetime.LocalDateTime
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 *
 */

class ChildEditFragment: BaseFragment<ChildEditViewModel, ItemMaintenanceChildEditBinding>(
	R.layout.item_maintenance_child_edit
) {
	private val parentViewModel: MaintenanceAddViewModel by lazy { requireParentFragment().getViewModel() }
	override val mViewModel: ChildEditViewModel by viewModel()
	
	private lateinit var child: Child
	
	private var pickedDate: LocalDateTime = currentTimeAndDate()
	
	companion object {
		private const val POSITION_KEY = "position"
		
		fun newInstance(position: Int): ChildEditFragment = ChildEditFragment().apply {
			arguments = Bundle().also { it.putInt(POSITION_KEY, position) }
		}
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel.viewState.observe(this, { renderState(it) })
		findSelectedChildByPosition(arguments?.getInt(POSITION_KEY) ?: 0)
	}
	
	override fun renderState(state: ViewState) {
		when(state) {
			is ChildEditViewState.Success -> {
				parentViewModel.parentShouldBeUpdated.postValue(true)
				// if only one entry planned to add -> dismiss automatically dialog
				// else show successful snack message
				if (parentViewModel.selectedChildren.value!!.size == 1) {
					(requireParentFragment() as BottomSheetDialogFragment).dialog!!.dismiss()
				}
				else {
					binding.root.rootView.showSnack(
						R.string.item_maintenance_add_operation_successful
					)
					binding.fabChildAdd.isEnabled = false
				}
				if (state.data.odometerValueBound.getValue() > MainActivity.currentVehicle!!.odometerValueBound.getValue()) {
					//update vehicle with new odometer value
					sharedViewModel.updateVehicle(
						MainActivity.currentUser,
						MainActivity.currentVehicle!!.copy(
							odometerValueBound = state.data.odometerValueBound,
							lastUpdatedDate = state.data.dateAdded
						)
					)
				}
			}
			is ChildEditViewState.Error -> {
				binding.root.rootView.showSnack(
					state.errorMessage ?:
					getString(R.string.item_maintenance_add_operation_error)
				)
			}
		}
	}
	
	override fun setupViews() {
		observeLastReplaced()
		setupInputFields()
		
		binding.apply {
			root.setOnTouchListener { rootView, _ ->
				rootView.performClick()
				return@setOnTouchListener rootView.hideKeyboard(rootView)
			}
			
			fabChildAdd.setDebounceOnClick {
				
				if (checkAreInputCorrect())
					
					mViewModel.addMaintenanceEntry(
						parent = parentViewModel.selectedVehicleSystemNode.value!!,
						user = MainActivity.currentUser,
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
	
	
	
	private fun findSelectedChildByPosition(position: Int){
		parentViewModel.selectedChildren.observe(this, {
			it?.let {
				//if first selection was 3 and than u return and choose 2
				//operator fun get will throw indexOutOfRange exception
				if (position < it.size) {
					child = it[position]
					setupFillForm(child)
					mViewModel.loadLastTimeSparePartReplaced(
						parentViewModel.selectedVehicleSystemNode.value!!,
						child
					)
				}
			}
		})
	}
	
	private fun observeLastReplaced() {
		mViewModel.lastReplacedChild.observe(this, {
			
			binding.tvLastReplacedOdometer.text = it?.odometerValueBound?.getOdometerFormatted(requireContext())
			if (!it?.vendor.isNullOrBlank() || !it?.articulus.isNullOrBlank())
				binding.tvLastReplacedDetail.text = getString(
					R.string.two_strings_whitespace_formatter,
					it?.vendor,
					it?.articulus
				)
			else binding.tvLastReplacedDetail.text = getString(R.string.undefined)
			
		})
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