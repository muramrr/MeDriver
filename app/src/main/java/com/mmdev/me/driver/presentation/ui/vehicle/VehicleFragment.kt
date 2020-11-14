/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 14.11.2020 17:48
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
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.databinding.FragmentVehicleBinding
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import com.mmdev.me.driver.presentation.ui.MainActivity
import com.mmdev.me.driver.presentation.ui.common.BaseDropAdapter
import com.mmdev.me.driver.presentation.ui.vehicle.add.VehicleAddBottomSheet
import com.mmdev.me.driver.presentation.utils.extensions.domain.getOdometerFormatted
import com.mmdev.me.driver.presentation.utils.extensions.getStringRes
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
import com.mmdev.me.driver.presentation.utils.extensions.text
import com.mmdev.me.driver.presentation.utils.extensions.visibleIf
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *
 */

class VehicleFragment : BaseFlowFragment<VehicleViewModel, FragmentVehicleBinding>(
	layoutId = R.layout.fragment_vehicle
) {
	
	override val mViewModel: VehicleViewModel by viewModel()
	
	private lateinit var mVehicleDropAdapter: VehicleDropAdapter
	
	private var currentTextOnDropDownList = ""
	
	
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
		
		binding.btnMileageHistory.setDebounceOnClick {
			MainActivity.bottomNavMain.selectedItemId = R.id.bottomNavFuel
		}
	}
	
	private fun initDropList() {
		mVehicleDropAdapter = VehicleDropAdapter(
			requireContext(),
			R.layout.item_drop_vehicles,
			emptyList()
		)
		
		binding.dropMyCarChooseCar.apply {
			setAdapter(mVehicleDropAdapter)
			
			setOnItemClickListener { _, view, position, _ ->
				
				if (view.isEnabled) {
					if (position == mVehicleDropAdapter.count - 1) {
						
						setText(currentTextOnDropDownList, false)
						showModalBottomSheet()
						
					}
					else {
						mViewModel.setVehicle(position)
					}
				}
				else setText(currentTextOnDropDownList, false)
				
			}
			
		}
	}
	
	private fun observeChosenCar() {
		mViewModel.chosenVehicle.observe(this, { vehicle ->
			if (vehicle != null)
				binding.dropMyCarChooseCar.setText("${vehicle.brand} ${vehicle.model}", false)
			
			updateMileageCard(vehicle)
			sharedViewModel.currentVehicle.postValue(vehicle)
			currentTextOnDropDownList = binding.dropMyCarChooseCar.text()
		})
	}
	
	private fun observeVehicleList() {
		mViewModel.vehicleUiList.observe(this, {
			mVehicleDropAdapter.setNewData(it)
			when {
				!it.isNullOrEmpty() -> {
					binding.dropMyCarChooseCar.setOnClickListener(null)
					if (mViewModel.chosenVehicle.value == null)
						binding.dropMyCarChooseCar.setText(R.string.fg_vehicle_choose_vehicle)
				}
				it.isNullOrEmpty() -> {
					binding.dropMyCarChooseCar.setOnClickListener { showModalBottomSheet() }
					binding.dropMyCarChooseCar.setText(R.string.fg_vehicle_add_new_vehicle)
				}
			}
			currentTextOnDropDownList = binding.dropMyCarChooseCar.text()
		})
	}
	
	
	
	private fun showModalBottomSheet() = VehicleAddBottomSheet().show(
		childFragmentManager, VehicleAddBottomSheet::class.java.canonicalName
	)
	
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
			
			childView.findViewById<TextView>(R.id.tvDropCarItemText).apply {
				text = if (vehicle.titleRes != null) getStringRes(vehicle.titleRes) else vehicle.title
				isEnabled = (position == 0) || ((position != 0) && MedriverApp.currentUser != null && MedriverApp.currentUser!!.isSubscriptionValid())
			}
			childView.findViewById<ImageView>(R.id.ivDropCarItemIcon).apply {
				setImageResource(vehicle.icon)
				isEnabled = (position == 0) || ((position != 0) && MedriverApp.currentUser != null && MedriverApp.currentUser!!.isSubscriptionValid())
			}
			//if no premium, only first position will be available
			childView.isEnabled = (position == 0) || ((position != 0) && MedriverApp.currentUser != null && MedriverApp.currentUser!!.isSubscriptionValid())
			childView.findViewById<TextView>(R.id.tvDropCarItemProLabel).visibleIf(otherwise = View.INVISIBLE) {
				position == count - 1 && MedriverApp.currentUser != null && !MedriverApp.currentUser!!.isSubscriptionValid()
			}
			return childView
		}
		
		
	}
}