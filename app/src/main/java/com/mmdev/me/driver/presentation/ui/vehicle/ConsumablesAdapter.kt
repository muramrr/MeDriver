/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.11.2020 21:31
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
					radioConsumableChangeCalculation.radioChangeCalculation.run {
						visibleIf(otherwise = View.INVISIBLE) { this.visibility == View.INVISIBLE }
					}
				}
				
				radioConsumableChangeCalculation.radioChangeCalculation.setOnCheckedChangeListener { group, checkedId ->
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