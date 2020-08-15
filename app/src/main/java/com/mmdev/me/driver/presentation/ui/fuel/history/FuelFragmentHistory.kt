/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 15.08.2020 20:56
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
import com.mmdev.me.driver.core.utils.logDebug
import com.mmdev.me.driver.databinding.FragmentFuelHistoryBinding
import com.mmdev.me.driver.domain.fuel.model.FuelHistoryRecord
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFragment
import com.mmdev.me.driver.presentation.ui.common.BaseAdapter
import com.mmdev.me.driver.presentation.ui.fuel.FuelViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 *
 */

class FuelFragmentHistory: BaseFragment<FuelViewModel, FragmentFuelHistoryBinding>(
	R.layout.fragment_fuel_history
) {
	override val mViewModel: FuelViewModel by sharedViewModel()
	
	private val mFuelHistoryAdapter = FuelHistoryAdapter()
	
	
	override fun setupViews() {
		binding.rvFuelHistory.apply {
			adapter = mFuelHistoryAdapter
			layoutManager = LinearLayoutManager(requireContext())
		}
		
		mFuelHistoryAdapter.setNewData(listOf(
			FuelHistoryRecord(date = DateConverter.toDate("20-01-2020")),
			FuelHistoryRecord(date = DateConverter.toDate("20-01-2020")),
			FuelHistoryRecord(date = DateConverter.toDate("20-01-2020")),
			FuelHistoryRecord(date = DateConverter.toDate("20-03-2020")),
			FuelHistoryRecord(date = DateConverter.toDate("20-04-2020")),
			FuelHistoryRecord(date = DateConverter.toDate("20-04-2020")),
			FuelHistoryRecord(date = DateConverter.toDate("20-04-2020")),
			FuelHistoryRecord(date = DateConverter.toDate("20-04-2020"))
		))
		
	}
	
	override fun renderState(state: ViewState) {}

	
	private class FuelHistoryAdapter(private val data: MutableList<FuelHistoryRecord> = mutableListOf()) :
			BaseAdapter<FuelHistoryRecord>(data, R.layout.item_fuel_history_entry) {
		
		
		private var firstHeaderCreated = false
		
		private var startPos = 0
		
		//decide to show Date separator or not
		override fun getLayoutIdForItem(position: Int): Int {
			if (!firstHeaderCreated && position == 0)
				return R.layout.item_fuel_history_entry_sep.also {
					firstHeaderCreated = true
					logDebug(message = "first iter = $firstHeaderCreated")
				}
			else if (position < itemCount - 1) {
				if (getItem(position).date.getMonthInt() !=
				    getItem(position + 1).date.getMonthInt())
					return R.layout.item_fuel_history_entry_sep.also {
						logDebug(message = "second iter = $firstHeaderCreated")
					}
				else {
					firstHeaderCreated = false
					return R.layout.item_fuel_history_entry.also {
						logDebug(message = "third iter = $firstHeaderCreated")
					}
				}
			}
			else return if (getItem(position).date.getMonthInt() !=
					getItem(position - 1).date.getMonthInt())
				R.layout.item_fuel_history_entry_sep
			else R.layout.item_fuel_history_entry
		}
		
		
		override fun setNewData(newData: List<FuelHistoryRecord>) {
			startPos = data.size - 1
			data.addAll(newData)
			notifyItemRangeInserted(startPos, newData.size)
		}
	}
	
}