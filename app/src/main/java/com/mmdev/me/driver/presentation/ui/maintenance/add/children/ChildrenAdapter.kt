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
	private var data: IntArray //string resources
) : RecyclerView.Adapter<ChildComponentViewHolder>() {
	
	
	// Keeps track of all the selected items from list to their positions
	val selectedChildren: MutableMap<Int, Int> = mutableMapOf()
	
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
	fun setNewData(newData: IntArray) {
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
	fun setOnItemClickListener(listener: (view: View, position: Int, stringRes: Int) -> Unit) {
		mClickListener = listener
	}
	
	// needed nullability to prevent attach click listener without handling it
	// clicks animation will be shown but not handled
	private var mClickListener: ((view: View, position: Int, stringRes: Int) -> Unit)? = null
	

	
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
		
		fun bind(stringRes: Int) {
			
			binding.tvChildComponentTitleCheckable.isChecked = selectedChildren[adapterPosition] != null
			
		
			binding.tvChildComponentTitleCheckable.setOnCheckedChangeListener { _, isChecked ->
				if (isChecked) selectedChildren[adapterPosition] = stringRes
				else selectedChildren.remove(adapterPosition)
			}
			
			binding.setVariable(BR.multiSelect, multiSelect)
			binding.setVariable(BR.bindItem, stringRes)
			binding.executePendingBindings()
		}
	}
}