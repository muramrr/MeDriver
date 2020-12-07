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

package com.mmdev.me.driver.presentation.ui.vehicle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.me.driver.BR
import com.mmdev.me.driver.databinding.ItemConsumableReminderBinding
import com.mmdev.me.driver.presentation.utils.extensions.invisible
import com.mmdev.me.driver.presentation.utils.extensions.visible
import com.mmdev.me.driver.presentation.utils.extensions.visibleIf

/**
 *
 */

class ConsumablesAdapter(private var data: List<ConsumablePartUi> = emptyList()):
		RecyclerView.Adapter<ConsumablesAdapter.ConsumableViewHolder>() {
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		ConsumableViewHolder(
			ItemConsumableReminderBinding.inflate(
				LayoutInflater.from(parent.context), parent, false
			)
		)
	
	override fun onBindViewHolder(holder: ConsumableViewHolder, position: Int) =
		holder.bind(data[position])
	
	
	fun setNewData(newData: List<ConsumablePartUi>) {
		data = newData
		notifyDataSetChanged()
	}
	
	override fun getItemCount(): Int = data.size
	
	override fun getItemViewType(position: Int): Int = position
	
	inner class ConsumableViewHolder(private val binding: ItemConsumableReminderBinding):
			RecyclerView.ViewHolder(binding.root) {
		
		
		fun bind(item: ConsumablePartUi) {
			binding.apply {
				cvConsumablePart.setOnClickListener {
					radioChangeCalculation.run {
						visibleIf(otherwise = View.INVISIBLE) { this.visibility == View.INVISIBLE }
					}
				}
				
				radioChangeCalculation.setOnCheckedChangeListener { group, checkedId ->
					when (checkedId) {
						group.children.toList()[0].id -> {
							tvConsumableFinalDate.visible(0)
							tvConsumableDistanceLeft.invisible(0)
						}
						group.children.toList()[1].id -> {
							tvConsumableFinalDate.invisible(0)
							tvConsumableDistanceLeft.visible(0)
						}
					}
					group.invisible()
				}
			}
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
		
	}
}