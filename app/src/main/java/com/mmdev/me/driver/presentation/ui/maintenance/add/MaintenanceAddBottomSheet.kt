/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.10.2020 17:20
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
import com.mmdev.me.driver.databinding.BottomSheetMaintenanceAddBinding
import com.mmdev.me.driver.domain.maintenance.data.components.OtherParts.OTHER
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart.Companion.OTHER
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType.Companion.getChildren
import com.mmdev.me.driver.presentation.ui.common.custom.decorators.GridItemDecoration
import com.mmdev.me.driver.presentation.ui.maintenance.MaintenanceViewModel
import com.mmdev.me.driver.presentation.ui.maintenance.add.children.ChildrenAdapter
import com.mmdev.me.driver.presentation.ui.maintenance.add.parent.ParentNodeAdapter
import com.mmdev.me.driver.presentation.ui.maintenance.add.parent.ParentNodeUi
import com.mmdev.me.driver.presentation.utils.extensions.hideKeyboard
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
import com.mmdev.me.driver.presentation.utils.extensions.showSnack
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Container for adding new maintenance records
 */

class MaintenanceAddBottomSheet: BottomSheetDialogFragment() {
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	private val mViewModel: MaintenanceViewModel by lazy { requireParentFragment().getViewModel() }
	
	//automatically dismiss to prevent half expanded state
	private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
		
