/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 24.11.2020 20:30
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.prices

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.me.driver.BR
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.ItemFuelPricesStationBinding
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.prices.data.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStationWithPrices
import com.mmdev.me.driver.presentation.utils.extensions.getStringRes

/**
 *
 */

class FuelPricesAdapter (
	private var data: List<FuelStationWithPrices> = emptyList()
): RecyclerView.Adapter<FuelPricesAdapter.PriceViewHolder>(){
	
	//no price stub
	private val noPrice = FuelPrice()
	
	// price appearance anim
	private lateinit var inAnim: Animation
	private lateinit var outAnim: Animation
	
	private var priceFormatter = ""
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		PriceViewHolder(
			ItemFuelPricesStationBinding.inflate(
				LayoutInflater.from(parent.context), parent, false
			)
		)
	
	override fun onBindViewHolder(holder: PriceViewHolder, position: Int) =
		holder.bind(data[position])
	
	
	fun setNewData(newData: List<FuelStationWithPrices>) {
		data = newData
		notifyDataSetChanged()
	}
	
	override fun getItemCount(): Int = data.size
	
	override fun getItemViewType(position: Int): Int = position
	
	inner class PriceViewHolder(private val binding: ItemFuelPricesStationBinding):
			RecyclerView.ViewHolder(binding.root) {
		
		
		init {
			inAnim = AnimationUtils.loadAnimation(binding.root.context, android.R.anim.fade_in).apply {
				duration = 200
			}
			// price disappearing anim
			outAnim = AnimationUtils.loadAnimation(binding.root.context, android.R.anim.fade_out).apply {
				duration = 200
			}
			//apply anim to textSwitcher
			binding.tvFuelPrice.apply {
				inAnimation = inAnim
				outAnimation = outAnim
			}
			
			priceFormatter = binding.root.getStringRes(R.string.price_formatter_left)
		}
		
		fun bind(item: FuelStationWithPrices) {
			
			binding.radioFuelTypes.addOnButtonCheckedListener { group, checkedId, isChecked ->
				binding.tvFuelPrice.setText(invokePriceSearch(item, checkedId))
			}
			
			//if no button checked (init state)
			if (binding.radioFuelTypes.checkedButtonId == -1) {
				//init A95 price for all
				binding.radioFuelTypes.check(R.id.btnFuelType95)
			} else {
				binding.tvFuelPrice.setText(
					invokePriceSearch(item, binding.radioFuelTypes.checkedButtonId)
				)
			}

			
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
		
		private fun invokePriceSearch(item: FuelStationWithPrices, buttonId: Int): String =
			when(buttonId) {
				R.id.btnFuelTypeGas -> priceFormatter.format(getPriceByType(item, FuelType.GAS))
				R.id.btnFuelTypeDT -> priceFormatter.format(getPriceByType(item, FuelType.DT))
				R.id.btnFuelType92 -> priceFormatter.format(getPriceByType(item, FuelType.A92))
				R.id.btnFuelType95 -> priceFormatter.format(getPriceByType(item, FuelType.A95))
				R.id.btnFuelType95PLUS -> priceFormatter.format(getPriceByType(item, FuelType.A95PLUS))
				R.id.btnFuelType98 -> priceFormatter.format(getPriceByType(item, FuelType.A98))
				R.id.btnFuelType100 -> priceFormatter.format(getPriceByType(item, FuelType.A100))
				else -> "--.--"
			}
		
		
		private fun getPriceByType(item: FuelStationWithPrices, fuelType: FuelType): String =
			(item.prices.find { it.type == fuelType } ?: noPrice).priceString()
		
		private fun FuelPrice.priceString(): String = if (price != 0.0) price.toString() else "--.--"
	}
	
}