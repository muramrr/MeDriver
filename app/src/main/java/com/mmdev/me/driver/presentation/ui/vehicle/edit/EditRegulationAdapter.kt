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

package com.mmdev.me.driver.presentation.ui.vehicle.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.me.driver.BR
import com.mmdev.me.driver.databinding.ItemVehicleRegulationEditBinding
import com.mmdev.me.driver.domain.maintenance.data.components.PlannedParts.INSURANCE
import com.mmdev.me.driver.presentation.ui.vehicle.edit.EditRegulationAdapter.EditRegulationViewHolder
import com.mmdev.me.driver.presentation.utils.extensions.domain.buildDistanceBound
import com.mmdev.me.driver.presentation.utils.extensions.domain.getValue
import com.mmdev.me.driver.presentation.utils.extensions.domain.getYearsFormatted

/**
 *
 */

class EditRegulationAdapter(
	private var data: List<RegulationUi>
): RecyclerView.Adapter<EditRegulationViewHolder>() {
	
	fun getOutput() = data
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		EditRegulationViewHolder(
			ItemVehicleRegulationEditBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	
	override fun onBindViewHolder(holder: EditRegulationViewHolder, position: Int) =
		holder.bind(data[position])
	
	override fun getItemViewType(position: Int): Int = position
	
	override fun getItemCount(): Int = data.size
	
	fun setNewData(newData: List<RegulationUi>) {
		data = newData
		notifyDataSetChanged()
	}
	
	inner class EditRegulationViewHolder(private val binding: ItemVehicleRegulationEditBinding):
			RecyclerView.ViewHolder(binding.root){
		
		fun bind(item: RegulationUi) {
			
			binding.run {
				layoutInputRegulationDistanceValue.isEnabled = item.part != INSURANCE
				
				tvRegulationTitle.text = item.part.getSparePartName()
				etRegulationDistanceValue.setText(item.distance.getValue().toString())
				tvRegulationTimeValue.text = getYearsFormatted(item.yearsCount, root.context)
				
				etRegulationDistanceValue.doAfterTextChanged {
					if (!it.isNullOrBlank()) item.distance = buildDistanceBound(it.toString().toInt())
				}
				
				btnRegulationTimeDecrease.setOnClickListener {
					if (item.yearsCount > 1) item.yearsCount--
					tvRegulationTimeValue.text = getYearsFormatted(item.yearsCount, root.context)
				}
				
				btnRegulationTimeIncrease.setOnClickListener {
					if (item.yearsCount < 99) item.yearsCount++
					tvRegulationTimeValue.text = getYearsFormatted(item.yearsCount, root.context)
				}
			}
			
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
	}
	
	
}