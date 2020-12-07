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

package com.mmdev.me.driver.presentation.ui.maintenance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.me.driver.BR
import com.mmdev.me.driver.databinding.ItemMaintenanceBinding
import com.mmdev.me.driver.domain.maintenance.data.VehicleSparePart

/**
 *
 */

class MaintenanceHistoryAdapter(
	private var data: MutableList<VehicleSparePart> = mutableListOf()
) : RecyclerView.Adapter<MaintenanceHistoryAdapter.MaintenanceHistoryViewHolder>() {
	
	private companion object{
		private const val FIRST_POS = 0
		private const val OPTIMAL_ITEMS_COUNT = 40
	}
	private var startPos = 0
	private var itemsLoaded = 0
	
	private var scrollToTopListener: (() -> Unit)? = null
	private var scrollToBottomListener: (() -> Unit)? = null
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		MaintenanceHistoryViewHolder(
			ItemMaintenanceBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	
	override fun onBindViewHolder(holder: MaintenanceHistoryViewHolder, position: Int) =
		holder.bind(data[position])
	
	override fun getItemCount(): Int = data.size
	
	fun setInitData(data: List<VehicleSparePart>) {
		this.data.clear()
		startPos = FIRST_POS
		this.data.addAll(data)
		itemsLoaded = data.size
		notifyDataSetChanged()
	}
	
	
	fun insertPreviousData(topData: List<VehicleSparePart>) {
		data.addAll(FIRST_POS, topData)
		notifyItemRangeInserted(FIRST_POS, topData.size)
		
		if (data.size > OPTIMAL_ITEMS_COUNT) {
			val shouldBeRemovedCount = data.size - OPTIMAL_ITEMS_COUNT
			data = data.dropLast(shouldBeRemovedCount).toMutableList()
			itemsLoaded -= shouldBeRemovedCount
			notifyItemRangeRemoved((data.size - 1), shouldBeRemovedCount)
		}
	}
	
	
	fun insertNextData(bottomData: List<VehicleSparePart>) {
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
	
	inner class MaintenanceHistoryViewHolder(
		private val binding: ItemMaintenanceBinding
	): RecyclerView.ViewHolder(binding.root) {
		
		fun bind(item: VehicleSparePart) {
			
			if (adapterPosition > 9 && adapterPosition == (data.size - 10))
				scrollToBottomListener?.invoke()
			
			if (itemsLoaded > data.size && adapterPosition == 10)
				scrollToTopListener?.invoke()
			
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
		
	}
	
}