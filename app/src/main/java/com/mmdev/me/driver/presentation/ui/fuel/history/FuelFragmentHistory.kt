/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 18.08.2020 18:05
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.history

import androidx.recyclerview.widget.LinearLayoutManager
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.utils.DateConverter
import com.mmdev.me.driver.core.utils.DateConverter.getMonthInt
import com.mmdev.me.driver.databinding.FragmentFuelHistoryBinding
import com.mmdev.me.driver.domain.fuel.history.model.FuelHistoryRecord
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFragment
import com.mmdev.me.driver.presentation.ui.common.BaseAdapter
import com.mmdev.me.driver.presentation.ui.common.EndlessRecyclerViewScrollListener
import com.mmdev.me.driver.presentation.utils.setDebounceOnClick
import org.koin.androidx.viewmodel.ext.android.sharedViewModel



class FuelFragmentHistory: BaseFragment<FuelHistoryViewModel, FragmentFuelHistoryBinding>(
	R.layout.fragment_fuel_history
) {
	override val mViewModel: FuelHistoryViewModel by sharedViewModel()
	
	private val data = listOf(
		FuelHistoryRecord(date = DateConverter.toDate("20-01-2020")),
		FuelHistoryRecord(date = DateConverter.toDate("20-01-2020")),
		FuelHistoryRecord(date = DateConverter.toDate("20-01-2020")),
		FuelHistoryRecord(date = DateConverter.toDate("20-03-2020")),
		FuelHistoryRecord(date = DateConverter.toDate("20-04-2020")),
		FuelHistoryRecord(date = DateConverter.toDate("20-01-2020")),
		FuelHistoryRecord(date = DateConverter.toDate("20-01-2020")),
		FuelHistoryRecord(date = DateConverter.toDate("20-01-2020")),
		FuelHistoryRecord(date = DateConverter.toDate("20-03-2020")),
		FuelHistoryRecord(date = DateConverter.toDate("20-04-2020"))
	)
	
	private val mFuelHistoryAdapter = FuelHistoryAdapter()
	
	
	
	
	override fun setupViews() {
		val linearLayoutManager = LinearLayoutManager(requireContext())
		
		mFuelHistoryAdapter.setNewData(data)
		
		
		binding.rvFuelHistory.apply {
			adapter = mFuelHistoryAdapter
			layoutManager = linearLayoutManager
			//load more messages on scroll
			addOnScrollListener(object: EndlessRecyclerViewScrollListener(linearLayoutManager) {
				override fun onLoadMore(lastVisiblePosition: Int, totalCount: Int, shouldBeLoaded: Int) {
					
					postDelayed({ mFuelHistoryAdapter.setNewData(data) }, 200)
					
				}
			})
		}
		
		
		binding.fabAddHistoryEntry.setDebounceOnClick {
			navController.navigate(R.id.action_fuelHistory_to_fuelHistoryAdd)
		}
	}
	
	override fun renderState(state: ViewState) {}

	
	private class FuelHistoryAdapter(private val data: MutableList<FuelHistoryRecord> = mutableListOf()) :
			BaseAdapter<FuelHistoryRecord>(data) {
		
		
		
		private var startPos = 0
		
		//decide to show Date separator or not
		override fun getLayoutIdForItem(position: Int): Int {
			return when {
				position == 0 -> R.layout.item_fuel_history_entry_sep
				getItem(position).date.getMonthInt() !=
						getItem(position - 1).date.getMonthInt() -> R.layout.item_fuel_history_entry_sep
				else -> R.layout.item_fuel_history_entry
			}
		}
		
		
		override fun setNewData(newData: List<FuelHistoryRecord>) {
			startPos = data.size
			data.addAll(newData)
			notifyItemRangeInserted(startPos, newData.size)
		}
	}
	
}