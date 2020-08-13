/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 13.08.20 20:09
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.prices

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.me.driver.BR
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.ItemFuelStationBinding
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.model.FuelStation
import com.mmdev.me.driver.presentation.utils.setOnClickWithSelection

/**
 *
 */

class PricesAdapter (private var data: MutableList<FuelStation> = MutableList(10){ FuelStation() } ) :
		RecyclerView.Adapter<PricesAdapter.PriceViewHolder>(){
	
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		PriceViewHolder(DataBindingUtil.inflate(
			LayoutInflater.from(parent.context),
			R.layout.item_fuel_station,
			parent,
			false
			)
		)
	
	override fun onBindViewHolder(holder: PriceViewHolder, position: Int) =
		holder.bind(getItem(position))
	
	
	fun setNewData(newData: List<FuelStation>) {
		data.clear()
		data.addAll(newData)
		notifyDataSetChanged()
	}
	
	override fun getItemCount(): Int = data.size
	
	private fun getItem(position: Int): FuelStation = data[position]
	
	inner class PriceViewHolder(private val binding: ItemFuelStationBinding):
			RecyclerView.ViewHolder(binding.root) {
		
		fun bind(item: FuelStation) {
			binding.run {
				
				// price appearance anim
				val inAnimName = AnimationUtils.loadAnimation(root.context,
				                                              android.R.anim.fade_in).apply {
					duration = 300
				}
				// price disappearing anim
				val outAnimName = AnimationUtils.loadAnimation(root.context,
				                                               android.R.anim.fade_out).apply {
					duration = 300
				}
				
				//apply anim to textSwitcher
				tvFuelPrice.apply {
					inAnimation = inAnimName
					outAnimation = outAnimName
				}
				
				//init A95 price for all
				tvFuelPrice.setCurrentText(getPriceByType(item, FuelType.A95)).also {
					btnFuelType95.isSelected = true
				}
				
				btnFuelType100.setOnClickWithSelection {
					deselectAll()
					tvFuelPrice.setText(getPriceByType(item, FuelType.A100))
				}
				
				btnFuelType95PLUS.setOnClickWithSelection {
					deselectAll()
					tvFuelPrice.setText(getPriceByType(item, FuelType.A95PLUS))
				}
				
				btnFuelType95.setOnClickWithSelection {
					deselectAll()
					tvFuelPrice.setText(getPriceByType(item, FuelType.A95))
				}
				
				btnFuelType92.setOnClickWithSelection {
					deselectAll()
					tvFuelPrice.setText(getPriceByType(item, FuelType.A92))
				}
				
				btnFuelTypeDT.setOnClickWithSelection {
					deselectAll()
					tvFuelPrice.setText(getPriceByType(item, FuelType.DT))
				}
				
				btnFuelTypeGas.setOnClickWithSelection {
					deselectAll()
					tvFuelPrice.setText(getPriceByType(item, FuelType.GAS))
				}
				
			}
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
		
		private fun getPriceByType(item: FuelStation, fuelType: FuelType): String =
			with(fuelType) {
				item.prices.find { it.type == this } ?: FuelStation.FuelPrice(this)
			}.price
		
		private fun deselectAll() {
			binding.run {
				btnFuelType100.isSelected = false
				btnFuelType95PLUS.isSelected = false
				btnFuelType95.isSelected = false
				btnFuelType92.isSelected = false
				btnFuelTypeDT.isSelected = false
				btnFuelTypeGas.isSelected = false
			}
		}
	}
	
	
}
