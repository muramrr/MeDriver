/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.09.2020 19:26
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.history

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.FragmentFuelHistoryAddBinding
import com.mmdev.me.driver.databinding.ItemDropFuelStationBinding
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStation
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStationWithPrices
import com.mmdev.me.driver.presentation.ui.common.DropAdapter
import com.mmdev.me.driver.presentation.ui.fuel.FuelStationConstants
import com.mmdev.me.driver.presentation.utils.hideKeyboard
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Fullscreen dialog used to add records to Fuel History
 * Hosted by FuelFragmentHistory
 */

internal class DialogFragmentHistoryAdd(fuelStationWithPrices: List<FuelStationWithPrices>):
		DialogFragment() {
	
	private val TAG = javaClass.simpleName
	
	private lateinit var binding: FragmentFuelHistoryAddBinding
	private var mFuelStationWithPrices: List<FuelStationWithPrices> = emptyList()
	
	//get same scope as FuelFragmentHistory
	private val mViewModel: FuelHistoryViewModel by lazy { requireParentFragment().getViewModel() }
	
	//init local list based on what we've got from constructor
	init {
		mFuelStationWithPrices =
			if (fuelStationWithPrices.isNullOrEmpty())
				FuelStationConstants.fuelStationList.map { FuelStationWithPrices(it) }
			else fuelStationWithPrices
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(STYLE_NORMAL, R.style.My_Dialog_FullScreen)
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View =
		FragmentFuelHistoryAddBinding.inflate(inflater, container, false)
			.apply {
				binding = this
				lifecycleOwner = viewLifecycleOwner
				viewModel = mViewModel
				executePendingBindings()
			}.root
	
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = setupViews()
	
	private fun setupViews() {
		//setup views
		setupFuelButtons()
		setupInputStationDropList()
		
		//setup observers
		observeInputStation()
		observeInputPrice()
		observeFuelType()
		observeInputOdometer()
		
		binding.run {
			//hide keyboard + clear focus while tapping somewhere on root view
			root.setOnTouchListener { rootView, _ ->
				rootView.performClick()
				rootView.hideKeyboard(rootView)
			}
			
			btnCancel.setOnClickListener {
				mViewModel.clearInputFields()
				dialog?.dismiss()
			}
			
			btnDone.setOnClickListener {
				mViewModel.addHistoryRecord().also {
					mViewModel.clearInputFields()
					dialog?.dismiss()
				}
			}
		}
		
	}

	private fun setupFuelButtons() {
		binding.radioFuelTypes.setOnClickListener { _, id ->
			when (id) {
				R.id.btnFuelTypeGas -> mViewModel.selectFuelType(FuelType.GAS)
				R.id.btnFuelTypeDT -> mViewModel.selectFuelType(FuelType.DT)
				R.id.btnFuelType92 -> mViewModel.selectFuelType(FuelType.A92)
				R.id.btnFuelType95 -> mViewModel.selectFuelType(FuelType.A95)
				R.id.btnFuelType95PLUS -> mViewModel.selectFuelType(FuelType.A95PLUS)
				R.id.btnFuelType98 -> mViewModel.selectFuelType(FuelType.A98)
				R.id.btnFuelType100 -> mViewModel.selectFuelType(FuelType.A100)
			}
		}
	}
	
	private fun setupInputStationDropList() {
		val adapter = FuelStationDropAdapter(
			requireContext(),
			R.layout.item_drop_fuel_station,
			ArrayList(mFuelStationWithPrices.map { it.fuelStation })
		)
		// drop down fuel station chooser
		binding.etFuelStationDrop.apply {
			
			setAdapter(adapter)
			
			setOnItemClickListener { _, _, position, _ ->
				with(adapter.getItem(position)) {
					//cast to global variable current selected station
					mViewModel.findFuelStationBySlug(this.slug, mFuelStationWithPrices)
					
					setText(this.brandTitle, false).also {
						hideKeyboard(this@apply)
					}
				}
			}
		}
	}
	
	private fun observeInputStation() {
		mViewModel.fuelStationRequires.observe(this, {
			if (it != null && it) binding.layoutInputFuelStation.error = getString(R.string.fg_fuel_history_add_fuel_station_input_error)
		})
		
	}
	
	private fun observeInputPrice() {
		mViewModel.fuelPriceRequires.observe(this, {
			if (it != null && it) binding.layoutInputPrice.error = getString(R.string.fg_fuel_history_add_fuel_price_input_error)
		})
	}
	
	private fun observeFuelType() {
		mViewModel.fuelTypeRequires.observe(this, {
			//if (it == null || it) binding.root.showSnack(getString(R.string .fg_fuel_history_add_fuel_type_error))
		})
	}
	
	private fun observeInputOdometer() {
		mViewModel.odometerRequires.observe(this, {
			if (it != null && it) binding.layoutInputOdometer.error = getString(R.string.fg_fuel_history_add_odometer_input_error)
		})
	}
	
	
	private class FuelStationDropAdapter(
		private val mContext: Context,
		@LayoutRes private val layoutId: Int,
		data: ArrayList<FuelStation>
	): DropAdapter<FuelStation>(mContext, layoutId, data) {
		
		private lateinit var binding: ItemDropFuelStationBinding
		private lateinit var fuelStation: FuelStation
		
		@SuppressLint("ViewHolder")
		override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
			binding = DataBindingUtil.inflate(
				LayoutInflater.from(mContext), layoutId, parent, false
			)
			
			fuelStation = getItem(position)
			binding.tvFuelStationTitle.text = fuelStation.brandTitle
			binding.ivDropFuelStationIcon.setImageResource(fuelStation.brandIcon)
			return binding.root
		}
		
	}
	
}