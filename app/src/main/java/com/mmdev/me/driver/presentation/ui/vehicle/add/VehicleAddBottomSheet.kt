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

package com.mmdev.me.driver.presentation.ui.vehicle.add

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.utils.extensions.currentTimeAndDate
import com.mmdev.me.driver.databinding.BtmSheetVehicleAddBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseBottomSheetFragment
import com.mmdev.me.driver.presentation.ui.MainActivity
import com.mmdev.me.driver.presentation.ui.vehicle.VehicleConstants
import com.mmdev.me.driver.presentation.ui.vehicle.VehicleViewModel
import com.mmdev.me.driver.presentation.utils.extensions.hideKeyboard
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
import com.mmdev.me.driver.presentation.utils.extensions.showSnack
import com.mmdev.me.driver.presentation.utils.extensions.visibleIf
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * [BottomSheetDialogFragment] used to add new vehicles
 */

class VehicleAddBottomSheet: BaseBottomSheetFragment<VehicleAddViewModel, BtmSheetVehicleAddBinding>(
	layoutId = R.layout.btm_sheet_vehicle_add
) {
	
	override val mViewModel: VehicleAddViewModel by viewModel()
	private val parentViewModel: VehicleViewModel by lazy { requireParentFragment().getViewModel() }
	
	
	private var vinInputError = ""
	private var yearInputError = ""
	private var engineInputError = ""
	private var odometerInputError = ""
	private var emptyError = ""
	
	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		//dismissWithAnimation = arguments?.getBoolean(ARG_DISMISS_WITH_ANIMATION) ?: true
		(requireDialog() as BottomSheetDialog).apply {
			dismissWithAnimation = true
		}
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel.viewState.observe(this, { renderState(it) })
	}
	
	override fun renderState(state: ViewState) {
		binding.viewLoading.visibleIf(otherwise = View.INVISIBLE) {
			state == VehicleAddViewState.Loading
		}
		
		when (state) {
			is VehicleAddViewState.Success -> {
				parentViewModel.shouldBeUpdated.postValue(true)
				dismiss()
			}
			is VehicleAddViewState.Error -> {
				binding.root.rootView.showSnack(
					state.errorMessage ?: "Unexpected error",
					Snackbar.LENGTH_LONG
				)
			}
		}
		
	}
	
	override fun setupViews() {
		initStringRes()
		setupInputBrandDropList()
		setupInputFields()
		
		binding.apply {
			root.setOnTouchListener { rootView, _ ->
				rootView.performClick()
				rootView.hideKeyboard(rootView)
			}
			
			btnAdd.setDebounceOnClick {
				if (checkAreInputCorrect()) mViewModel.checkAndAdd(MainActivity.currentUser)
			}
			
			btnCancel.setOnClickListener { dismiss() }
		}
	}
	
	private fun initStringRes() {
		vinInputError = getString(R.string.btm_sheet_vehicle_add_enter_vin_error)
		yearInputError = getString(R.string.btm_sheet_vehicle_add_enter_year_error)
		engineInputError = getString(R.string.btm_sheet_vehicle_add_enter_engine_cap_error)
		odometerInputError = getString(R.string.odometer_input_error)
		emptyError = getString(R.string.input_empty_error)
	}
	
	private fun setupInputBrandDropList() {
		val brandsAdapter = ArrayAdapter(
			requireContext(),
			R.layout.item_drop_single_text,
			VehicleConstants.vehicleBrands
		)
		// drop down fuel station chooser
		binding.etInputBrand.apply {
			
			setAdapter(brandsAdapter)
			threshold = 1
			
			setOnItemClickListener { _, _, position, _ ->
				
				//cast to global variable current selected station
				mViewModel.brandInput.value = brandsAdapter.getItem(position)
				hideKeyboard(this@apply)
			}
		}
	}
	
	private fun setupInputFields() {
		binding.etInputVin.doOnTextChanged { text, start, before, count ->
			if (text.isNullOrBlank() || text.length != 17) binding.layoutInputVin.error = vinInputError
			else {
				binding.layoutInputVin.error = null
				binding.layoutInputVin.hideKeyboard(binding.layoutInputVin)
				mViewModel.getVehicleByVIN(text.toString())
			}
		}
		
		binding.etInputBrand.doOnTextChanged { text, start, before, count ->
			if (text.isNullOrBlank()) binding.layoutInputBrand.error = emptyError
			else binding.layoutInputBrand.error = null
		}
		
		binding.etInputModel.doOnTextChanged { text, start, before, count ->
			if (text.isNullOrBlank()) binding.layoutInputModel.error = emptyError
			else binding.layoutInputModel.error = null
		}
		
		binding.etInputYear.doOnTextChanged { text, start, before, count ->
			if (!text.isNullOrBlank() &&
			    text.toString().toInt() > 1885 &&
			    text.toString().toInt() < currentTimeAndDate().date.year)
				binding.layoutInputYear.error = null
			else binding.layoutInputYear.error = yearInputError
		}
		
		binding.etInputEngineCap.doOnTextChanged { text, start, before, count ->
			if (!text.isNullOrBlank() && text.toString().toDouble() < 100 && text.toString().toDouble() > 0)
				binding.layoutInputEngineCap.error = null
			else binding.layoutInputEngineCap.error = engineInputError
		}
		
		binding.etInputOdometer.doOnTextChanged { text, start, before, count ->
			if (text.isNullOrBlank()) binding.layoutInputOdometer.error = odometerInputError
			else binding.layoutInputOdometer.error = null
		}
	}
	
	/** return true if all required fields have been inputted correctly  */
	private fun checkAreInputCorrect(): Boolean {
		if (binding.etInputVin.text.isNullOrBlank())
			binding.layoutInputVin.error = vinInputError
		
		if (binding.etInputBrand.text.isNullOrBlank())
			binding.layoutInputBrand.error = emptyError
		
		if (binding.etInputModel.text.isNullOrBlank())
			binding.layoutInputModel.error = emptyError
		
		if (binding.etInputYear.text.isNullOrBlank())
			binding.layoutInputYear.error = yearInputError
		
		if (binding.etInputEngineCap.text.isNullOrBlank())
			binding.layoutInputEngineCap.error = engineInputError
		
		if (binding.etInputOdometer.text.isNullOrBlank())
			binding.layoutInputOdometer.error = getString(R.string.input_empty_error)
		
		return (!binding.etInputVin.text.isNullOrBlank() || binding.layoutInputVin.error == null) &&
		       (!binding.etInputBrand.text.isNullOrBlank() || binding.layoutInputBrand.error == null) &&
		       (!binding.etInputModel.text.isNullOrBlank() || binding.layoutInputModel.error == null) &&
		       (!binding.etInputYear.text.isNullOrBlank() || binding.layoutInputYear.error == null) &&
		       (!binding.etInputEngineCap.text.isNullOrBlank() || binding.layoutInputEngineCap.error == null) &&
		       (!binding.etInputOdometer.text.isNullOrBlank() || binding.layoutInputOdometer.error == null)
	}
	
}