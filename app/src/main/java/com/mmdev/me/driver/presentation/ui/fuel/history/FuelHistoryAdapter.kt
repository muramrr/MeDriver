/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.09.2020 17:37
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.history

import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.utils.DateConverter.getMonthInt
import com.mmdev.me.driver.domain.fuel.history.model.FuelHistoryRecord
import com.mmdev.me.driver.presentation.ui.common.BaseAdapter



class FuelHistoryAdapter(
	private val data: MutableList<FuelHistoryRecord> = MutableList(5) { FuelHistoryRecord(0) }
) : BaseAdapter<FuelHistoryRecord>(data) {
	
	
	private companion object {
		private const val FIRST_POS = 0
	}
	private var startPos = 0
	
	//decide to show Date separator or not
	override fun getLayoutIdForItem(position: Int): Int {
		return when {
			position == 0 -> R.layout.item_fuel_history_entry_sep
			
			getItem(position).date.getMonthInt() !=
					getItem(position - 1).date.getMonthInt() -> {
				R.layout.item_fuel_history_entry_sep
			}
			
			else -> R.layout.item_fuel_history_entry
		}
	}
	
	fun setInitData(data: List<FuelHistoryRecord>) {
		this.data.clear()
		this.data.addAll(data)
		notifyDataSetChanged()
	}
	
	fun insertRecordOnTop(data: List<FuelHistoryRecord>) {
		this.data.addAll(FIRST_POS, data)
		notifyItemRangeInserted(FIRST_POS, data.size)
	}
	
	fun insertPaginationData(newData: List<FuelHistoryRecord>) {
		startPos = data.size
		data.addAll(newData)
		notifyItemRangeInserted(startPos, newData.size)
	}
	
	
}