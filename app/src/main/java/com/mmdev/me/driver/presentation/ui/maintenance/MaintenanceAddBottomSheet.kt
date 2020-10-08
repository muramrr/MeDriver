/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.10.2020 19:24
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
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType.*
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType.Companion.getChildren
import com.mmdev.me.driver.presentation.ui.common.BaseRecyclerAdapter
import com.mmdev.me.driver.presentation.utils.extensions.hideKeyboard
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
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
	private var childComponent = ""
	
	
	private var childComponentsAdapter = NodeChildComponentsAdapter(emptyList())
	
	private var engineComponents: List<String> = emptyList()
	private var transmissionComponents: List<String> = emptyList()
	private var electricComponents: List<String> = emptyList()
	private var suspensionComponents: List<String> = emptyList()
	private var brakesComponents: List<String> = emptyList()
	private var bodyComponents: List<String> = emptyList()
	private var otherComponents: List<String> = emptyList()
	private var plannedComponents: List<String> = emptyList()
	
	
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
		engineComponents = resources.getStringArray(R.array.fg_maintenance_engine_components).toList()
		transmissionComponents = resources.getStringArray(R.array.fg_maintenance_transmission_components).toList()
		electricComponents = resources.getStringArray(R.array.fg_maintenance_electric_components).toList()
		suspensionComponents = resources.getStringArray(R.array.fg_maintenance_suspension_components).toList()
		brakesComponents = resources.getStringArray(R.array.fg_maintenance_brakes_components).toList()
		bodyComponents = resources.getStringArray(R.array.fg_maintenance_body_components).toList()
		otherComponents = resources.getStringArray(R.array.fg_maintenance_other_components).toList()
		plannedComponents = resources.getStringArray(R.array.fg_maintenance_planned_components).toList()
	}
	
	private fun setupViews() {
		binding.apply {
			
			motionMaintenance.addTransitionListener(object : MotionLayout.TransitionListener {
				override fun onTransitionStarted(container: MotionLayout, start: Int, end: Int) {}
				
				override fun onTransitionChange(container: MotionLayout, start: Int, end: Int, pos: Float) {}
				
				override fun onTransitionCompleted(container: MotionLayout, currentId: Int) {
					when (currentId) {
						R.id.childPickSet -> (requireDialog() as BottomSheetDialog).behavior.isDraggable = false
						else -> (requireDialog() as BottomSheetDialog).behavior.isDraggable = true
					}
				}
				
				override fun onTransitionTrigger(
					container: MotionLayout, triggerId: Int, positive: Boolean, progress: Float
				) {}
			})
			
			
			
			
			btnBack.setDebounceOnClick(500) {
				//transition to start motion state and the next click dismisses dialog
				when (motionMaintenance.currentState) {
					R.id.parentPickSet -> dismiss()
					R.id.childPickSet -> motionMaintenance.transitionToState(R.id.parentPickSet)
					R.id.formSet -> {
						tvToolbarTitle.text = parentNode
						motionMaintenance.transitionToStart()
					}
				}
				hideKeyboard(rootView)
			}
			
			
		}
		
		setupParentPickSet()
		setupChildPickSet()
		setupFormSet()
	}
	
	private fun setupParentPickSet() {
		binding.apply {
			cvEngineNode.setOnClickListener {
				parentNodeSelected(ENGINE, tvEngineNodeTitle.text.toString())
			}
			
			cvTransmissionNode.setOnClickListener {
				parentNodeSelected(TRANSMISSION, tvTransmissionNodeTitle.text.toString())
			}
			
			cvElectricsNode.setOnClickListener {
				parentNodeSelected(ELECTRICS, tvElectricsNodeTitle.text.toString())
			}
			
			cvSuspensionNode.setOnClickListener {
				parentNodeSelected(SUSPENSION, tvSuspensionNodeTitle.text.toString())
			}
			
			cvBrakesNode.setOnClickListener {
				parentNodeSelected(BRAKES, tvBrakesNodeTitle.text.toString())
			}
			
			cvBodyNode.setOnClickListener {
				parentNodeSelected(BODY, tvBodyNodeTitle.text.toString())
			}
			
			cvOtherNode.setOnClickListener {
				parentNodeSelected(OTHERS, tvOtherNodeTitle.text.toString())
			}
			
			cvPlannedNode.setOnClickListener {
				parentNodeSelected(PLANNED, tvPlannedNodeTitle.text.toString())
			}
		}
	}
	
	private fun setupChildPickSet() {
		binding.apply {
			
			childComponentsAdapter.setOnItemClickListener { view, position, item ->
				childComponentSelected(
					mViewModel.selectedVehicleSystemNode.value!!.getChildren()[position],
					item
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
			
			logWtf(TAG, "parent " + it.name)
			
			when (it) {
				ENGINE -> childComponentsAdapter.setNewData(engineComponents)
				TRANSMISSION -> childComponentsAdapter.setNewData(transmissionComponents)
				ELECTRICS -> childComponentsAdapter.setNewData(electricComponents)
				SUSPENSION -> childComponentsAdapter.setNewData(suspensionComponents)
				BRAKES -> childComponentsAdapter.setNewData(brakesComponents)
				BODY -> childComponentsAdapter.setNewData(bodyComponents)
				OTHERS -> childComponentsAdapter.setNewData(otherComponents)
				PLANNED -> childComponentsAdapter.setNewData(plannedComponents)
				else -> {}
			}
		})
	}
	
	private fun observeSelectedChildComponent() {
		mViewModel.selectedChildComponent.observe(this, {
			
			logWtf(TAG, "child = " + it.getSparePartName())
			
		})
	}
	
	private fun parentNodeSelected(parent: VehicleSystemNodeType, title: String) {
		mViewModel.selectedVehicleSystemNode.postValue(parent)
		parentNode = title
		binding.tvToolbarTitle.text = parentNode
		
		if (parent != OTHERS) {
			binding.motionMaintenance.transitionToState(R.id.childPickSet)
		}
		else {
			binding.motionMaintenance.transitionToState(R.id.formSet)
			childComponentSelected(OtherParts.OTHER, title)
		}
	}
	
	private fun childComponentSelected(component: SparePart, title: String) {
		binding.motionMaintenance.transitionToState(R.id.formSet)
		mViewModel.selectedChildComponent.postValue(component)
		childComponent = title
		binding.tvToolbarTitle.text = childComponent
	}
	
	
	override fun onDestroyView() {
		binding.unbind()
		_binding = null
		super.onDestroyView()
	}
	
	
	
	
	private class NodeChildComponentsAdapter(data: List<String>) :
			BaseRecyclerAdapter<String>(data, R.layout.item_single_text)
	
}