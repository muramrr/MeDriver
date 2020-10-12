/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 12.10.2020 21:01
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
import androidx.recyclerview.widget.GridLayoutManager
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
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType.Companion.getChildren
import com.mmdev.me.driver.presentation.ui.common.custom.decorators.GridItemDecoration
import com.mmdev.me.driver.presentation.ui.maintenance.MaintenanceViewModel
import com.mmdev.me.driver.presentation.ui.maintenance.add.child.EditChildAdapter
import com.mmdev.me.driver.presentation.ui.maintenance.add.children.ChildrenAdapter
import com.mmdev.me.driver.presentation.ui.maintenance.add.parent.ParentNodeAdapter
import com.mmdev.me.driver.presentation.ui.maintenance.add.parent.ParentNodeUi
import com.mmdev.me.driver.presentation.utils.extensions.hideKeyboard
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 *
 */

class MaintenanceAddBottomSheet: BottomSheetDialogFragment() {
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	private val mViewModel: MaintenanceViewModel by lazy { requireParentFragment().getViewModel() }
	
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
	
	private val mParentNodesAdapter = ParentNodeAdapter()
	private val mChildrenAdapter = ChildrenAdapter(emptyList())
	private lateinit var mEditChildAdapter: EditChildAdapter
	
	
	
	private var multiSelectEnabled = false
	
	private var multiSelectButtonChooseText = ""
	private var multiSelectButtonClearText = ""
	
	
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
	
	}
	
	private fun setupViews() {
		binding.apply {
			
			motionMaintenance.addTransitionListener(object : MotionLayout.TransitionListener {
				override fun onTransitionStarted(container: MotionLayout, start: Int, end: Int) {}
				
				override fun onTransitionChange(container: MotionLayout, start: Int, end: Int, pos: Float) {}
				
				override fun onTransitionCompleted(container: MotionLayout, currentId: Int) {
					(requireDialog() as BottomSheetDialog).behavior.isDraggable = currentId != R.id.childrenPickSet
					if (currentId == R.id.parentPickSet) mChildrenAdapter.turnOffMultiSelect()
					
					btnNextFromMultiSelect.animate().apply {
						translationY(
							if (multiSelectEnabled && currentId == R.id.childrenPickSet ) 0f
							else btnNextFromMultiSelectStartPos
						)
						interpolator = OvershootInterpolator()
						duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
					}
				}
				
				override fun onTransitionTrigger(
					container: MotionLayout, triggerId: Int, positive: Boolean, progress: Float
				) {}
			})
			
			
			btnMultiSelect.setDebounceOnClick(500) {
				mChildrenAdapter.toggleMultiSelect()
			}
		}
		
		setupParentPickSet()
		setupNavigationButtons()
		setupChildrenPickSet()
		setupFormSet()
	}
	
	
	
	/**
	 * setup parent adapter, click listener, manager
	 * observe selected [ParentNodeUi]
	 */
	private fun setupParentPickSet() {
		mViewModel.selectedParentNode.observe(this, {
			if (it != null) renderParentSelected(it)
		})
		
		binding.rvParentNode.apply {
			adapter = mParentNodesAdapter
			addItemDecoration(GridItemDecoration())
			layoutManager = GridLayoutManager(context, 2)
			setHasFixedSize(true)
		}
		
		mParentNodesAdapter.setOnItemClickListener { view, position, item ->
			mViewModel.selectParentNode(VehicleSystemNodeType.valuesArray[position], item)
		}
	}
	
	/** render selected [ParentNodeUi] */
	private fun renderParentSelected(parentNodeUi: ParentNodeUi) {
		
		/** 0 res int contains only [OTHER] in [ParentNodeUi] */
		if (parentNodeUi.children != 0) {
			binding.tvToolbarTitle.text = getString(parentNodeUi.title)
			mChildrenAdapter.setNewData(
				resources.getStringArray(parentNodeUi.children).toList()
			)
			binding.motionMaintenance.transitionToState(R.id.childrenPickSet)
		}
		else {
			mChildrenAdapter.setNewData(emptyList())
			mEditChildAdapter.setNewData(
				listOf(Pair(getString(parentNodeUi.title), OtherParts.OTHER))
			)
			binding.motionMaintenance.transitionToState(R.id.formSet)
		}
		
	}
	
	
	
	
	
	
	private fun setupNavigationButtons() {
		binding.apply {
			btnBack.setDebounceOnClick(500) {
				//transition to start motion state and the next click dismisses dialog
				when (motionMaintenance.currentState) {
					R.id.parentPickSet -> dismissAllowingStateLoss()
					R.id.childrenPickSet -> motionMaintenance.transitionToState(R.id.parentPickSet)
					R.id.formSet -> motionMaintenance.transitionToStart()
					
				}
				hideKeyboard(rootView)
			}
			
			btnNextFromMultiSelectStartPos = btnNextFromMultiSelect.translationY
			btnNextFromMultiSelect.setDebounceOnClick {
				childrenSelected(mChildrenAdapter.selectedChildren.map {
					Pair(
						it.first,
						mViewModel.selectedVehicleSystemNode.value!!.getChildren()[it.second]
					)
				})
			}
		}
	}
	
	private fun setupChildrenPickSet() {
		
		observeMultiSelect()
		
		binding.rvNodeChildren.apply {
			adapter = mChildrenAdapter
			layoutManager = LinearLayoutManager(requireContext())
			setHasFixedSize(true)
		}
		
		
		mChildrenAdapter.setOnItemClickListener { view, position, item ->
			
			childrenSelected(mChildrenAdapter.selectedChildren.map {
				Pair(
					it.first,
					mViewModel.selectedVehicleSystemNode.value!!.getChildren()[it.second]
				)
			})
			
			binding.motionMaintenance.transitionToState(R.id.formSet)
		}
	}
	
	private fun observeMultiSelect() {
		mChildrenAdapter.multiSelectState.observe(this, {
			multiSelectEnabled = it
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
	}
	
	
	
	private fun setupFormSet() {
		mEditChildAdapter = EditChildAdapter(emptyList(), childFragmentManager, lifecycle)
		
		binding.vpNodeEditChild.apply {
			adapter = mEditChildAdapter
		}
		
	}
	
	private fun observeSelectedNode() {
		mViewModel.selectedVehicleSystemNode.observe(this, {
			
			logWtf(TAG, "parent " + it?.name)
			
		})
	}
	
	
	
	
	private fun observeSelectedChildComponent() {
		mViewModel.selectedChildComponent.observe(this, {
			
			logWtf(TAG, "child = " + it?.getSparePartName())
			
		})
	}
	
	private fun childrenSelected(selectedItems: List<Pair<String, SparePart>>) {
		mEditChildAdapter.setNewData(selectedItems)
		binding.motionMaintenance.transitionToState(R.id.formSet)
	}
	
	
	override fun onDestroyView() {
		binding.unbind()
		_binding = null
		super.onDestroyView()
	}
	
	
}