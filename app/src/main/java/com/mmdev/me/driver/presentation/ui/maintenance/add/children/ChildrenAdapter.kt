/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.10.2020 17:17
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance.add.children

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.me.driver.BR
import com.mmdev.me.driver.databinding.ItemMaintenanceChildBinding
import com.mmdev.me.driver.presentation.ui.maintenance.add.children.ChildrenAdapter.ChildComponentViewHolder

/**
 * Adapter for displaying list of children
 */

class ChildrenAdapter(
	private var data: List<String>
) : RecyclerView.Adapter<ChildComponentViewHolder>() {
	
	
	// Keeps track of all the selected images and their positions
	val selectedChildren = arrayListOf<Pair<String, Int>>()
	
	// true if the user in selection mode, false otherwise
	// private flag
	private var multiSelect = false
	
	
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		ChildComponentViewHolder(
			ItemMaintenanceChildBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	
	override fun onBindViewHolder(holder: ChildComponentViewHolder, position: Int) =
		holder.bind(data[position])
	
	/** this will prevent to recycle viewHolders
	 * this affects situations when you input some inside first view then scroll to bottom and see
	 * same inputs on another view
	 * to avoid this make each view separated and prevent reusing created views
	 * we do not have a lot of objects so performance decreasing is not significant
	 */
	override fun getItemViewType(position: Int): Int = position
	
	// default data setter
	// override to implement another approach
	fun setNewData(newData: List<String>) {
		data = newData
		notifyDataSetChanged()
	}
	
	override fun getItemCount(): Int = data.size
	
	fun toggleMultiSelect(): Boolean {
		multiSelect = !multiSelect
		//clear list only once to do less job
		if (multiSelect) selectedChildren.clear()
		notifyItemRangeChanged(0, data.size)
		return multiSelect
	}
	
	fun turnOffMultiSelect(): Boolean {
		multiSelect = false
		selectedChildren.clear()
		notifyItemRangeChanged(0, data.size)
		return multiSelect
	}
	
	// allows clicks events to be caught
	fun setOnItemClickListener(listener: (view: View, position: Int, item: String) -> Unit) {
		mClickListener = listener
	}
	
	// needed nullability to prevent attach click listener without handling it
	// clicks animation will be shown but not handled
	private var mClickListener: ((view: View, position: Int, item: String) -> Unit)? = null
	

	
	inner class ChildComponentViewHolder(private val binding: ItemMaintenanceChildBinding):
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
			
			binding.tvChildComponentTitleCheckable.isChecked =
				selectedChildren.find { it.second == adapterPosition } != null
			
		
			binding.tvChildComponentTitleCheckable.setOnCheckedChangeListener { _, isChecked ->
				if (isChecked) selectedChildren.add(Pair(item, adapterPosition))
				else selectedChildren.remove(Pair(item, adapterPosition))
			}
			
			binding.setVariable(BR.multiSelect, multiSelect)
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
	}
}