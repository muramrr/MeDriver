/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 23.09.2020 19:17
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
import com.mmdev.me.driver.core.utils.MetricSystem
import com.mmdev.me.driver.databinding.BottomSheetVehicleAddBinding
import com.mmdev.me.driver.presentation.utils.hideKeyboard
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
		initStringRes()
		
		observeVinCode()
		observeYear()
		observeEngineCapacity()
		observeInputOdometer()
		
		binding.btnCancel.setOnClickListener { dialog?.dismiss() }
	}
	
	private fun initStringRes() {
		binding.apply {
			//Show strings according to current metric system
			when (MedriverApp.metricSystem) {
				
				// #################################### KILOMETERS ############################################# //
				MetricSystem.KILOMETERS -> {
					
					layoutInputOdometer.suffixText = getString(R.string.kilometers)
					
				}
				
				// ###################################### MILES ############################################## //
				MetricSystem.MILES -> {
					
					layoutInputOdometer.suffixText = getString(R.string.miles)
					
				}
			}
		}
	}
	
	private fun observeVinCode() {
		mViewModel.vinCodeInput.observe(this, {
			if (it.isNullOrEmpty() || it.length != 17)
				binding.layoutInputVin.error = getString(R.string.fg_vehicle_add_enter_vin_error)
			else {
				binding.layoutInputVin.hideKeyboard(binding.layoutInputVin)
				binding.layoutInputVin.error = null
				mViewModel.getVehicleByVIN(it)
			}
		})
	}
	
	private fun observeYear() {
		mViewModel.yearInput.observe(this, {
			if (!it.isNullOrBlank() && it.toInt() < 1885)
				binding.layoutInputYear.error = getString(R.string.fg_vehicle_add_enter_year_error)
			else binding.layoutInputYear.error = null
		})
	}
	
	private fun observeEngineCapacity() {
		mViewModel.engineCapacityInput.observe(this, {
			if (!it.isNullOrBlank())
				if (it.toDouble() > 30 || it.toDouble() <= 0)
					binding.layoutInputEngineCap.error =
						getString(R.string.fg_vehicle_add_enter_engine_cap_error)
			else binding.layoutInputEngineCap.error = null
		})
	}
	
	private fun observeInputOdometer() {
		mViewModel.odometerInput.observe(this, {
			if (it.isNullOrBlank())
				binding.layoutInputOdometer.error = getString(R.string.odometer_input_error)
			else binding.layoutInputOdometer.error = null
		})
	}
	
	override fun onDestroyView() {
		binding.unbind()
		_binding = null
		super.onDestroyView()
	}
}