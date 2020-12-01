/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 01.12.2020 20:51
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.me.driver.BR
import com.mmdev.me.driver.databinding.ItemFuelHistoryEntryBinding
import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory
import com.mmdev.me.driver.presentation.utils.extensions.gone
import com.mmdev.me.driver.presentation.utils.extensions.visible


class FuelHistoryAdapter(
	private var data: MutableList<FuelHistory> = mutableListOf()
) : RecyclerView.Adapter<FuelHistoryAdapter.FuelHistoryViewHolder>() {
	
	
	private companion object {
		private const val SHOW_MONTH_SEPARATOR = 0
		private const val HIDE_MONTH_SEPARATOR = 1
		private const val FIRST_POS = 0
		private const val OPTIMAL_ITEMS_COUNT = 40
	}
	private var startPos = 0
	private var itemsLoaded = 0
	
	private var scrollToTopListener: (() -> Unit)? = null
	private var scrollToBottomListener: (() -> Unit)? = null
	private var mItemClickListner: ((View, Int, FuelHistory) -> Unit)? = null
	
	
	
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
		startPos = FIRST_POS
		this.data.addAll(data)
		itemsLoaded = data.size
		notifyDataSetChanged()
	}
	
	
	fun insertPreviousData(topData: List<FuelHistory>) {
		data.addAll(FIRST_POS, topData)
		notifyItemRangeInserted(FIRST_POS, topData.size)
		
		if (data.size > OPTIMAL_ITEMS_COUNT) {
			val shouldBeRemovedCount = data.size - OPTIMAL_ITEMS_COUNT
			data = data.dropLast(shouldBeRemovedCount).toMutableList()
			itemsLoaded -= shouldBeRemovedCount
			notifyItemRangeRemoved((data.size - 1), shouldBeRemovedCount)
		}
	}
	
	
	fun insertNextData(bottomData: List<FuelHistory>) {
		startPos = data.size
		data.addAll(bottomData)
		itemsLoaded += bottomData.size
		notifyItemRangeInserted(startPos, bottomData.size)
		if (data.size > OPTIMAL_ITEMS_COUNT) {
			val shouldBeRemovedCount = data.size - OPTIMAL_ITEMS_COUNT
			data = data.drop(shouldBeRemovedCount).toMutableList()
			notifyItemRangeRemoved(FIRST_POS, shouldBeRemovedCount)
		}
	}
	
	fun setToTopScrollListener(listener: () -> Unit) {
		scrollToTopListener = listener
	}
	
	fun setToBottomScrollListener(listener: () -> Unit) {
		scrollToBottomListener = listener
	}
	
	fun setOnItemClickListener(listener: (view: View, position: Int, item: FuelHistory) -> Unit) {
		mItemClickListner = listener
	}
	
	inner class FuelHistoryViewHolder(
		private val binding: ItemFuelHistoryEntryBinding,
		private val viewType: Int
	): RecyclerView.ViewHolder(binding.root) {
		
		init {
			mItemClickListner?.let { listener ->
				binding.cvFuelHistoryEntryContainer.setOnClickListener {
					listener.invoke(it, adapterPosition, data[adapterPosition])
				}
			}
		}
		
		fun bind(item: FuelHistory) {
			
			if (viewType == HIDE_MONTH_SEPARATOR) binding.tvFuelHistoryMonthSeparator.gone()
			else binding.tvFuelHistoryMonthSeparator.visible()
			
			if (adapterPosition == (data.size - 5))
				scrollToBottomListener?.invoke()
			
			if (itemsLoaded > data.size && adapterPosition == 10)
				scrollToTopListener?.invoke()
			
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
		
	}
	
}