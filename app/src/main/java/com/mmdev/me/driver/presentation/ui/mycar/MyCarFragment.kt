/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 11.08.20 17:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.mycar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.lifecycle.Observer
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.FragmentMycarBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *
 */

class MyCarFragment : BaseFlowFragment<MyCarViewModel, FragmentMycarBinding>(
	R.layout.fragment_mycar
) {
	
	
	data class CarInDropDown (val carBrand: String,
	                          val carModel: String,
	                          @DrawableRes val brandIcon: Int) {
		fun getFullCarTitle(): String = "$carBrand $carModel"
	}
	
	override val mViewModel: MyCarViewModel by viewModel()
	
	private val list : ArrayList<CarInDropDown> = arrayListOf(
			CarInDropDown("Ford", "Focus",
			              R.drawable.hatchback),
			CarInDropDown("Range Rover", "Sport",
			              R.drawable.motorcycle),
			CarInDropDown("Add another ","car",
			              R.drawable.cuv)
	)
	
	override fun setupViews() {
		mViewModel.myCar.observe(this, Observer {
			binding.dropMyCarChooseCar.setText(it.getFullCarTitle(), false)
		})
		
		val adapter = DropAdapter(requireContext(), list)
		binding.dropMyCarChooseCar.apply {
			setAdapter(adapter)
			
			setOnItemClickListener { _, _, position, _ ->
				if (position != adapter.count - 1)
					mViewModel.myCar.value = adapter.getItem(position)
				else binding.dropMyCarChooseCar.setText("Choose car", false)
				
			}
		}
	}

	override fun renderState(state: ViewState) {

	}
	
	
	
	
	private class DropAdapter(context: Context, private val data: ArrayList<CarInDropDown>):
			ArrayAdapter<CarInDropDown>(context, R.layout.item_drop_mycar, data) {
		
		override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
			val car: CarInDropDown = getItem(position)
			val childView : View = convertView ?:
			                       LayoutInflater.from(context).inflate(R.layout.item_drop_mycar, null)
			
			childView.findViewById<TextView>(R.id.tvDropCar).text = car.getFullCarTitle()
			childView.findViewById<ImageView>(R.id.ivDropCarBrand).setImageResource(car.brandIcon)
			return childView
		}
		
		override fun getItem(position: Int): CarInDropDown = data[position]
	}
}