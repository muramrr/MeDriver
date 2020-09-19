/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.09.2020 01:46
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
			DataBindingUtil.inflate(LayoutInflater.from(parent.context),
			                        viewType,
			                        parent,
			                        false)
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
	open fun setOnItemClickListener(listener: (view: View, position: Int, item: T) -> Unit) {
		mClickListener = listener
	}
	
	// needed nullability to prevent attach click listener without handling it
	// clicks animation will be shown but not handled
	private var mClickListener: ((view: View, position: Int, item: T) -> Unit)? = null

//	override fun onFailedToRecycleView(holder: BaseViewHolder<T>): Boolean { return true }

	open inner class BaseViewHolder<T>(private val binding: ViewDataBinding):
			RecyclerView.ViewHolder(binding.root){

		init {
			mClickListener?.let { mClickListener ->
				itemView.setOnClickListener {
					mClickListener.invoke(itemView, adapterPosition, getItem(adapterPosition))
				}
			}
		}

		open fun bind(item: T) {
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
	}
	
}