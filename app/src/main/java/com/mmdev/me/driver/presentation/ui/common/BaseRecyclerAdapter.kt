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

package com.mmdev.me.driver.presentation.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.me.driver.BR

/**
 * A generic RecyclerView adapter that uses Data Binding & DiffUtil.
 *
 * @param <T> Type of the items in the list
 * @param <V> The type of the ViewDataBinding</V></T>
 */

abstract class BaseRecyclerAdapter<T>(
	private var data: List<T>,
	@LayoutRes private val layoutId: Int
): RecyclerView.Adapter<BaseRecyclerAdapter<T>.BaseViewHolder<T>>() {
	

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		BaseViewHolder<T>(
			DataBindingUtil.inflate(
				LayoutInflater.from(parent.context),
				viewType,
				parent,
				false
			)
		)

	override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) =
		holder.bind(getItem(position))

	// default data setter
	// override to implement another approach
	open fun setNewData(newData: List<T>) {
		data = newData
		notifyDataSetChanged()
	}

	override fun getItemViewType(position: Int) = layoutId

	override fun getItemCount(): Int = data.size

	protected fun getItem(position: Int): T = data[position]

	// allows clicks events to be caught
	open fun setOnItemClickListener(
		@IdRes viewId: Int? = null,
		listener: (view: View, position: Int, item: T) -> Unit
	) {
		this.viewId = viewId
		mClickListener = listener
	}
	
	@IdRes private var viewId: Int? = null
	
	// needed nullability to prevent attach click listener without handling it
	// clicks animation will be shown but not handled
	private var mClickListener: ((view: View, position: Int, item: T) -> Unit)? = null

//	override fun onFailedToRecycleView(holder: BaseViewHolder<T>): Boolean { return true }

	open inner class BaseViewHolder<T>(private val binding: ViewDataBinding):
			RecyclerView.ViewHolder(binding.root){

		init {
			if (viewId == null) {
				mClickListener?.let { clickListener ->
					binding.root.setOnClickListener {
						clickListener.invoke(it, adapterPosition, getItem(adapterPosition))
					}
				}
			}
		}

		open fun bind(item: T) {
			viewId?.let { viewId ->
				mClickListener?.let { clickListener ->
					binding.root.findViewById<View>(viewId).setOnClickListener {
						clickListener.invoke(it, adapterPosition, getItem(adapterPosition))
					}
				}
			}
			
			
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
	}
	
}