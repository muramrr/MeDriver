/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 26.08.2020 03:11
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
import androidx.core.view.updateLayoutParams
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
import com.mmdev.me.driver.presentation.utils.setOnClickWithSelection
import com.mmdev.me.driver.presentation.utils.showSnack
import com.mmdev.me.driver.presentation.utils.updateMargins
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Fullscreen dialog used to add records to Fuel History
 * Hosted by FuelFragmentHistory
 */

class DialogFragmentHistoryAdd(private val mFuelStationWithPrices: List<FuelStationWithPrices>):
		DialogFragment() {
	
	private lateinit var binding: FragmentFuelHistoryAddBinding
	
	//get same scope as FuelFragmentHistory
	private val mViewModel: FuelHistoryViewModel by lazy { requireParentFragment().getViewModel() }
	
	
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
		//setup views behaviour
		changeFuelTypeButtonsSize()
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
				mViewModel.clearInputFields()
				dialog?.dismiss()
			}
		}
		
	}
	
	//todo: create custom viewGroup and custom rounded button
	private fun changeFuelTypeButtonsSize() {
		with(requireContext().resources.displayMetrics.widthPixels) {
			val itemWidth = this / 6
			val marginWidth = itemWidth / 4
			val finalItemSize = itemWidth - marginWidth
			
			binding.run {
				
				btnFuelTypeGas.apply {
					this.updateMargins(start = (marginWidth / 2), end = (marginWidth / 2))
					updateLayoutParams {
						height = finalItemSize
						width = finalItemSize
					}
				}
				btnFuelTypeDT.apply {
					this.updateMargins(start = (marginWidth / 2), end = (marginWidth / 2))
					updateLayoutParams {
						height = finalItemSize
						width = finalItemSize
					}
				}
				btnFuelType92.apply {
					this.updateMargins(start = (marginWidth / 2), end = (marginWidth / 2))
					updateLayoutParams {
						height = finalItemSize
						width = finalItemSize
					}
				}
				btnFuelType95.apply {
					this.updateMargins(start = (marginWidth / 2), end = (marginWidth / 2))
					updateLayoutParams {
						height = finalItemSize
						width = finalItemSize
					}
				}
				btnFuelType95PLUS.apply {
					this.updateMargins(start = (marginWidth / 2), end = (marginWidth / 2))
					updateLayoutParams {
						height = finalItemSize
						width = finalItemSize
					}
				}
				btnFuelType100.apply {
					this.updateMargins(start = (marginWidth / 2), end = (marginWidth / 2))
					updateLayoutParams {
						height = finalItemSize
						width = finalItemSize
					}
				}
				
			}
			
		}
	}
	
	private fun setupFuelButtons() {
		binding.run {
			fun deselectAllFuelButtons() {
				btnFuelType100.isSelected = false
				btnFuelType95PLUS.isSelected = false
				btnFuelType95.isSelected = false
				btnFuelType92.isSelected = false
				btnFuelTypeDT.isSelected = false
				btnFuelTypeGas.isSelected = false
			}
			
			btnFuelType100.setOnClickWithSelection {
				deselectAllFuelButtons()
				mViewModel.selectedFuelType.postValue(FuelType.A100)
			}
			
			btnFuelType95PLUS.setOnClickWithSelection {
				deselectAllFuelButtons()
				mViewModel.selectedFuelType.postValue(FuelType.A95PLUS)
			}
			
			btnFuelType95.setOnClickWithSelection {
				deselectAllFuelButtons()
				mViewModel.selectedFuelType.postValue(FuelType.A95)
			}
			
			btnFuelType92.setOnClickWithSelection {
				deselectAllFuelButtons()
				mViewModel.selectedFuelType.postValue(FuelType.A92)
			}
			
			btnFuelTypeDT.setOnClickWithSelection {
				deselectAllFuelButtons()
				mViewModel.selectedFuelType.postValue(FuelType.DT)
			}
			
			btnFuelTypeGas.setOnClickWithSelection {
				deselectAllFuelButtons()
				mViewModel.selectedFuelType.postValue(FuelType.GAS)
			}
		}
	}
	
	private fun setupInputStationDropList() {
		val adapter = FuelStationDropAdapter(
			requireContext(),
			R.layout.item_drop_fuel_station,
			ArrayList(FuelStationConstants.fuelStationList)
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
			if (it != null && it) binding.layoutInputFuelStation.error = "Empty field"
		})
		
	}
	
	private fun observeInputPrice() {
		mViewModel.fuelPriceRequires.observe(this, {
			if (it != null && it) binding.layoutInputPrice.error = "Price is not specified"
		})
	}
	
	private fun observeFuelType() {
		mViewModel.fuelTypeRequires.observe(this, {
			if (it == null || it) binding.root.showSnack("Fuel type is not selected")
		})
	}
	
	private fun observeInputOdometer() {
		mViewModel.odometerRequires.observe(this, {
			if (it != null && it) binding.layoutInputOdometer.error = "Empty field"
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