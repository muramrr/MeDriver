/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 25.11.2020 20:51
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.me.driver.BR
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.ItemHomeGarageBinding
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.ui.home.MyGarageAdapter.MyGarageViewHolder
import com.mmdev.me.driver.presentation.utils.extensions.attachClickToCopyText

/**
 *
 */

class MyGarageAdapter(
	private var data: List<Vehicle> = emptyList()
): RecyclerView.Adapter<MyGarageViewHolder>() {
	
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyGarageViewHolder =
		MyGarageViewHolder(
			ItemHomeGarageBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	
	override fun onBindViewHolder(holder: MyGarageViewHolder, position: Int) = holder.bind(data[position])
	
	override fun getItemCount(): Int = data.size
	
	fun setNewData(newData: List<Vehicle>) {
		data = newData
		notifyDataSetChanged()
	}
	
	inner class MyGarageViewHolder(private val binding: ItemHomeGarageBinding):
			RecyclerView.ViewHolder(binding.root) {
		
		fun bind(item: Vehicle) {
			
			binding.btnCopyVehicleVin.attachClickToCopyText(
				binding.root.context,
				R.string.fg_vehicle_copy_vin
			)
			
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
		
	}
	
	
	
}