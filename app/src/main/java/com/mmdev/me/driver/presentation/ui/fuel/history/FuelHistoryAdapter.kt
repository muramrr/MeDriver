/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.09.2020 20:55
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.me.driver.BR
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.DateConverter.getMonthInt
import com.mmdev.me.driver.core.utils.MetricSystem.KILOMETERS
import com.mmdev.me.driver.core.utils.MetricSystem.MILES
import com.mmdev.me.driver.databinding.ItemFuelHistoryEntryBinding
import com.mmdev.me.driver.domain.fuel.history.model.FuelHistoryRecord
import com.mmdev.me.driver.presentation.utils.getStringRes
import com.mmdev.me.driver.presentation.utils.gone


class FuelHistoryAdapter(
	private val data: MutableList<FuelHistoryRecord> = MutableList(5) { FuelHistoryRecord(0) }
) : RecyclerView.Adapter<FuelHistoryAdapter.PriceHistoryViewHolder>() {
	
	
	private companion object {
		private const val FIRST_POS = 0
		private const val SHOW_MOUNTH_SEPARATOR = 0
		private const val HIDE_MOUNTH_SEPARATOR = 1
	}
	private var startPos = 0
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		PriceHistoryViewHolder(
			DataBindingUtil.inflate(
				LayoutInflater.from(parent.context),
				R.layout.item_fuel_history_entry,
				parent,
				false
			),
			viewType
		)
	
	override fun onBindViewHolder(holder: PriceHistoryViewHolder, position: Int) =
		holder.bind(data[position])
	
	override fun getItemCount(): Int = data.size
	
	override fun getItemViewType(position: Int): Int {
		return when {
			position == 0 -> SHOW_MOUNTH_SEPARATOR
			data[position].date.getMonthInt() !=
					data[position - 1].date.getMonthInt() -> { SHOW_MOUNTH_SEPARATOR }
			else -> HIDE_MOUNTH_SEPARATOR
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
	
	inner class PriceHistoryViewHolder(
		private var binding: ItemFuelHistoryEntryBinding, viewType: Int
	): RecyclerView.ViewHolder(binding.root) {
		
		init {
			if (viewType == HIDE_MOUNTH_SEPARATOR) binding.tvFuelHistoryMonthSeparator.gone()
		}
		
		fun bind(item: FuelHistoryRecord) {
			when(MedriverApp.metricSystem) {
				
				KILOMETERS -> {
					
					binding.tvHistoryEntryDistancePassed.apply {
						text = getStringRes(R.string.item_fuel_history_entry_distance_passed_km)
							.format(item.distancePassed())
					}
					
					binding.tvHistoryEntryOdometer.apply {
						text = this.getStringRes(R.string.item_fuel_history_entry_odometer_km)
							.format(item.odometerValue())
					}
					
					binding.tvHistoryEntryFuelConsumption.apply {
						text = getStringRes(R.string.item_fuel_history_entry_consumption_km)
							.format(item.fuelConsumption())
					}
				}
				
				MILES -> {
					
					binding.tvHistoryEntryDistancePassed.apply {
						text = getStringRes(R.string.item_fuel_history_entry_distance_passed_mi)
							.format(item.distancePassed())
					}
					
					binding.tvHistoryEntryOdometer.apply {
						text = getStringRes(R.string.item_fuel_history_entry_odometer_mi)
							.format(item.odometerValue())
					}
					
					binding.tvHistoryEntryFuelConsumption.apply {
						text = getStringRes(R.string.item_fuel_history_entry_consumption_mi)
							.format(item.fuelConsumption())
					}
				}
			}
			
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
		
		
	}
	
}