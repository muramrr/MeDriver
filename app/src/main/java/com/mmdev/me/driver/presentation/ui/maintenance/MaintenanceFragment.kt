/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 25.10.2020 20:41
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
import com.mmdev.me.driver.databinding.FragmentMaintenanceBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
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
	
	private val mAdapter = MaintenanceHistoryAdapter()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel.updateTrigger.observe(this, {})
		mViewModel.loadMaintenanceHistory()
	}
	
	override fun setupViews() {
		setupAddMaintenance()
		setupFilter()
		setupSearch()
		
		mViewModel.viewState.observe(this, { renderState(it) })
		
		binding.rvMaintenance.apply {
			adapter = mAdapter
			layoutManager = LinearLayoutManager(requireContext())
			addItemDecoration(LinearItemDecoration())
			
			setOnTouchListener { v, event ->
				performClick()
				hideKeyboard(binding.etSearchMaintenance)
			}
		}
		
		
	}
	
	private fun setupSearch() {
		binding.etSearchMaintenance.apply {
			
			doOnTextChanged { text, start, before, count ->
				if (!text.isNullOrBlank()) mViewModel.searchMaintenanceHistory(text.toString())
			}
			
			setOnEditorActionListener { _, actionId, _ ->
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					mViewModel.searchMaintenanceHistory(text.toString())
					hideKeyboard(this)
				}
				false
			}
		}
	}
	
	private fun setupFilter() {
		binding.btnMaintenanceFilter.isEnabled = MedriverApp.currentVehicle != null
		binding.btnMaintenanceFilter.setOnClickListener {
			binding.etSearchMaintenance.text = null //clear any typed search text
			showFilterDialog()
		}
	}
	
	private fun setupAddMaintenance() {
		binding.fabAddMaintenance.isEnabled = true //MedriverApp.currentVehicle != null
		binding.fabAddMaintenance.setDebounceOnClick {
			MaintenanceAddBottomSheet()
				.show(childFragmentManager, MaintenanceAddBottomSheet::class.java.canonicalName)
		}
	}
	

	override fun renderState(state: ViewState) {
		super.renderState(state)
		when (state) {
			is MaintenanceHistoryViewState.Init -> {
				logInfo(TAG, "init data size = ${state.data.size}")
				mAdapter.setInitData(state.data)
			}
			is MaintenanceHistoryViewState.Paginate -> {
				logInfo(TAG, "paginate data size = ${state.data.size}")
				mAdapter.insertPaginationData(state.data)
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
		var checkedItem = 0
		
		MaterialAlertDialogBuilder(requireContext())
			.setTitle(getString(R.string.maintenance_dialog_filter_title))
			//.setMessage(getString(R.string.maintenance_dialog_filter_message))
			.setNeutralButton(getString(R.string.maintenance_dialog_filter_btn_neutral)) { dialog, which ->
				dialog.dismiss()
			}
			.setPositiveButton(getString(R.string.maintenance_dialog_filter_btn_positive)) { _, which ->
				mViewModel.filterHistory(checkedItem)
			}
			// Single-choice items (initialized with checked item)
			.setSingleChoiceItems(singleItems, checkedItem) { _, position -> checkedItem = position }
			.show()
	}
}