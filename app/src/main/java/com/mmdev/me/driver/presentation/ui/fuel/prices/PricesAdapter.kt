/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.08.20 20:29
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.prices

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.me.driver.BR
import com.mmdev.me.driver.R
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.model.FuelProvider
import kotlinx.android.synthetic.main.item_fuel_provider.view.*

/**
 *
 */

class PricesAdapter (private var data: MutableList<FuelProvider> = MutableList(10){ FuelProvider() } ) :
		
		RecyclerView.Adapter<PricesAdapter.PriceViewHolder>(){
	
	private var mClickListener: OnItemClickListener<FuelProvider>? = null
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		PriceViewHolder(DataBindingUtil.inflate(
				LayoutInflater.from(parent.context),
				R.layout.item_fuel_provider,
				parent,
				false
			)
		)
	
	override fun onBindViewHolder(holder: PriceViewHolder, position: Int) =
		holder.bind(getItem(position))
	
	
	fun setNewData(newData: List<FuelProvider>) {
		data.clear()
		data.addAll(newData)
		notifyDataSetChanged()
	}
	
	fun setNewData(newData: FuelProvider, position: Int) {
		data[position] = newData
		notifyItemChanged(position)
	}
	
	override fun getItemCount(): Int = data.size
	
	private fun getItem(position: Int): FuelProvider = data[position]
	
	// allows clicks events to be caught
	fun setOnItemClickListener(itemClickListener: OnItemClickListener<FuelProvider>) {
		mClickListener = itemClickListener
	}
	
	//	override fun onFailedToRecycleView(holder: BaseViewHolder<T>): Boolean { return true }
	
	inner class PriceViewHolder(private val binding: ViewDataBinding):
			RecyclerView.ViewHolder(binding.root){
		
		fun bind(item: FuelProvider) {
			mClickListener?.let { mClickListener ->
				binding.root.btnFuelTypeProvider100.setOnClickListener {
					mClickListener.onItemClick(
						getItem(adapterPosition),
						adapterPosition,
						FuelType.A100)
				}
				binding.root.btnFuelTypeProvider95PLUS.setOnClickListener {
					mClickListener.onItemClick(
						getItem(adapterPosition),
						adapterPosition,
						FuelType.A95PLUS)
				}
				binding.root.btnFuelTypeProvider95.setOnClickListener {
					mClickListener.onItemClick(
						getItem(adapterPosition),
						adapterPosition,
						FuelType.A95)
				}
				binding.root.btnFuelTypeProvider92.setOnClickListener {
					mClickListener.onItemClick(
						getItem(adapterPosition),
						adapterPosition,
						FuelType.A92)
				}
				binding.root.btnFuelTypeProviderDT.setOnClickListener {
					mClickListener.onItemClick(
						getItem(adapterPosition),
						adapterPosition,
						FuelType.DT)
				}
				binding.root.btnFuelTypeProviderGas.setOnClickListener {
					mClickListener.onItemClick(
						getItem(adapterPosition),
						adapterPosition,
						FuelType.GAS)
				}
			}
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
	}
	
	// parent fragment will override this method to respond to click events
	interface OnItemClickListener<T> {
		fun onItemClick(item: T, position: Int, fuelType: FuelType)
	}
	
	
}
