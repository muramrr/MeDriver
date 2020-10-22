/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.10.2020 19:42
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
	private val data: MutableList<VehicleSparePart> = mutableListOf()
) : RecyclerView.Adapter<MaintenanceHistoryAdapter.MaintenanceHistoryViewHolder>() {
	
	private companion object{
		private const val FIRST_POS = 0
	}
	private var startPos = 0
	
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
		notifyDataSetChanged()
	}
	
	
	fun insertPaginationData(newData: List<VehicleSparePart>) {
		startPos = data.size
		data.addAll(newData)
		notifyItemRangeInserted(startPos, newData.size)
	}
	
	inner class MaintenanceHistoryViewHolder(
		private var binding: ItemMaintenanceBinding
	): RecyclerView.ViewHolder(binding.root) {
		
		fun bind(item: VehicleSparePart) {
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
		
	}
	
}