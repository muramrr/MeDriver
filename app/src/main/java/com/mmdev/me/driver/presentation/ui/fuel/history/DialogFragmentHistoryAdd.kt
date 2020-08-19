/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.08.2020 01:31
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
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.FragmentFuelHistoryAddBinding
import com.mmdev.me.driver.databinding.ItemDropFuelStationBinding
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.prices.model.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStation
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStationWithPrices
import com.mmdev.me.driver.presentation.ui.common.DropAdapter
import com.mmdev.me.driver.presentation.ui.fuel.prices.FuelPricesViewModel
import com.mmdev.me.driver.presentation.utils.hideKeyboard
import com.mmdev.me.driver.presentation.utils.setOnClickWithSelection
import org.koin.androidx.viewmodel.compat.ViewModelCompat.getViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Fullscreen dialog used to add records to Fuel History
 * Hosted by FuelFragmentHistory
 */

class DialogFragmentHistoryAdd: DialogFragment() {
	
	private val mViewModel: FuelPricesViewModel by sharedViewModel()
	
	private val historyViewModel= getViewModel(
		requireParentFragment(), FuelHistoryViewModel::class.java
	)
	
	//no price stub
	private val noPrice = FuelPrice()
	//selected station in dropdown list
	private var selectedFuelStation: FuelStationWithPrices? = null
	
	private lateinit var binding: FragmentFuelHistoryAddBinding
	
	companion object {
		
		
		fun newInstance() = DialogFragmentHistoryAdd().apply {
			arguments = Bundle().apply {
			
			}
		}
	}
	
	
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(STYLE_NORMAL, R.style.My_Dialog_FullScreen)
		arguments?.let {
		
		}
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View =
		FragmentFuelHistoryAddBinding.inflate(inflater, container, false)
			.apply {
				binding = this
				executePendingBindings()
			}.root
	
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		setupViews()
	}
	
	private fun setupViews() {
		//clear focus + hide keyboard listener
		//used by edit inputs
		val editorActionListener = TextView.OnEditorActionListener { v, actionId, _ ->
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				v.hideKeyboard(v)
				return@OnEditorActionListener true
			}
			return@OnEditorActionListener false
		}
		
		val adapter = FuelStationDropAdapter(
			requireContext(),
			R.layout.item_drop_fuel_station,
			mViewModel.fuelPrices.value as ArrayList<FuelStationWithPrices>
		)
		
		binding.run {
			// drop down fuel station chooser
			dropFuelStationChoose.apply {
				setAdapter(adapter)
				
				setOnItemClickListener { _, _, position, _ ->
					with(adapter.getItem(position)) {
						//cast to global variable current selected station
						selectedFuelStation = this
						setText(this.fuelStation.brandTitle, false).also {
							hideKeyboard(this@apply)
						}
					}
				}
			}
			
			//hide keyboard + clear focus
			root.setOnTouchListener { rootView, _ ->
				rootView.performClick()
				rootView.hideKeyboard(rootView)
			}
			
			//clear focus + hide keyboard by pressing on ime Done button
			etInputOdometer.setOnEditorActionListener(editorActionListener)
			dropFuelStationChoose.setOnEditorActionListener(editorActionListener)
			
			btnFuelType100.setOnClickWithSelection {
				deselectAll()
				etInputPrice.setText(getPriceByType(FuelType.A100))
			}
			
			btnFuelType95PLUS.setOnClickWithSelection {
				deselectAll()
				etInputPrice.setText(getPriceByType(FuelType.A95PLUS))
			}
			
			btnFuelType95.setOnClickWithSelection {
				deselectAll()
				etInputPrice.setText(getPriceByType(FuelType.A95))
			}
			
			btnFuelType92.setOnClickWithSelection {
				deselectAll()
				etInputPrice.setText(getPriceByType(FuelType.A92))
			}
			
			btnFuelTypeDT.setOnClickWithSelection {
				deselectAll()
				etInputPrice.setText(getPriceByType(FuelType.DT))
			}
			
			btnFuelTypeGas.setOnClickWithSelection {
				deselectAll()
				etInputPrice.setText(getPriceByType(FuelType.GAS))
			}
			
			
			btnDone.setOnClickListener { dialog?.dismiss() }
			btnCancel.setOnClickListener { dialog?.dismiss() }
		}
	}
	
	private fun getPriceByType(fuelType: FuelType): String = with(fuelType) {
		selectedFuelStation?.prices?.find { it.type == this } ?: noPrice
	}.priceString
	
	private fun deselectAll() {
		binding.run {
			btnFuelType100.isSelected = false
			btnFuelType95PLUS.isSelected = false
			btnFuelType95.isSelected = false
			btnFuelType92.isSelected = false
			btnFuelTypeDT.isSelected = false
			btnFuelTypeGas.isSelected = false
		}
	}
	
	
	
	private class FuelStationDropAdapter(
		context: Context, @LayoutRes private val layoutId: Int, data: ArrayList<FuelStationWithPrices>
	): DropAdapter<FuelStationWithPrices>(context, layoutId, data) {
		
		private lateinit var binding: ItemDropFuelStationBinding
		private lateinit var fuelStation: FuelStation
		
		@SuppressLint("ViewHolder")
		override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
			binding = DataBindingUtil.inflate(
				LayoutInflater.from(context), layoutId, parent, false
			)
			
			fuelStation = getItem(position).fuelStation
			binding.tvFuelStationTitle.text = fuelStation.brandTitle
			binding.ivDropFuelStationIcon.setImageResource(fuelStation.brandIcon)
			return binding.root
		}
		
		
	}
}