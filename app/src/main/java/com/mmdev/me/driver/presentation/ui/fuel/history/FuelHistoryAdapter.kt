/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.10.2020 19:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.me.driver.BR
import com.mmdev.me.driver.databinding.ItemFuelHistoryEntryBinding
import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory
import com.mmdev.me.driver.presentation.utils.extensions.gone


class FuelHistoryAdapter(
	private var data: MutableList<FuelHistory> = mutableListOf()
) : RecyclerView.Adapter<FuelHistoryAdapter.FuelHistoryViewHolder>() {
	
	
	private companion object {
		private const val FIRST_POS = 0
		private const val SHOW_MONTH_SEPARATOR = 0
		private const val HIDE_MONTH_SEPARATOR = 1
	}
	private var startPos = 0
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		FuelHistoryViewHolder(
			ItemFuelHistoryEntryBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			),
			viewType
		)
	
	override fun onBindViewHolder(holder: FuelHistoryViewHolder, position: Int) =
		holder.bind(data[position])
	
	override fun getItemCount(): Int = data.size
	
	override fun getItemViewType(position: Int): Int {
		return when {
			position == 0 -> SHOW_MONTH_SEPARATOR
			data[position].date.monthNumber !=
					data[position - 1].date.monthNumber -> { SHOW_MONTH_SEPARATOR }
			else -> HIDE_MONTH_SEPARATOR
		}
	}
	
	fun setInitData(data: List<FuelHistory>) {
		this.data.clear()
		this.data.addAll(data)
		notifyDataSetChanged()
	}
	
	fun insertPaginationData(newData: List<FuelHistory>) {
		startPos = data.size
		data.addAll(newData)
		notifyItemRangeInserted(startPos, newData.size)
	}
	
	inner class FuelHistoryViewHolder(
		private var binding: ItemFuelHistoryEntryBinding, viewType: Int
	): RecyclerView.ViewHolder(binding.root) {
		
		init {
			if (viewType == HIDE_MONTH_SEPARATOR) binding.tvFuelHistoryMonthSeparator.gone()
		}
		
		fun bind(item: FuelHistory) {
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
		
	}
	
}