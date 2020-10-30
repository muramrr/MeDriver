/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.10.2020 20:39
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.history

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.core.utils.log.logWarn
import com.mmdev.me.driver.core.utils.log.logWtf
import com.mmdev.me.driver.databinding.FragmentFuelHistoryBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFragment
import com.mmdev.me.driver.presentation.ui.common.EndlessRecyclerViewScrollListener
import com.mmdev.me.driver.presentation.ui.fuel.history.add.FuelHistoryAddDialog
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
import com.mmdev.me.driver.presentation.utils.extensions.visibleIf
import org.koin.androidx.viewmodel.ext.android.viewModel


class FuelHistoryFragment: BaseFragment<FuelHistoryViewModel, FragmentFuelHistoryBinding>(
	R.layout.fragment_fuel_history
) {
	override val mViewModel: FuelHistoryViewModel by viewModel()
	
	private val mFuelHistoryAdapter = FuelHistoryAdapter()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel.getHistoryRecords()
		mViewModel.shouldBeUpdated.observe(this, { if (it) mViewModel.getHistoryRecords() })
	}
	
	override fun setupViews() {
		mViewModel.viewState.observe(this, {
			renderState(it)
		})
		
		binding.fabAddHistoryEntry.isEnabled = MedriverApp.currentVehicle != null
		
		val linearLayoutManager = LinearLayoutManager(requireContext())
		
		binding.rvFuelHistory.apply {
			setHasFixedSize(true)
			//register data observer to automatically scroll to top when new history record added
			adapter = mFuelHistoryAdapter
			
			layoutManager = linearLayoutManager
			
			//load more data on scroll
			addOnScrollListener(object: EndlessRecyclerViewScrollListener(linearLayoutManager) {
				override fun onLoadMore(lastVisiblePosition: Int, totalCount: Int, shouldBeLoaded: Int) {
					
					//todo: properly load
					//mViewModel.getHistoryRecords(shouldBeLoaded)
					logWarn(TAG, "scrolled")
				}
			})
		}
		
		
		binding.fabAddHistoryEntry.setDebounceOnClick {
			
			FuelHistoryAddDialog().show(childFragmentManager, FuelHistoryAddDialog::class.java.canonicalName)
		}
	}
	
	override fun renderState(state: ViewState) {
		binding.viewLoading.visibleIf(otherwise = View.INVISIBLE) {
			state == FuelHistoryViewState.Loading
		}
		
		when (state) {
			is FuelHistoryViewState.Init -> {
				logInfo(TAG, "init data size = ${state.data.size}")
				mFuelHistoryAdapter.setInitData(state.data)
				logWtf(TAG, "${state.data}")
			}
			is FuelHistoryViewState.Paginate -> {
				logInfo(TAG, "paginate data size = ${state.data.size}")
				mFuelHistoryAdapter.insertPaginationData(state.data)
			}
			is FuelHistoryViewState.Error -> {
				logError(TAG, state.errorMessage)
			}
		}
	}
	
}