		override fun onStateChanged(bottomSheet: View, newState: Int) {
			if (newState !in arrayOf(BottomSheetBehavior.STATE_EXPANDED,
			                         BottomSheetBehavior.STATE_DRAGGING,
			                         BottomSheetBehavior.STATE_SETTLING))
				dismiss()
		}
		// Do something for slide offset
		override fun onSlide(bottomSheet: View, slideOffset: Float) {}
	}
	
	// prevent view being leaked
	private var _binding: BottomSheetMaintenanceAddBinding? = null
	private val binding: BottomSheetMaintenanceAddBinding
		get() = _binding ?: throw IllegalStateException(
			"Trying to access the binding outside of the view lifecycle."
		)
	
	private val mParentNodesAdapter = ParentNodeAdapter()
	private val mChildrenAdapter = ChildrenAdapter(emptyList())
	//private lateinit var mEditChildAdapter: EditChildAdapter
	
	
	// local multi select flag
	private var multiSelectEnabled = false
	
	//text to be shown on multi select toggle button
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
			dismissWithAnimation = true
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
		
		observeParentSelected()
		observeChildrenSelected()
		observeMultiSelect()
		
		setupViews()
	}
	
	private fun initStringRes() {
		multiSelectButtonChooseText = getString(R.string.btm_sheet_maintenance_btn_multi_select_choose)
		multiSelectButtonClearText = getString(R.string.btm_sheet_maintenance_btn_multi_select_clear)
		
	}
	
	private fun setupViews() {
		binding.apply {
			
			//hide keyboard by pressing anywhere
			root.setOnTouchListener { rootView, _ ->
				rootView.performClick()
				rootView.hideKeyboard(rootView)
			}
			
			//transition to start motion state and the next click dismisses dialog
			btnBack.setDebounceOnClick(500) {
				when (motionMaintenance.currentState) {
					R.id.parentPickSet -> dismissAllowingStateLoss()
					R.id.childrenPickSet -> motionMaintenance.transitionToState(R.id.parentPickSet)
					R.id.formSet -> motionMaintenance.transitionToStart()
				}
				hideKeyboard(rootView)
			}
			
			//save button initial translationY (i guess 100dp, see layout xml)
			btnNextFromMultiSelectStartPos = btnNextFromMultiSelect.translationY
			
			
			motionMaintenance.addTransitionListener(object : MotionLayout.TransitionListener {
				override fun onTransitionStarted(container: MotionLayout, start: Int, end: Int) {}
				override fun onTransitionChange(container: MotionLayout, start: Int, end: Int, pos: Float) {}
				override fun onTransitionTrigger(container: MotionLayout, triggerId: Int, positive: Boolean, progress: Float) {}
				
				
				override fun onTransitionCompleted(container: MotionLayout, currentId: Int) {
					//disable "drag down to close" only on children list
					(requireDialog() as BottomSheetDialog).behavior.isDraggable = currentId != R.id.childrenPickSet
					
					if (currentId == R.id.parentPickSet)
						mChildrenAdapter.turnOffMultiSelect().also {
							mViewModel.multiSelectState.postValue(false)
						}
					
					// animate btnNextFromMultiSelect appearance depending on flag and current state
					animateBtnNextFromMultiSelect {
						if (multiSelectEnabled && currentId == R.id.childrenPickSet ) 0f
						else btnNextFromMultiSelectStartPos
					}
				}
				
			})
			
		}
		
		setupParentPickSet()
		setupChildrenPickSet()
		setupFormSet()
	}
	
	
	
	/** setup parent adapter, manager, adapter click listener */
	private fun setupParentPickSet() {
		
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
	
	private fun setupChildrenPickSet() {
		binding.rvNodeChildren.apply {
			adapter = mChildrenAdapter
			layoutManager = LinearLayoutManager(requireContext())
			setHasFixedSize(true)
		}
		
		// click on single children
		mChildrenAdapter.setOnItemClickListener { view, position, item ->
			
			mViewModel.selectedChildren.postValue(
				listOf(
					Pair(item, mViewModel.selectedVehicleSystemNode.value!!.getChildren()[position])
				)
			)
		}
		
		binding.btnMultiSelect.setDebounceOnClick(500) {
			mChildrenAdapter.toggleMultiSelect().also { mViewModel.multiSelectState.postValue(it) }
		}
		
		// if no children selected and you press next -> show snack info, else -> proceed
		binding.btnNextFromMultiSelect.setDebounceOnClick {
			
			if (mChildrenAdapter.selectedChildren.isEmpty()) {
				rootView.showSnack(R.string.btm_sheet_maintenance_btn_multi_select_error)
			}
			else {
				mViewModel.selectedChildren.postValue(
					mChildrenAdapter.selectedChildren.map {
						Pair(
							it.first,
							mViewModel.selectedVehicleSystemNode.value!!.getChildren()[it.second]
						)
					}
				)
			}
		}
	}
	
	
	private inline fun animateBtnNextFromMultiSelect(condition: () -> Float) {
		binding.btnNextFromMultiSelect.animate().apply {
			translationY(condition())
			interpolator = OvershootInterpolator()
			duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
		}
	}
	
	private fun setupFormSet() {
//		mEditChildAdapter = EditChildAdapter(emptyList(), childFragmentManager, lifecycle)
//		mEditChildAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
//			@SuppressLint("SetTextI18n")
//			override fun onChanged() {
//				binding.tvSelectedChildrenCount.text = "${1}/${mEditChildAdapter.itemCount}"
//			}
//		})
//
//		binding.vpNodeEditChild.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
//			@SuppressLint("SetTextI18n")
//			override fun onPageSelected(position: Int) {
//				binding.tvSelectedChildrenCount.text = "${position + 1}/${mEditChildAdapter.itemCount}"
//			}
//		})
//
//		binding.vpNodeEditChild.apply {
//			adapter = mEditChildAdapter
//		}
//
	}
	
	
	private fun observeParentSelected() {
		mViewModel.selectedParentNode.observe(this, { parentNodeUi ->
			
			parentNodeUi?.let {
				binding.tvToolbarTitle.text = getString(it.title)
				
				/** check [ParentNodeUi] contains children if yes -> navigate to next step */
				if (it.children != 0) {
					
					mChildrenAdapter.setNewData(resources.getStringArray(it.children).toList())
					binding.motionMaintenance.transitionToState(R.id.childrenPickSet)
				}
				/** navigate directly to final form because of [OTHER] doesn't has any children */
				else {
					mChildrenAdapter.setNewData(emptyList())
					//mEditChildAdapter.setNewData(listOf(Pair(getString(parentNodeUi.title), OTHER)))
					binding.motionMaintenance.transitionToState(R.id.formSet)
				}
			}
			
		})
	}
	
	private fun observeMultiSelect() {
		mViewModel.multiSelectState.observe(this, {
			multiSelectEnabled = it
			if (!it) mChildrenAdapter.turnOffMultiSelect()
			
			//change multi select button text after pressing
			binding.btnMultiSelect.text = if (it) multiSelectButtonClearText
			else multiSelectButtonChooseText
			
			animateBtnNextFromMultiSelect { if (it) 0f else btnNextFromMultiSelectStartPos }
			
		})
	}
	
	private fun observeChildrenSelected() {
		mViewModel.selectedChildren.observe(this, {
			
			it?.let {
				//mEditChildAdapter.setNewData(it)
				binding.motionMaintenance.transitionToState(R.id.formSet)
			}
			
		})
	}
	
	
	override fun onDestroyView() {
		binding.unbind()
		_binding = null
		super.onDestroyView()
	}
	
	
}