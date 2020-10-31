/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.10.2020 18:03
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.vehicle

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.FragmentVehicleBinding
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import com.mmdev.me.driver.presentation.ui.common.BaseDropAdapter
import com.mmdev.me.driver.presentation.ui.vehicle.add.VehicleAddBottomSheet
import com.mmdev.me.driver.presentation.utils.extensions.domain.getOdometerFormatted
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *
 */

class VehicleFragment : BaseFlowFragment<VehicleViewModel, FragmentVehicleBinding>(
	layoutId = R.layout.fragment_vehicle
) {
	
	override val mViewModel: VehicleViewModel by viewModel()
	
	private lateinit var mVehicleDropAdapter: VehicleDropAdapter
	
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel.shouldBeUpdated.observe(this, {
			if (it) mViewModel.getSavedVehicles()
		})
	}
	
	override fun setupViews() {
		initDropList()
		
		observeVehicleList()
		observeChosenCar()
		
	}
	
	private fun initDropList() {
		mVehicleDropAdapter = VehicleDropAdapter(
			requireContext(),
			R.layout.item_drop_image_text,
			emptyList()
		)
		
		binding.dropMyCarChooseCar.setOnClickListener { showModalBottomSheet() }
		
		binding.dropMyCarChooseCar.apply {
			setAdapter(mVehicleDropAdapter)
			
			setOnItemClickListener { _, _, position, _ ->
				if (position == mVehicleDropAdapter.count - 1) showModalBottomSheet()
				else {
					mViewModel.chosenVehicle.postValue(mViewModel.vehicleList.value!![position])
					sharedViewModel.currentVehicle.postValue(mViewModel.vehicleList.value!![position])
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
			} ?: binding.dropMyCarChooseCar.setText("Add new car", false)
		})
	}
	
	private fun observeVehicleList() {
		mViewModel.vehicleList.observe(this, {
			mVehicleDropAdapter.setNewData(mViewModel.mapToUi(it))
			if (!it.isNullOrEmpty()) binding.dropMyCarChooseCar.setOnClickListener(null)
		})
	}
	
	
	
	private fun showModalBottomSheet() {
		val vehicleAdd = VehicleAddBottomSheet()
		vehicleAdd.show(childFragmentManager, VehicleAddBottomSheet::class.java.canonicalName)
	}
	
	private fun updateMileageCard(vehicle: Vehicle?) {
		binding.tvMileageValue.text = vehicle?.odometerValueBound?.getOdometerFormatted(requireContext()) ?: getString(R.string.default_OdometerValue)
	}
	
	
	
	
	
	private class VehicleDropAdapter(
		context: Context,
		@LayoutRes private val layoutId: Int,
		data: List<VehicleUi>
	): BaseDropAdapter<VehicleUi>(context, layoutId, data) {
		
		@SuppressLint("SetTextI18n")
		override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
			val vehicle: VehicleUi = getItem(position)
			val childView: View = convertView ?:
			                       LayoutInflater.from(context).inflate(layoutId, null)
			
			childView.findViewById<TextView>(R.id.tvDropItemText).text = vehicle.title
			childView.findViewById<ImageView>(R.id.ivDropItemIcon).setImageResource(vehicle.icon)
			return childView
		}
		
		
	}
}