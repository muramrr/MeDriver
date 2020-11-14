/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 14.11.2020 17:41
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.history

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.databinding.FragmentFuelHistoryBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFragment
import com.mmdev.me.driver.presentation.ui.fuel.history.add.FuelHistoryAddDialog
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
import org.koin.androidx.viewmodel.ext.android.viewModel


class FuelHistoryFragment: BaseFragment<FuelHistoryViewModel, FragmentFuelHistoryBinding>(
	R.layout.fragment_fuel_history
) {
	override val mViewModel: FuelHistoryViewModel by viewModel()
	
	private val mAdapter = FuelHistoryAdapter().apply {
		setToBottomScrollListener {
			mViewModel.loadNextHistory()
		}
		
		setToTopScrollListener {
			mViewModel.loadPreviousHistory()
		}
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel.shouldBeUpdated.observe(this, { if (it) mViewModel.loadInitHistory() })
	}
	
	override fun setupViews() {
		mViewModel.viewState.observe(this, { renderState(it) })
		
		binding.fabAddHistoryEntry.isEnabled = MedriverApp.currentVehicle != null
		
		binding.rvFuelHistory.apply {
			setHasFixedSize(true)
			//register data observer to automatically scroll to top when new history record added
			adapter = mAdapter
			
			layoutManager = LinearLayoutManager(requireContext())
		}
		
		
		binding.fabAddHistoryEntry.setDebounceOnClick {
			
			FuelHistoryAddDialog().show(childFragmentManager, FuelHistoryAddDialog::class.java.canonicalName)
		}
	}
	
	override fun renderState(state: ViewState) {
//		binding.viewLoading.visibleIf(otherwise = View.INVISIBLE) {
//			state == FuelHistoryViewState.Loading
//		}
		
		when (state) {
			is FuelHistoryViewState.Init -> {
				logInfo(TAG, "init data size = ${state.data.size}")
				mAdapter.setInitData(state.data)
				
			}
			is FuelHistoryViewState.LoadNext -> {
				logInfo(TAG, "loaded next = ${state.data.size}")
				mAdapter.insertNextData(state.data)
			}
			is FuelHistoryViewState.LoadPrevious -> {
				logInfo(TAG, "loaded previous = ${state.data.size}")
				mAdapter.insertPreviousData(state.data)
			}
			is FuelHistoryViewState.Error -> {
				logError(TAG, state.errorMessage)
			}
		}
	}
	
}