/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.09.2020 04:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.history

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.databinding.FragmentFuelHistoryBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFragment
import com.mmdev.me.driver.presentation.ui.common.EndlessRecyclerViewScrollListener
import com.mmdev.me.driver.presentation.ui.fuel.prices.FuelPricesViewModel
import com.mmdev.me.driver.presentation.utils.setDebounceOnClick
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class FuelHistoryFragment: BaseFragment<FuelHistoryViewModel, FragmentFuelHistoryBinding>(
	R.layout.fragment_fuel_history
) {
	override val mViewModel: FuelHistoryViewModel by viewModel()
	private val fuelPricesViewModel: FuelPricesViewModel by sharedViewModel()
	
	private val mFuelHistoryAdapter = FuelHistoryAdapter()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel.fuelHistoryState.observe(this, {
			renderState(it)
		})
		
		mViewModel.getHistoryRecords()
	}
	
	override fun setupViews() {
		val linearLayoutManager = LinearLayoutManager(requireContext())
		
		binding.rvFuelHistory.apply {
			//register data observer to automatically scroll to top when new history record added
			adapter = mFuelHistoryAdapter.apply {
				registerAdapterDataObserver (object : RecyclerView.AdapterDataObserver() {
					override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
						super.onItemRangeInserted(positionStart, itemCount)
						if (positionStart == 0
						    && positionStart == linearLayoutManager.findFirstCompletelyVisibleItemPosition()) {
							linearLayoutManager.scrollToPosition(0)
						}
					}
				})
			}
			
			layoutManager = linearLayoutManager
			
			//load more data on scroll
			addOnScrollListener(object: EndlessRecyclerViewScrollListener(linearLayoutManager) {
				override fun onLoadMore(lastVisiblePosition: Int, totalCount: Int, shouldBeLoaded: Int) {
					
					mViewModel.getHistoryRecords(shouldBeLoaded)
					
				}
			})
		}
		
		
		binding.fabAddHistoryEntry.setDebounceOnClick {
			
			FuelHistoryAddDialog(fuelPricesViewModel.fuelPrices.value!!)
				.show(childFragmentManager, FuelHistoryAddDialog::class.java.canonicalName)
		}
	}
	
	override fun renderState(state: ViewState) {
		super.renderState(state)
		when (state) {
			is FuelHistoryViewState.Init -> {
				logInfo(TAG, "init data size = ${state.data.size}")
				mFuelHistoryAdapter.setInitData(state.data)
			}
			is FuelHistoryViewState.InsertNewOne -> {
				logInfo(TAG, "insert new data: " +
				             "odometer = ${state.data.map { it.odometerValueBound }} km, " +
				             "date = ${state.data.map { it.dateText }}")
				mFuelHistoryAdapter.insertRecordOnTop(state.data)
			}
			is FuelHistoryViewState.Paginate -> {
				logInfo(TAG, "paginate data size = ${state.data.size}")
				mFuelHistoryAdapter.insertPaginationData(state.data)
			}
			is FuelHistoryViewState.Loading -> {
				logDebug(TAG, "loading")
			}
			is FuelHistoryViewState.Error -> {
				logError(TAG, state.errorMessage)
			}
		}
	}
	
}