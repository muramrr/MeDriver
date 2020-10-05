/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.10.2020 19:39
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.history

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import com.mmdev.me.driver.core.utils.MetricSystem
import com.mmdev.me.driver.core.utils.convertToLocalDateTime
import com.mmdev.me.driver.databinding.DialogFuelHistoryAddBinding
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStation
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStationWithPrices
import com.mmdev.me.driver.presentation.core.base.BaseDialogFragment
import com.mmdev.me.driver.presentation.ui.common.BaseDropAdapter
import com.mmdev.me.driver.presentation.ui.fuel.FuelStationConstants
import com.mmdev.me.driver.presentation.ui.fuel.brandIcon
import com.mmdev.me.driver.presentation.ui.fuel.getValue
import com.mmdev.me.driver.presentation.utils.hideKeyboard
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.toInstant
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.util.*

/**
 * Fullscreen dialog used to add records to Fuel History
 * Hosted by FuelFragmentHistory
 */

class FuelHistoryAddDialog(
	fuelStationWithPrices: List<FuelStationWithPrices>
): BaseDialogFragment<FuelHistoryViewModel, DialogFuelHistoryAddBinding>(
	layoutId = R.layout.dialog_fuel_history_add
)   {
	
	private var mFuelStationWithPrices: List<FuelStationWithPrices> = emptyList()
	
	//get same scope as FuelFragmentHistory
	override val mViewModel: FuelHistoryViewModel by lazy { requireParentFragment().getViewModel() }
	
	private val distancePassedAnimator = ValueAnimator.ofInt().apply { duration = 1200 }
	
	private var distancePassedValueDefault = ""
	private var distancePassedSubtitleValueFormatter = ""
	
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
	
	override fun setupViews() {
		//setup views
		initStringRes()
		
		setupDatePicker()
		setupFuelButtons()
		setupInputStationDropList()
		
		//setup observers
		observeDistancePassed()
		observeInputFuelStation()
		observeInputPrice()
		observeFuelType()
		observeInputOdometer()
		
		binding.run {
			//hide keyboard + clear focus while tapping somewhere on root view
			root.setOnTouchListener { rootView, _ ->
				rootView.performClick()
				rootView.hideKeyboard(rootView)
			}
			
			btnCancel.setOnClickListener { dialog?.dismiss() }
			
			btnDone.setOnClickListener {
				mViewModel.addHistoryRecord(MedriverApp.currentUser).also { dialog?.dismiss() }
			}
			
			tvDistancePassedSubtitle.text = distancePassedSubtitleValueFormatter.format(
				mViewModel.history.value?.odometerValueBound?.getValue() ?: 0
			)
		
		}
	}
	
	private fun initStringRes() {
		//Show strings according to current metric system
		when (MedriverApp.metricSystem) {
			MetricSystem.KILOMETERS -> {
				
				distancePassedAnimator.apply {
					addUpdateListener {
						binding.tvDistancePassedValue.text = String.format(
							getString(R.string.fg_fuel_history_add_distance_passed_value_km),
							it.animatedValue.toString()
						)
					}
				}
				
				distancePassedValueDefault =
					getString(R.string.fg_fuel_history_add_distance_passed_value_default_km)
				
				distancePassedSubtitleValueFormatter =
					getString(R.string.fg_fuel_history_add_distance_passed_subtitle_value_km)
				
			}
			
			MetricSystem.MILES -> {
				
				distancePassedAnimator.apply {
					addUpdateListener {
						binding.tvDistancePassedValue.text = String.format(
							getString(R.string.fg_fuel_history_add_distance_passed_value_mi),
							it.animatedValue.toString()
						)
					}
				}
				
				distancePassedValueDefault =
					getString(R.string.fg_fuel_history_add_distance_passed_value_default_mi)
				
				distancePassedSubtitleValueFormatter =
					getString(R.string.fg_fuel_history_add_distance_passed_subtitle_value_mi)
				
			}
		}
	}

	@SuppressLint("SetTextI18n")
	private fun setupDatePicker() {
		val calendar = Calendar.getInstance(TimeZone.getDefault())
		val currentYear = calendar.get(Calendar.YEAR)
		val currentMonth = calendar.get(Calendar.MONTH)
		val currentMonthDisplay = currentMonth + 1 // january corresponds to 0
		val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
		
		val pickedDate = Calendar.getInstance(TimeZone.getDefault())
		
		binding.btnDatePicker.text =
			(if (currentDay < 10) "0$currentDay." else "$currentDay.") +
			(if (currentMonthDisplay < 10) "0$currentMonthDisplay" else "$currentMonthDisplay") +
			".$currentYear"
		
		val datePickerDialog = DatePickerDialog(requireContext(), {
				_, pickedYear, pickedMonth, pickedDay ->
			
			val pickedMonthDisplay = pickedMonth + 1 // january corresponds to 0
			
			// Display Selected date in Button
			binding.btnDatePicker.text =
				(if (pickedDay < 10) "0$pickedDay." else "$pickedDay.") +
				(if (pickedMonthDisplay < 10) "0$pickedMonthDisplay" else "$pickedMonthDisplay") +
				".$pickedYear"
			
			// set picked date to mViewModel
			pickedDate.set(pickedYear, pickedMonth, pickedDay)
			mViewModel.pickedDate = convertToLocalDateTime(pickedDate.timeInMillis)
			
			
		}, currentYear, currentMonth, currentDay)
		
		// restrict dates min = last added history date if exists or start of epoch
		datePickerDialog.datePicker.minDate = mViewModel.history.value!!
			.date.toInstant(currentSystemDefault()).toEpochMilliseconds()
		
		// max = current date
		datePickerDialog.datePicker.maxDate = calendar.timeInMillis
		
		binding.btnDatePicker.setOnClickListener {
			datePickerDialog.show()
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
			R.layout.drop_item_fuel_station,
			mFuelStationWithPrices.map { it.fuelStation }
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
	
	private fun observeDistancePassed() {
		var oldValue = 0
		
		mViewModel.distancePassed.observe(this, { distancePassed ->
			//cancel existing animation if such exists
			distancePassedAnimator.cancel()
			with(distancePassed) {
				oldValue = if (this > 0) {
					/**
					 * check if oldValue is same with given [distancePassed] if no ->
					 * set oldValue as start
					 */
					if (this != oldValue) distancePassedAnimator.setIntValues(oldValue, this)
					else distancePassedAnimator.setIntValues(0, this)
					
					distancePassedAnimator.start()
					this
				}
				else {
					binding.tvDistancePassedValue.text = distancePassedValueDefault
					0
				}
			}
		})
	}
	
	private fun observeInputFuelStation() {
		mViewModel.fuelStationInputValue.observe(this, {
			if (it.isNullOrBlank()) {
				binding.layoutInputFuelStation.error = getString(R.string.fg_fuel_history_add_fuel_station_input_error)
				mViewModel.fuelStationReady = false
			}
			else {
				binding.layoutInputFuelStation.isErrorEnabled = false
				mViewModel.fuelStationReady = true
			}
		})
		
	}
	
	private fun observeInputPrice() {
		mViewModel.priceInputValue.observe(this, {
			if (it.isNullOrBlank()) {
				binding.layoutInputPrice.error = getString(R.string.fg_fuel_history_add_fuel_price_input_error)
				mViewModel.fuelPriceReady = false
			}
			else {
				binding.layoutInputPrice.isErrorEnabled = false
				mViewModel.fuelPriceReady = true
			}
		})
	}
	
	private fun observeFuelType() {
		mViewModel.selectedFuelType.observe(this, {
			mViewModel.fuelTypeReady = it != null
		})
	}
	
	private fun observeInputOdometer() {
		mViewModel.odometerReady.observe(this, {
			if (it != null && !it) binding.layoutInputOdometer.error = getString(R.string.odometer_input_error)
		})
	}
	
	
	
	
	private class FuelStationDropAdapter(
		context: Context,
		@LayoutRes private val layoutId: Int,
		data: List<FuelStation>
	): BaseDropAdapter<FuelStation>(context, layoutId, data) {
		
		private lateinit var childView: View
		private lateinit var fuelStation: FuelStation
		
		override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
			childView = convertView ?:
			            LayoutInflater.from(context).inflate(layoutId, parent, false)
			
			fuelStation = getItem(position)
			childView.findViewById<TextView>(R.id.tvFuelStationTitle).text = fuelStation.brandTitle
			childView.findViewById<ImageView>(R.id.ivDropFuelStationIcon).setImageResource(fuelStation.brandIcon())
			return childView
		}
		
	}
	
}