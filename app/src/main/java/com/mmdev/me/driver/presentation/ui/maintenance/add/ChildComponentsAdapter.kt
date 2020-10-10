/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.10.2020 20:05
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance.add

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.me.driver.BR
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.ItemMaintenanceChildComponentBinding

/**
 *
 */

class ChildComponentsAdapter(
	private var data: List<String>
) : RecyclerView.Adapter<ChildComponentsAdapter.ChildComponentViewHolder>() {
	
	
	// Keeps track of all the selected images
	private val selectedItems = arrayListOf<String>()
	
	// true if the user in selection mode, false otherwise
	// private flag
	private var multiSelect = false
	// public mutable value to change parent behaviour
	// this primary happens because of long pressed click listener attached inside ViewHolder
	// without outer receiver
	val multiSelectState: MutableLiveData<Boolean> = MutableLiveData(false)
	
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		ChildComponentViewHolder(
			DataBindingUtil.inflate(
				LayoutInflater.from(parent.context),
				R.layout.item_maintenance_child_component,
				parent,
				false)
		)
	
	override fun onBindViewHolder(holder: ChildComponentViewHolder, position: Int) =
		holder.bind(data[position])
	
	// default data setter
	// override to implement another approach
	fun setNewData(newData: List<String>) {
		data = newData
		notifyDataSetChanged()
	}
	
	override fun getItemCount(): Int = data.size
	
	fun turnOffMultiSelect() {
		if (multiSelect) {
			multiSelect = false
			multiSelectState.postValue(false)
			selectedItems.clear()
			notifyItemRangeChanged(0, data.size)
		}
	}
	
	fun toggleMultiSelect() {
		multiSelect = !multiSelect
		multiSelectState.postValue(multiSelect)
		//clear list only once to do less job
		if (multiSelect) selectedItems.clear()
		notifyItemRangeChanged(0, data.size)
	}
	
	// allows clicks events to be caught
	fun setOnItemClickListener(listener: (view: View, position: Int, item: String) -> Unit) {
		mClickListener = listener
	}
	
	// needed nullability to prevent attach click listener without handling it
	// clicks animation will be shown but not handled
	private var mClickListener: ((view: View, position: Int, item: String) -> Unit)? = null
	

	
	inner class ChildComponentViewHolder(private val binding: ItemMaintenanceChildComponentBinding):
			RecyclerView.ViewHolder(binding.root){
		
		init {
			mClickListener?.let { mClickListener ->
				itemView.setOnClickListener {
					mClickListener.invoke(itemView, adapterPosition, data[adapterPosition])
				}
			}
			itemView.setOnLongClickListener {
				toggleMultiSelect()
				true
			}
		}
		
		fun bind(item: String) {
			
			binding.tvChildComponentTitleCheckable.isChecked = false
		
			binding.tvChildComponentTitleCheckable.setOnCheckedChangeListener { _, isChecked ->
				if (isChecked) selectedItems.add(item)
				else selectedItems.remove(item)
			}
			
			binding.setVariable(BR.multiSelect, multiSelect)
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
	}
}