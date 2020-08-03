/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 01.08.20 20:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.mycar

import androidx.annotation.DrawableRes
import androidx.lifecycle.Observer
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.FragmentMycarBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFragment

/**
 *
 */

class MyCarFragment : BaseFragment<MyCarViewModel, FragmentMycarBinding>(R.layout.fragment_mycar) {
	
	
	data class CarInDropDown (val carBrand: String,
	                          val carModel: String,
	                          @DrawableRes val brandIcon: Int) {
		fun getFullCarTitle(): String = "$carBrand $carModel"
	}
	
	override val mViewModel: MyCarViewModel = MyCarViewModel()
	
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
}