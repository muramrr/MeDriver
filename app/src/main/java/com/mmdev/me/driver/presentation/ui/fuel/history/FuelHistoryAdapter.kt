/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.09.2020 19:11
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.history

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.me.driver.BR
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.DateConverter.getMonthInt
import com.mmdev.me.driver.core.utils.MetricSystem.KILOMETERS
import com.mmdev.me.driver.core.utils.MetricSystem.MILES
import com.mmdev.me.driver.domain.fuel.history.model.FuelHistoryRecord
import com.mmdev.me.driver.presentation.utils.getStringRes


class FuelHistoryAdapter(
	private val data: MutableList<FuelHistoryRecord> = MutableList(5) { FuelHistoryRecord(0) }
) : RecyclerView.Adapter<FuelHistoryAdapter.PriceHistoryViewHolder>() {
	
	
	private companion object {
		private const val FIRST_POS = 0
	}
	private var startPos = 0
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		PriceHistoryViewHolder(
			DataBindingUtil.inflate(
				LayoutInflater.from(parent.context),
				viewType,
				parent,
				false
			)
		)
	
	override fun onBindViewHolder(holder: PriceHistoryViewHolder, position: Int) =
		holder.bind(data[position])
	
	override fun getItemCount(): Int = data.size
	
	override fun getItemViewType(position: Int): Int {
		return when {
			position == 0 -> R.layout.item_fuel_history_entry_sep
			
			data[position].date.getMonthInt() !=
					data[position - 1].date.getMonthInt() -> {
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
	
	inner class PriceHistoryViewHolder(private var binding: ViewDataBinding):
			RecyclerView.ViewHolder(binding.root) {
		
		
		fun bind(item: FuelHistoryRecord) {
			
			when(MedriverApp.metricSystem) {
				KILOMETERS -> {
					
					binding.root.findViewById<TextView>(R.id.tvHistoryEntryDistancePassed).apply {
						text = String.format(
							this.getStringRes(R.string.item_fuel_history_entry_distance_passed_km),
							item.distancePassed
						)
					}
					
					binding.root.findViewById<TextView>(R.id.tvHistoryEntryOdometer).apply {
						text = String.format(
							this.getStringRes(R.string.item_fuel_history_entry_odometer_km),
							item.odometerValue
						)
					}
					
					binding.root.findViewById<TextView>(R.id.tvHistoryEntryFuelConsumption).apply {
						text = String.format(
							this.getStringRes(R.string.item_fuel_history_entry_consumption_km),
							item.fuelConsumption
						)
					}
					
				}
				
				MILES -> {
					
					binding.root.findViewById<TextView>(R.id.tvHistoryEntryDistancePassed).apply {
						text = String.format(
							this.getStringRes(R.string.item_fuel_history_entry_distance_passed_mi),
							item.distancePassed
						)
					}
					
					binding.root.findViewById<TextView>(R.id.tvHistoryEntryOdometer).apply {
						text = String.format(
							this.getStringRes(R.string.item_fuel_history_entry_odometer_mi),
							item.odometerValue
						)
					}
					
					binding.root.findViewById<TextView>(R.id.tvHistoryEntryFuelConsumption).apply {
						text = String.format(
							this.getStringRes(R.string.item_fuel_history_entry_consumption_mi),
							item.fuelConsumption
						)
					}
				}
			}
			
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
		
		
	}
	
}