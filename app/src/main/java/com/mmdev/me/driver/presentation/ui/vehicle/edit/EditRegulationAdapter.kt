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
import com.google.android.material.textfield.TextInputEditText
import com.mmdev.me.driver.BR
import com.mmdev.me.driver.databinding.ItemVehicleRegulationEditBinding
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.maintenance.data.components.OtherParts
import com.mmdev.me.driver.domain.maintenance.data.components.PlannedParts
import com.mmdev.me.driver.domain.maintenance.data.components.PlannedParts.INSURANCE
import com.mmdev.me.driver.presentation.ui.maintenance.VehicleSystemNodeConstants
import com.mmdev.me.driver.presentation.ui.vehicle.edit.EditRegulationAdapter.EditRegulationViewHolder
import com.mmdev.me.driver.presentation.utils.extensions.domain.buildDistanceBound
import com.mmdev.me.driver.presentation.utils.extensions.domain.getValue
import com.mmdev.me.driver.presentation.utils.extensions.domain.getYearsFormatted
import com.mmdev.me.driver.presentation.utils.extensions.hideKeyboard

/**
 *
 */

class EditRegulationAdapter(
	private var data: List<RegulationUi> = List(PlannedParts.values().size) {
		RegulationUi(OtherParts.OTHER, DistanceBound(0, 0), 0)
	}
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
	
	override fun getItemCount(): Int = data.size
	
	override fun getItemViewType(position: Int): Int = position
	
	fun setNewData(newData: List<RegulationUi>) {
		data = newData
		notifyDataSetChanged()
	}
	
	inner class EditRegulationViewHolder(private val binding: ItemVehicleRegulationEditBinding):
			RecyclerView.ViewHolder(binding.root){
		
		fun bind(item: RegulationUi) {
			
			binding.run {
				//hide keyboard + clear focus while tapping somewhere on root view
				root.setOnTouchListener { rootView, _ ->
					rootView.performClick()
					rootView.hideKeyboard(rootView)
				}
				
				//disable distance input for insurance
				layoutInputRegulationDistanceValue.isEnabled = item.part != INSURANCE
				
				//init titles
				tvRegulationTitle.text = root.context.getString(
					VehicleSystemNodeConstants.plannedComponents[item.part.getSparePartOrdinal()]
				)
				//set initial text
				etRegulationDistanceValue.setText(item.distance.getValue().toString())
				//set initial years
				tvRegulationTimeValue.text = getYearsFormatted(item.yearsCount, root.context)
				
				//apply listeners for distance input
				etRegulationDistanceValue.run {
					doAfterTextChanged {
						if (!it.isNullOrBlank()) item.distance = buildDistanceBound(it.toString().toInt())
					}
					setOnFocusChangeListener { view, hasFocus ->
						(view as TextInputEditText).run {
							//clear previous value when user taps on input
							if (hasFocus) view.setText("")
							else {
								//if field is empty after input finished -> set last satisfied value
								if (view.text.isNullOrBlank()) view.setText(item.distance.getValue().toString())
							}
						}
					}
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