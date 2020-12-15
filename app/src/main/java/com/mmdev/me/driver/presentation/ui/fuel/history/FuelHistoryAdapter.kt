/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. medriver
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
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
import com.mmdev.me.driver.presentation.utils.extensions.invisible
import com.mmdev.me.driver.presentation.utils.extensions.visible
import com.mmdev.me.driver.presentation.utils.extensions.visibleIf


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
	private var deleteListener: ((View, Int, FuelHistory) -> Unit)? = null
	
	
	
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
			data[position].date.monthNumber != data[position - 1].date.monthNumber -> SHOW_MONTH_SEPARATOR
			else -> HIDE_MONTH_SEPARATOR
		}
	}
	
	fun delete(position: Int) {
		data.removeAt(position)
		itemsLoaded--
		notifyItemRemoved(position)
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
	
	fun setOnDeleteClickListener(listener: (view: View, position: Int, item: FuelHistory) -> Unit) {
		deleteListener = listener
	}
	
	inner class FuelHistoryViewHolder(
		private val binding: ItemFuelHistoryEntryBinding,
		private val viewType: Int
	): RecyclerView.ViewHolder(binding.root) {
		
		fun bind(item: FuelHistory) {
			
			if (adapterPosition == (data.size - 5))
				scrollToBottomListener?.invoke()
			
			if (itemsLoaded > data.size && adapterPosition == 10)
				scrollToTopListener?.invoke()
			
			binding.apply {
				if (viewType == HIDE_MONTH_SEPARATOR) tvFuelHistoryMonthSeparator.gone()
				else tvFuelHistoryMonthSeparator.visible()
				
				cvFuelHistoryEntryContainer.setOnClickListener {
					layoutControls.run {
						visibleIf(otherwise = View.INVISIBLE) { visibility == View.INVISIBLE }
					}
				}
				
				btnReturn.setOnClickListener { layoutControls.invisible() }
				btnDelete.setOnClickListener {
					deleteListener?.invoke(it, adapterPosition, data[adapterPosition])
					layoutControls.invisible()
				}
			}
			
			
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
		
	}
	
}