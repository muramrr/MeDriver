/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.10.2020 19:31
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.vehicle

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.FragmentVehicleBinding
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import com.mmdev.me.driver.presentation.ui.common.BaseDropAdapter
import com.mmdev.me.driver.presentation.utils.extensions.domain.getValue
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *
 */

class VehicleFragment : BaseFlowFragment<VehicleViewModel, FragmentVehicleBinding>(
	R.layout.fragment_vehicle
) {
	
	
	
	override val mViewModel: VehicleViewModel by viewModel()
	
	private lateinit var mVehicleDropAdapter: VehicleDropAdapter
	
	
	override fun setupViews() {
		initDropList()
		
		observeVehicleList()
		observeChosenCar()
		
	}

	override fun renderState(state: ViewState) {

	}
	
	private fun initDropList() {
		mVehicleDropAdapter = VehicleDropAdapter(
			requireContext(),
			R.layout.item_single_text,
			emptyList()
		)
		
		binding.dropMyCarChooseCar.setOnClickListener { showModalBottomSheet() }
		
		binding.dropMyCarChooseCar.apply {
			setAdapter(mVehicleDropAdapter)
			
			setOnItemClickListener { _, _, position, _ ->
				
				with(mVehicleDropAdapter.getItem(position)) {
					mViewModel.chosenVehicle.postValue(this)
					
					sharedViewModel.currentVehicle.postValue(this)
					
					binding.dropMyCarChooseCar.setText("${this.brand} ${this.model}", false)
				}
				
			}
		}
	}
	
	private fun observeChosenCar() {
		mViewModel.chosenVehicle.observe(this, { vehicle ->
			vehicle?.let {
				binding.dropMyCarChooseCar.setText("${it.brand} ${it.model}", false)
				sharedViewModel.currentVehicle.postValue(it)
				updateMileageCard(it)
			}
		})
	}
	
	private fun observeVehicleList() {
		mViewModel.vehicleList.observe(this, {
			mVehicleDropAdapter.updateData(it)
			if (!it.isNullOrEmpty()) binding.dropMyCarChooseCar.setOnClickListener(null)
		})
	}
	
	
	
	private fun showModalBottomSheet() {
		val vehicleAdd = VehicleAddBottomSheet()
		vehicleAdd.show(childFragmentManager, VehicleAddBottomSheet::class.java.canonicalName)
	}
	
	private fun updateMileageCard(vehicle: Vehicle?) {
		binding.tvMileageValue.text = vehicle?.odometerValueBound?.getValue()?.toString() ?:
		                              getString(R.string.default_OdometerValue)
		
		
	}
	
	
	
	
	
	private class VehicleDropAdapter(
		context: Context,
		@LayoutRes private val layoutId: Int,
		data: List<Vehicle>
	): BaseDropAdapter<Vehicle>(context, layoutId, data) {
		
		@SuppressLint("SetTextI18n")
		override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
			val vehicle: Vehicle = getItem(position)
			val childView : View = convertView ?:
			                       LayoutInflater.from(context).inflate(layoutId, null)
			
			childView.findViewById<TextView>(R.id.tvDropSingleText).text =
				"${vehicle.brand} ${vehicle.model} (${vehicle.year}), ${vehicle.engineCapacity}"
//			childView.findViewById<ImageView>(R.id.ivDropCarBrand).setImageResource(vehicle.brandIcon)
			return childView
		}
		
		
	}
}