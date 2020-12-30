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

package com.mmdev.me.driver.presentation.ui.maintenance.add

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.BtmSheetMaintenanceAddBinding
import com.mmdev.me.driver.domain.maintenance.data.components.OtherParts
import com.mmdev.me.driver.domain.maintenance.data.components.OtherParts.OTHER
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart.Companion.OTHER
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType.Companion.getChildren
import com.mmdev.me.driver.presentation.core.base.BaseBottomSheetFragment
import com.mmdev.me.driver.presentation.ui.common.custom.decorators.GridItemDecoration
import com.mmdev.me.driver.presentation.ui.maintenance.MaintenanceViewModel
import com.mmdev.me.driver.presentation.ui.maintenance.add.child.Child
import com.mmdev.me.driver.presentation.ui.maintenance.add.child.ChildEditAdapter
import com.mmdev.me.driver.presentation.ui.maintenance.add.children.ChildrenAdapter
import com.mmdev.me.driver.presentation.ui.maintenance.add.parent.ParentNodeAdapter
import com.mmdev.me.driver.presentation.ui.maintenance.add.parent.ParentNodeUi
import com.mmdev.me.driver.presentation.utils.extensions.hideKeyboard
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Container for adding new maintenance records
 */

class MaintenanceAddBottomSheet: BaseBottomSheetFragment<MaintenanceAddViewModel, BtmSheetMaintenanceAddBinding>(
	layoutId = R.layout.btm_sheet_maintenance_add
) {
	
	override val mViewModel: MaintenanceAddViewModel by viewModel()
	private val parentViewModel: MaintenanceViewModel by lazy { requireParentFragment().getViewModel() }
	
	
	private val mParentNodesAdapter = ParentNodeAdapter()
	private val mChildrenAdapter = ChildrenAdapter(IntArray(0))
	private lateinit var mChildEditAdapter: ChildEditAdapter
	
	
	// local multi select flag
	private var multiSelectEnabled = false
	
	//text to be shown on multi select toggle button
	private var multiSelectButtonChooseText = ""
	private var multiSelectButtonClearText = ""
	
	
	private var btnNextFromMultiSelectStartPos = 0f
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		parentViewModel.isAddDialogShowing.postValue(true)
	}
	
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
	
	
	
	override fun setupViews() {
		initStringRes()
		
		observeShouldBeUpdated()
		observeParentSelected()
		observeChildrenSelected()
		observeMultiSelect()
		
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
					(requireDialog() as BottomSheetDialog).behavior.isDraggable = currentId == R.id.parentPickSet
					
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
	
	private fun initStringRes() {
		multiSelectButtonChooseText = getString(R.string.btm_sheet_maintenance_btn_multi_select_choose)
		multiSelectButtonClearText = getString(R.string.btm_sheet_maintenance_btn_multi_select_clear)
		
	}
	
	/** setup parent adapter, manager, adapter click listener (step 1 in motion scene) */
	private fun setupParentPickSet() {
		
		binding.rvParentNode.apply {
			adapter = mParentNodesAdapter
			addItemDecoration(GridItemDecoration(true))
			layoutManager = GridLayoutManager(context, 2)
			setHasFixedSize(true)
		}
		
		mParentNodesAdapter.setOnItemClickListener { view, position, item ->
			mViewModel.selectParentNode(VehicleSystemNodeType.valuesArray[position], item)
		}
	}
	
	/** setup children adapter, click listener, multi select (step 2 in motion scene) */
	private fun setupChildrenPickSet() {
		binding.rvNodeChildren.apply {
			adapter = mChildrenAdapter
			layoutManager = LinearLayoutManager(requireContext())
			setHasFixedSize(true)
		}
		
		// click on single children
		mChildrenAdapter.setOnItemClickListener { view, position, stringRes ->
			
			mViewModel.selectedChildren.postValue(
				listOf(
					Child(stringRes, mViewModel.selectedVehicleSystemNode.value!!.getChildren()[position])
				)
			)
		}
		
		binding.btnMultiSelect.setDebounceOnClick(500) {
			mChildrenAdapter.toggleMultiSelect().also { mViewModel.multiSelectState.postValue(it) }
		}
		
		// if no children selected and you press next -> show snack info, else -> proceed
		binding.btnNextFromMultiSelect.setDebounceOnClick {
			
			if (mChildrenAdapter.selectedChildren.isEmpty()) {
				showInnerSnack(R.string.btm_sheet_maintenance_btn_multi_select_error)
			}
			else {
				mViewModel.selectedChildren.postValue(
					mChildrenAdapter.selectedChildren.map {
						Child(
							title = it.value,
							sparePart = mViewModel.selectedVehicleSystemNode.value!!.getChildren()[it.key]
						)
					}
				)
				
				animateBtnNextFromMultiSelect { btnNextFromMultiSelectStartPos }
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
	
	
	
	/** setup final fill in form set (step 3 in motion scene) */
	private fun setupFormSet() {
		mChildEditAdapter = ChildEditAdapter(emptyList(), this)
		
		binding.vpNodeEditChild.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
			@SuppressLint("SetTextI18n")
			override fun onPageSelected(position: Int) {
				binding.tvSelectedChildrenCount.text = "${position + 1}/${mChildEditAdapter.itemCount}"
			}
		})
		
		binding.vpNodeEditChild.apply {
			adapter = mChildEditAdapter
		}
		
	}
	
	private fun observeShouldBeUpdated() {
		mViewModel.parentShouldBeUpdated.observe(this, {
			parentViewModel.shouldUpdateData.postValue(it)
		})
	}
	
	/** observe selected parent node, defines next children list */
	private fun observeParentSelected() {
		mViewModel.selectedParentNode.observe(this, { parentNodeUi ->
			
			parentNodeUi?.let {
				binding.tvToolbarTitle.text = getString(it.title)
				
				/** check [ParentNodeUi] contains children if yes -> navigate to next step */
				if (it.children.isNotEmpty()) {
					
					mChildrenAdapter.setNewData(it.children)
					binding.motionMaintenance.transitionToState(R.id.childrenPickSet)
				}
				/** navigate directly to final form because of [OTHER] doesn't has any children */
				else {
					mChildrenAdapter.setNewData(IntArray(0))
					mViewModel.selectedChildren.postValue(
						listOf(Child(R.string.maintenance_other_component, OtherParts.OTHER))
					)
					binding.motionMaintenance.transitionToState(R.id.formSet)
				}
			}
			
		})
	}
	
	/** observe toggling multiSelect button to change its text and clear selected items */
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
	
	/** observe selected children to make update adapter with new items to add in final step */
	@SuppressLint("SetTextI18n")
	private fun observeChildrenSelected() {
		mViewModel.selectedChildren.observe(this, {
			
			it?.let {
				mChildEditAdapter.setNewData(it)
				binding.tvSelectedChildrenCount.text = "${1}/${mChildEditAdapter.itemCount}"
				binding.motionMaintenance.transitionToState(R.id.formSet)
			}
			
		})
	}
	
	override fun onDismiss(dialog: DialogInterface) {
		parentViewModel.isAddDialogShowing.postValue(false)
		super.onDismiss(dialog)
	}
}