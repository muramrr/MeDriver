/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.09.2020 04:04
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
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.me.driver.BR
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.ItemFuelPricesStationBinding
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.prices.model.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStationWithPrices
import com.mmdev.me.driver.presentation.utils.getStringRes

/**
 *
 */

class FuelPricesAdapter (
	
	private var data: MutableList<FuelStationWithPrices> = MutableList(10){ FuelStationWithPrices() }

) : RecyclerView.Adapter<FuelPricesAdapter.PriceViewHolder>(){
	
	//no price stub
	private val noPrice = FuelPrice()
	
	// price appearance anim
	private lateinit var inAnim: Animation
	private lateinit var outAnim: Animation
	
	private var priceFormatter = ""
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		PriceViewHolder(
			DataBindingUtil.inflate(
				LayoutInflater.from(parent.context), R.layout.item_fuel_prices_station, parent, false
			)
		)
	
	override fun onBindViewHolder(holder: PriceViewHolder, position: Int) =
		holder.bind(getItem(position))
	
	
	fun setNewData(newData: List<FuelStationWithPrices>) {
		data.clear()
		data.addAll(newData)
		notifyDataSetChanged()
	}
	
	override fun getItemCount(): Int = data.size
	
	private fun getItem(position: Int): FuelStationWithPrices = data[position]
	
	inner class PriceViewHolder(private val binding: ItemFuelPricesStationBinding):
			RecyclerView.ViewHolder(binding.root) {
		
		init {
			inAnim = AnimationUtils.loadAnimation(binding.root.context, android.R.anim.fade_in).apply {
				duration = 300
			}
			// price disappearing anim
			outAnim = AnimationUtils.loadAnimation(binding.root.context, android.R.anim.fade_out).apply {
				duration = 300
			}
			priceFormatter = binding.root.getStringRes(R.string.price_formatter)
		}
		
		
		
		fun bind(item: FuelStationWithPrices) {
			
			//apply anim to textSwitcher
			binding.tvFuelPrice.apply {
				inAnimation = inAnim
				outAnimation = outAnim
			}
			
			//init A95 price for all
			binding.tvFuelPrice.setCurrentText(
				priceFormatter.format(getPriceByType(item, FuelType.A95))
			).also {
				binding.radioFuelTypes.setInitialSelected(R.id.btnFuelType95)
			}
			
			binding.radioFuelTypes.setOnClickListener { _, id ->
				when (id) {
					R.id.btnFuelTypeGas -> binding.tvFuelPrice.setText(
						priceFormatter.format(getPriceByType(item, FuelType.GAS))
					)
					R.id.btnFuelTypeDT -> binding.tvFuelPrice.setText(
						priceFormatter.format(getPriceByType(item, FuelType.DT))
					)
					R.id.btnFuelType92 -> binding.tvFuelPrice.setText(
						priceFormatter.format(getPriceByType(item, FuelType.A92))
					)
					R.id.btnFuelType95 -> binding.tvFuelPrice.setText(
						priceFormatter.format(getPriceByType(item, FuelType.A95))
					)
					R.id.btnFuelType95PLUS -> binding.tvFuelPrice.setText(
						priceFormatter.format(getPriceByType(item, FuelType.A95PLUS))
					)
					R.id.btnFuelType98 -> binding.tvFuelPrice.setText(
						priceFormatter.format(getPriceByType(item, FuelType.A98))
					)
					R.id.btnFuelType100 -> binding.tvFuelPrice.setText(
						priceFormatter.format(getPriceByType(item, FuelType.A100))
					)
				}
			}
			
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
		
		private fun getPriceByType(item: FuelStationWithPrices, fuelType: FuelType): String =
			(item.prices.find { it.type == fuelType } ?: noPrice).priceString()
		
		
		private fun FuelPrice.priceString(): String = if (price != 0.0) price.toString() else "--.--"
		
	}
	
	
}
