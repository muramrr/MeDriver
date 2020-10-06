/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 06.10.2020 19:20
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mmdev.me.driver.databinding.BottomSheetMaintenanceAddBinding
import com.mmdev.me.driver.domain.maintenance.data.components.VehicleSystemNodeType.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 *
 */

class MaintenanceAddBottomSheet: BottomSheetDialogFragment() {
	
	private val dismissWithAnimationBool = true
	
	//automatically dismiss to prevent half expanded state
	private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
		
		override fun onStateChanged(bottomSheet: View, newState: Int) {
			if (newState !in arrayOf(BottomSheetBehavior.STATE_EXPANDED,
			                         BottomSheetBehavior.STATE_DRAGGING,
			                         BottomSheetBehavior.STATE_SETTLING))
				dismiss()
		}
		
		override fun onSlide(bottomSheet: View, slideOffset: Float) {
			// Do something for slide offset
		}
	}
	
	// prevent view being leaked
	private var _binding: BottomSheetMaintenanceAddBinding? = null
	private val binding: BottomSheetMaintenanceAddBinding
	get() = _binding ?: throw IllegalStateException(
		"Trying to access the binding outside of the view lifecycle."
	)
	
	private val mViewModel: MaintenanceViewModel by lazy { requireParentFragment().getViewModel() }
	
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View =
		BottomSheetMaintenanceAddBinding.inflate(inflater, container, false)
			.apply {
				_binding = this
				lifecycleOwner = viewLifecycleOwner
				viewModel = mViewModel
				executePendingBindings()
			}.root
	
	
	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		//dismissWithAnimation = arguments?.getBoolean(ARG_DISMISS_WITH_ANIMATION) ?: true
		(requireDialog() as BottomSheetDialog).apply {
			dismissWithAnimation = dismissWithAnimationBool
			behavior.state = BottomSheetBehavior.STATE_EXPANDED
			behavior.addBottomSheetCallback(bottomSheetCallback)
		}
	}
	
	override fun onStart() {
		super.onStart()
		val sheetContainer = requireView().parent as? ViewGroup ?: return
		sheetContainer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
	}

	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		observeSelectedNode()
		
		setupViews()
	}
	
	private fun setupViews() {
		binding.apply {
			
			cvEngineNode.setOnClickListener {
				mViewModel.selectedVehicleSystemNode.postValue(ENGINE)
			}
			cvTransmissionNode.setOnClickListener {
				mViewModel.selectedVehicleSystemNode.postValue(TRANSMISSION)
			}
			cvElectronicsNode.setOnClickListener {
				mViewModel.selectedVehicleSystemNode.postValue(ELECTRICS)
			}
			cvSuspensionNode.setOnClickListener {
				mViewModel.selectedVehicleSystemNode.postValue(SUSPENSION)
			}
			cvBrakesNode.setOnClickListener {
				mViewModel.selectedVehicleSystemNode.postValue(BRAKES)
			}
			cvBodyNode.setOnClickListener {
				mViewModel.selectedVehicleSystemNode.postValue(BODY)
			}
			cvOtherNode.setOnClickListener {
				mViewModel.selectedVehicleSystemNode.postValue(OTHERS)
			}
			cvPlannedNode.setOnClickListener {
				mViewModel.selectedVehicleSystemNode.postValue(PLANNED)
			}
		}
	}
	
	private fun observeSelectedNode() {
		mViewModel.selectedVehicleSystemNode.observe(this, {
		
		})
	}
	
	override fun onDestroyView() {
		binding.unbind()
		_binding = null
		super.onDestroyView()
	}
	
}