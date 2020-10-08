/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.10.2020 19:24
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.vehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.currentTimeAndDate
import com.mmdev.me.driver.databinding.BottomSheetVehicleAddBinding
import com.mmdev.me.driver.presentation.utils.extensions.hideKeyboard
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * [BottomSheetDialogFragment] used to add new vehicles
 */

class VehicleAddBottomSheet : BottomSheetDialogFragment() {
	
	private val dismissWithAnimation = true
	
	// prevent view being leaked
	private var _binding: BottomSheetVehicleAddBinding? = null
	private val binding: BottomSheetVehicleAddBinding
		get() = _binding ?: throw IllegalStateException(
			"Trying to access the binding outside of the view lifecycle."
		)
	
	private val mViewModel: VehicleViewModel by lazy { requireParentFragment().getViewModel() }
	
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View =
		BottomSheetVehicleAddBinding.inflate(inflater, container, false)
			.apply {
				_binding = this
				lifecycleOwner = viewLifecycleOwner
				viewModel = mViewModel
				executePendingBindings()
			}.root
	
	
	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		//dismissWithAnimation = arguments?.getBoolean(ARG_DISMISS_WITH_ANIMATION) ?: true
		(requireDialog() as BottomSheetDialog).dismissWithAnimation = dismissWithAnimation
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) { setupViews() }
	
	
	
	private fun setupViews() {
		
		observeInputVinCode()
		observeInputBrand()
		observeInputModel()
		observeInputYear()
		observeInputEngineCapacity()
		observeInputOdometer()
		
		
		binding.btnAdd.setOnClickListener {
			mViewModel.checkAndAdd(MedriverApp.currentUser)
			dialog?.dismiss()
		}
		binding.btnCancel.setOnClickListener { dialog?.dismiss() }
	}
	
	private fun observeInputVinCode() {
		mViewModel.vinCodeInput.observe(this, {
			if (it.isNullOrEmpty() || it.length != 17) {
				mViewModel.isVinCodeReady = false
				binding.layoutInputVin.error = getString(R.string.btm_sheet_vehicle_add_enter_vin_error)
			}
			else {
				binding.layoutInputVin.hideKeyboard(binding.layoutInputVin)
				binding.layoutInputVin.error = null
				mViewModel.isVinCodeReady = true
				mViewModel.getVehicleByVIN(it)
			}
		})
	}
	
	private fun observeInputBrand() {
		mViewModel.brandInput.observe(this, {
			if (it.isNullOrBlank()) {
				mViewModel.isBrandReady = false
				binding.layoutInputBrand.error = getString(R.string.btm_sheet_vehicle_add_enter_brand_error)
			}
			else {
				binding.layoutInputBrand.error = null
				mViewModel.isBrandReady = true
			}
		})
	}
	
	private fun observeInputModel() {
		mViewModel.modelInput.observe(this, {
			if (it.isNullOrBlank()) {
				mViewModel.isModelReady = false
				binding.layoutInputModel.error = getString(R.string.btm_sheet_vehicle_add_enter_model_error)
			}
			else {
				binding.layoutInputModel.error = null
				mViewModel.isModelReady = true
			}
		})
	}
	
	private fun observeInputYear() {
		mViewModel.yearInput.observe(this, {
			if (!it.isNullOrBlank() &&
			    it.toInt() > 1885 && it.toInt() < currentTimeAndDate().date.year) {
				binding.layoutInputYear.error = null
				mViewModel.isYearReady = true
			}
			else {
				mViewModel.isYearReady = false
				binding.layoutInputYear.error = getString(R.string.btm_sheet_vehicle_add_enter_year_error)
			}
		})
	}
	
	private fun observeInputEngineCapacity() {
		mViewModel.engineCapacityInput.observe(this, {
			if (!it.isNullOrBlank() && it.toDouble() < 30 && it.toDouble() > 0) {
				mViewModel.isEngineCapReady = true
				binding.layoutInputEngineCap.error = null
			}
			else {
				mViewModel.isEngineCapReady = false
				binding.layoutInputEngineCap.error =
					getString(R.string.btm_sheet_vehicle_add_enter_engine_cap_error)
			}
		})
	}
	
	private fun observeInputOdometer() {
		mViewModel.odometerInput.observe(this, {
			if (it.isNullOrBlank()) {
				mViewModel.isOdometerReady = false
				binding.layoutInputOdometer.error = getString(R.string.odometer_input_error)
			}
			else {
				binding.layoutInputOdometer.error = null
				mViewModel.isOdometerReady = true
			}
		})
	}
	
	override fun onDestroyView() {
		binding.unbind()
		_binding = null
		super.onDestroyView()
	}
}