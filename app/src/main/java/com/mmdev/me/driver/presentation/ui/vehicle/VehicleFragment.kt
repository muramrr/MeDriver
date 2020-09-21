/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.09.2020 19:34
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.vehicle

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.FragmentVehicleBinding
import com.mmdev.me.driver.domain.fuel.history.model.DistanceBound
import com.mmdev.me.driver.domain.vehicle.model.Vehicle
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import com.mmdev.me.driver.presentation.ui.common.BaseDropAdapter
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
		observeVehicleList()
		observeChosenCar()
		
		mVehicleDropAdapter = VehicleDropAdapter(
				requireContext(),
				R.layout.drop_item_vehicle,
				emptyList()
		)
		
		binding.dropMyCarChooseCar.apply {
			setAdapter(mVehicleDropAdapter)
			
			setOnItemClickListener { _, _, position, _ ->
				
				mViewModel.chosenVehicle.postValue(mVehicleDropAdapter.getItem(position))
				
				sharedViewModel.currentVehicle.postValue(mVehicleDropAdapter.getItem(position))
			}
		}
		
		
	}

	override fun renderState(state: ViewState) {

	}
	
	private fun observeChosenCar() {
		mViewModel.chosenVehicle.observe(this, { vehicle ->
		//	binding.dropMyCarChooseCar.setText("${it.brand} ${it.model}", false)
			vehicle?.let {
				binding.dropMyCarChooseCar.listSelection = mVehicleDropAdapter.getVehiclePosition(it)
			}
			
		})
	}
	
	private fun observeVehicleList() {
		mViewModel.vehicleList.observe(this, {
			mVehicleDropAdapter.updateVehicleList(it)
		})
	}
	
	
	
	
	
	
	//temporary
	private companion object {
		//private val vehiclesBrand = listOf("")
		
		//private val vehiclesIcon = R.array.vehicles_logo
		
		//val map = vehiclesBrand.zip(vehiclesIcon).toMap().withDefault { 0 }
		
		val ford = Vehicle(
			"Ford",
			"Focus",
			2013,
			"VINFORD",
			DistanceBound(0, 0)
		)
		
		val landRover = Vehicle(
			"Land Rover",
			"Discovery",
			2012,
			"VINLAND",
			DistanceBound(0, 0)
		)
	}
	
	
	
	
	
	
	
	private class VehicleDropAdapter(
		context: Context,
		@LayoutRes private val layoutId: Int,
		private var data: List<Vehicle>
	): BaseDropAdapter<Vehicle>(context, layoutId, data) {
		
		override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
			val vehicle: Vehicle = getItem(position)
			val childView : View = convertView ?:
			                       LayoutInflater.from(context).inflate(layoutId, null)
			
			childView.findViewById<TextView>(R.id.tvDropCar).text = "${vehicle.brand} ${vehicle.model}"
//			childView.findViewById<ImageView>(R.id.ivDropCarBrand).setImageResource(vehicle.brandIcon)
			return childView
		}
		
		fun getVehiclePosition(vehicle: Vehicle) = data.indexOf(vehicle)
		
		fun updateVehicleList(data: List<Vehicle>) {
			this.data = data
			notifyDataSetChanged()
		}
	}
}