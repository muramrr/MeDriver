/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.10.2020 19:47
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.utils.log.logWtf
import com.mmdev.me.driver.databinding.BottomSheetMaintenanceAddBinding
import com.mmdev.me.driver.domain.maintenance.data.components.OtherParts
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType.*
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType.Companion.getChildren
import com.mmdev.me.driver.presentation.ui.maintenance.MaintenanceViewModel
import com.mmdev.me.driver.presentation.utils.extensions.hideKeyboard
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
import com.mmdev.me.driver.presentation.utils.extensions.text
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 *
 */

class MaintenanceAddBottomSheet: BottomSheetDialogFragment() {
	private val TAG = "mylogs_${javaClass.simpleName}"
	
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
	
	private var parentNode = ""
	private var childComponentLocalized = ""
	
	
	private var childComponentsAdapter = ChildComponentsAdapter(emptyList())
	
	private var multiSelectButtonChooseText = ""
	private var multiSelectButtonClearText = ""
	
	private var engineComponents: List<String> = emptyList()
	private var transmissionComponents: List<String> = emptyList()
	private var electricComponents: List<String> = emptyList()
	private var suspensionComponents: List<String> = emptyList()
	private var brakesComponents: List<String> = emptyList()
	private var bodyComponents: List<String> = emptyList()
	private var otherComponents: List<String> = emptyList()
	private var plannedComponents: List<String> = emptyList()
	
	
	private var btnNextFromMultiSelectStartPos = 0f
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View =
		BottomSheetMaintenanceAddBinding.inflate(inflater, container, false)
			.apply {
				_binding = this
				lifecycleOwner = viewLifecycleOwner
				viewModel = mViewModel
				executePendingBindings()
			}.root
	
	
    //attach callback, force dismiss with animation, set state
	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		//dismissWithAnimation = arguments?.getBoolean(ARG_DISMISS_WITH_ANIMATION) ?: true
		(requireDialog() as BottomSheetDialog).apply {
			dismissWithAnimation = dismissWithAnimationBool
			behavior.state = BottomSheetBehavior.STATE_EXPANDED
			behavior.addBottomSheetCallback(bottomSheetCallback)
		}
	}
	
	//make bottomSheet fit all screen height
	override fun onStart() {
		super.onStart()
		val sheetContainer = requireView().parent as? ViewGroup ?: return
		sheetContainer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
	}

	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		initStringRes()
		
		observeSelectedNode()
		observeSelectedChildComponent()
		
		setupViews()
	}
	
	private fun initStringRes() {
		multiSelectButtonChooseText = getString(R.string.btm_sheet_maintenance_btn_multi_select_choose)
		multiSelectButtonClearText = getString(R.string.btm_sheet_maintenance_btn_multi_select_clear)
		
		engineComponents = resources.getStringArray(R.array.fg_maintenance_engine_components).toList()
		transmissionComponents = resources.getStringArray(R.array.fg_maintenance_transmission_components).toList()
		electricComponents = resources.getStringArray(R.array.fg_maintenance_electric_components).toList()
		suspensionComponents = resources.getStringArray(R.array.fg_maintenance_suspension_components).toList()
		brakesComponents = resources.getStringArray(R.array.fg_maintenance_brakes_components).toList()
		bodyComponents = resources.getStringArray(R.array.fg_maintenance_body_components).toList()
		plannedComponents = resources.getStringArray(R.array.fg_maintenance_planned_components).toList()
	}
	
	private fun setupViews() {
		binding.apply {
			
			motionMaintenance.addTransitionListener(object : MotionLayout.TransitionListener {
				override fun onTransitionStarted(container: MotionLayout, start: Int, end: Int) {}
				
				override fun onTransitionChange(container: MotionLayout, start: Int, end: Int, pos: Float) {}
				
				override fun onTransitionCompleted(container: MotionLayout, currentId: Int) {
					(requireDialog() as BottomSheetDialog).behavior.isDraggable = currentId != R.id.childPickSet
					if (currentId == R.id.parentPickSet) childComponentsAdapter.turnOffMultiSelect()
				}
				
				override fun onTransitionTrigger(
					container: MotionLayout, triggerId: Int, positive: Boolean, progress: Float
				) {}
			})
			
			
			btnBack.setDebounceOnClick(500) {
				//transition to start motion state and the next click dismisses dialog
				when (motionMaintenance.currentState) {
					R.id.parentPickSet -> dismissAllowingStateLoss()
					R.id.childPickSet -> motionMaintenance.transitionToState(R.id.parentPickSet)
					R.id.formSet -> motionMaintenance.transitionToStart()
					
				}
				hideKeyboard(rootView)
			}
			
			btnMultiSelect.setDebounceOnClick(500) {
				childComponentsAdapter.toggleMultiSelect()
			}
			
			btnNextFromMultiSelectStartPos = btnNextFromMultiSelect.translationY
			btnNextFromMultiSelect.setOnClickListener {
				motionMaintenance.transitionToState(R.id.formSet)
			}
			
		}
		
		
		setupChildPickSet()
		setupFormSet()
	}
	
	
	private fun setupChildPickSet() {
		
		childComponentsAdapter.multiSelectState.observe(this, {
			binding.btnMultiSelect.text = if (it) multiSelectButtonClearText
			else  multiSelectButtonChooseText
			
			binding.btnNextFromMultiSelect.animate().apply {
				translationY(
					if (it) 0f else btnNextFromMultiSelectStartPos
				)
				interpolator = OvershootInterpolator()
				duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
			}
			
		})
		
		binding.apply {
			
			childComponentsAdapter.setOnItemClickListener { view, position, item ->
				childComponentSelected(
					mViewModel.selectedVehicleSystemNode.value!!.getChildren()[position], item
				)
			}
			
			rvNodeChildComponents.apply {
				adapter = childComponentsAdapter
				layoutManager = LinearLayoutManager(requireContext())
				setHasFixedSize(true)
			}
		}
	}
	
	private fun setupFormSet() {
	
	}
	
	private fun observeSelectedNode() {
		mViewModel.selectedVehicleSystemNode.observe(this, {
			
			logWtf(TAG, "parent " + it?.name)
			
			when (it) {
				ENGINE -> {
					parentNodeSelected(binding.tvEngineNodeTitle.text.toString(), engineComponents)
				}
				TRANSMISSION -> {
					parentNodeSelected(binding.tvTransmissionNodeTitle.text(), transmissionComponents)
				}
				ELECTRICS -> {
					parentNodeSelected(binding.tvElectricsNodeTitle.text(), electricComponents)
				}
				SUSPENSION -> {
					parentNodeSelected(binding.tvSuspensionNodeTitle.text(), suspensionComponents)
				}
				BRAKES -> { parentNodeSelected(binding.tvBrakesNodeTitle.text(), brakesComponents) }
				BODY -> { parentNodeSelected(binding.tvBodyNodeTitle.text(), bodyComponents) }
				OTHERS -> { parentNodeSelected(binding.tvOtherNodeTitle.text(), otherComponents) }
				PLANNED -> { parentNodeSelected(binding.tvPlannedNodeTitle.text(), plannedComponents) }
				else -> {}
			}
		})
	}
	
	private fun observeSelectedChildComponent() {
		mViewModel.selectedChildComponent.observe(this, {
			
			logWtf(TAG, "child = " + it?.getSparePartName())
			
		})
	}
	
	private fun parentNodeSelected(title: String, data: List<String>) {
		parentNode = title
		binding.tvToolbarTitle.text = parentNode
		childComponentsAdapter.setNewData(data)
		
		/** empty list contains only [OTHERS] system node */
		if (data.isNotEmpty()) binding.motionMaintenance.transitionToState(R.id.childPickSet)
		else childComponentSelected(OtherParts.OTHER, title)
	}
	
	private fun childComponentSelected(component: SparePart, title: String) {
		binding.motionMaintenance.transitionToState(R.id.formSet)
		mViewModel.selectedChildComponent.postValue(component)
		childComponentLocalized = title
	}
	
	
	override fun onDestroyView() {
		binding.unbind()
		_binding = null
		super.onDestroyView()
	}
	
	
}