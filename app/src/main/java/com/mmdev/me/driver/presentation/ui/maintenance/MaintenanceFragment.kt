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

package com.mmdev.me.driver.presentation.ui.maintenance

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.core.utils.log.logWtf
import com.mmdev.me.driver.databinding.FragmentMaintenanceBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import com.mmdev.me.driver.presentation.ui.MainActivity
import com.mmdev.me.driver.presentation.ui.common.custom.decorators.LinearItemDecoration
import com.mmdev.me.driver.presentation.ui.maintenance.add.MaintenanceAddBottomSheet
import com.mmdev.me.driver.presentation.utils.extensions.hideKeyboard
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *
 */

class MaintenanceFragment : BaseFlowFragment<MaintenanceViewModel, FragmentMaintenanceBinding>(
	layoutId = R.layout.fragment_maintenance
) {
	
	override val mViewModel: MaintenanceViewModel by viewModel()
	
	private val mAdapter = MaintenanceHistoryAdapter().apply {
		setToBottomScrollListener {
			mViewModel.loadNextMaintenanceHistory()
		}
		
		setToTopScrollListener {
			mViewModel.loadPreviousMaintenanceHistory()
		}
	}
	
	private var checkedDialogItem = 0
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel.viewState.observe(this, { renderState(it) })
		mViewModel.updateTrigger.observe(this, {})
	}
	
	override fun setupViews() {
		setupSearch()
		
		binding.apply {
			
			with(MainActivity.currentVehicle != null) {
				fabAddMaintenance.isEnabled = this
				btnMaintenanceFilter.isEnabled = this
				layoutSearchMaintenance.isEnabled = this
			}
			
			btnMaintenanceFilter.setOnClickListener {
				binding.etSearchMaintenance.text = null //clear any typed search text
				showFilterDialog()
			}
			
			rvMaintenance.apply {
				setHasFixedSize(true)
				
				adapter = mAdapter
				layoutManager = LinearLayoutManager(requireContext())
				addItemDecoration(LinearItemDecoration())
				
				setOnTouchListener { v, event ->
					performClick()
					hideKeyboard(binding.etSearchMaintenance)
				}
			}
			
			fabAddMaintenance.setDebounceOnClick {
				MaintenanceAddBottomSheet()
					.show(childFragmentManager, MaintenanceAddBottomSheet::class.java.canonicalName)
			}
			
			if (MedriverApp.debug.isEnabled) fabAddMaintenance.setOnLongClickListener {
				mViewModel.generateMaintenanceData(requireContext())
				true
			}
			
		}
		
		mAdapter.setOnDeleteClickListener { view, position, item ->
			mViewModel.delete(item, position)
		}
		
	}
	
	private fun setupSearch() {
		binding.etSearchMaintenance.apply {
			
			doOnTextChanged { text, start, before, count ->
				if (before != 0 && count != 0) {
					logWtf(TAG, "text changed")
					mViewModel.searchMaintenanceHistory(text.toString())
				}
				
			}
			
			setOnEditorActionListener { _, actionId, _ ->
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					mViewModel.searchMaintenanceHistory(text.toString())
					hideKeyboard(this)
				}
				true
			}
		}
	}

	override fun renderState(state: ViewState) {
		when (state) {
			is MaintenanceHistoryViewState.Init -> {
				logInfo(TAG, "init data size = ${state.data.size}")
				mAdapter.setInitData(state.data)
			}
			is MaintenanceHistoryViewState.LoadNext -> {
				logInfo(TAG, "load next = ${state.data.size}")
				mAdapter.insertNextData(state.data)
			}
			is MaintenanceHistoryViewState.LoadPrevious -> {
				logInfo(TAG, "load previous = ${state.data.size}")
				mAdapter.insertPreviousData(state.data)
			}
			is MaintenanceHistoryViewState.Delete -> {
				logInfo(TAG, "deleted at position ${state.position}")
				mAdapter.delete(state.position)
			}
			is MaintenanceHistoryViewState.Filter -> {
				logInfo(TAG, "showing filtered parts")
				mAdapter.setInitData(state.data)
			}
			is MaintenanceHistoryViewState.Error -> {
				logError(TAG, state.errorMessage ?: getString(R.string.default_error))
			}
		}
	}
	
	private fun showFilterDialog() {
		val singleItems = resources.getStringArray(R.array.maintenance_dialog_filter_node_list)
		
		MaterialAlertDialogBuilder(requireContext())
			.setTitle(getString(R.string.maintenance_dialog_filter_title))
			//.setMessage(getString(R.string.maintenance_dialog_filter_message))
			.setNeutralButton(getString(R.string.dialog_btn_cancel)) { dialog, which ->
				dialog.dismiss()
			}
			.setPositiveButton(getString(R.string.dialog_btn_apply)) { _, which ->
				mViewModel.filterHistory(checkedDialogItem)
				logWtf(TAG, "showing filter for ${singleItems[checkedDialogItem]}")
			}
			// Single-choice items (initialized with checked item)
			.setSingleChoiceItems(singleItems, checkedDialogItem) { _, position -> checkedDialogItem = position }
			.show()
	}
}