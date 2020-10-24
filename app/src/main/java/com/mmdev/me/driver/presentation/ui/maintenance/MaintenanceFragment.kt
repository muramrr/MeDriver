/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 24.10.2020 19:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance

import android.os.Bundle
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
import com.mmdev.me.driver.presentation.ui.common.custom.decorators.LinearItemDecoration
import com.mmdev.me.driver.presentation.ui.maintenance.add.MaintenanceAddBottomSheet
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
		observeUpdateRequires()
		mViewModel.loadMaintenanceHistory()
	}
	
	override fun setupViews() {
		
		mViewModel.viewState.observe(this, {
			renderState(it)
		})
		
		binding.apply {
			rvMaintenance.apply {
				adapter = mAdapter
				layoutManager = LinearLayoutManager(requireContext())
				addItemDecoration(LinearItemDecoration())
			}
			
			btnMaintenanceFilter.isEnabled = MedriverApp.currentVehicle != null
			
			binding.btnMaintenanceFilter.setOnClickListener {
				showFilterDialog()
			}
			
			fabAddMaintenance.isEnabled = true //MedriverApp.currentVehicle != null
			
			fabAddMaintenance.setDebounceOnClick {
				MaintenanceAddBottomSheet()
					.show(childFragmentManager, MaintenanceAddBottomSheet::class.java.canonicalName)
			}
		}
		
	}
	
	private fun observeUpdateRequires() {
		mViewModel.updateTrigger.observe(this, {})
	}

	override fun renderState(state: ViewState) {
		super.renderState(state)
		when (state) {
			is MaintenanceHistoryViewState.Init -> {
				logInfo(TAG, "init data size = ${state.data.size}")
				mAdapter.setInitData(state.data)
				logWtf(TAG, "${state.data}")
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
				logError(TAG, state.errorMessage)
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