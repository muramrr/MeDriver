/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.08.2020 20:07
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
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.FragmentMycarBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import com.mmdev.me.driver.presentation.ui.common.DropAdapter
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
		mViewModel.myCar.observe(this, {
			binding.dropMyCarChooseCar.setText(it.getFullCarTitle(), false)
		})
		
		val adapter = CarDropAdapter(requireContext(), R.layout.item_drop_mycar, list)
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
	
	
	
	
	private class CarDropAdapter(
		context: Context, @LayoutRes private val layoutId: Int, data: ArrayList<CarInDropDown>
	): DropAdapter<CarInDropDown>(context, layoutId, data) {
		
		override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
			val car: CarInDropDown = getItem(position)
			val childView : View = convertView ?:
			                       LayoutInflater.from(context).inflate(layoutId, null)
			
			childView.findViewById<TextView>(R.id.tvDropCar).text = car.getFullCarTitle()
			childView.findViewById<ImageView>(R.id.ivDropCarBrand).setImageResource(car.brandIcon)
			return childView
		}
	}
}