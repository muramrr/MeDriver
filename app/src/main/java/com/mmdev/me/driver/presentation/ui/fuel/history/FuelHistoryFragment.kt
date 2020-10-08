/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.10.2020 19:24
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
import com.mmdev.me.driver.core.utils.log.logWarn
import com.mmdev.me.driver.core.utils.log.logWtf
import com.mmdev.me.driver.databinding.FragmentFuelHistoryBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFragment
import com.mmdev.me.driver.presentation.ui.common.EndlessRecyclerViewScrollListener
import com.mmdev.me.driver.presentation.ui.fuel.prices.FuelPricesViewModel
import com.mmdev.me.driver.presentation.utils.extensions.domain.getValue
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
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
		mViewModel.getHistoryRecords()
	}
	
	override fun setupViews() {
		mViewModel.fuelHistoryState.observe(this, {
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
					
					//mViewModel.getHistoryRecords(shouldBeLoaded)
					logWarn(TAG, "scrolled")
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
				logWtf(TAG, "${state.data}")
			}
			is FuelHistoryViewState.InsertNewOne -> {
				logInfo(TAG, "insert new data: " +
				             "odometer = ${state.item.odometerValueBound.kilometers } km," +
				             "vehicle = ${state.item.vehicleVinCode}")
						           
				mFuelHistoryAdapter.insertNewRecord(state.item)
				
				if (state.item.odometerValueBound.getValue() > MedriverApp.currentVehicle!!.odometerValueBound.getValue())
					//update vehicle with new odometer value
					sharedViewModel.updateVehicle(
						MedriverApp.currentUser,
						MedriverApp.currentVehicle!!.copy(odometerValueBound = state.item.odometerValueBound)
					)
				
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