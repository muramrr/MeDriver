/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. medriver
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.me.driver.presentation.ui.fuel.history.add

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.widget.doOnTextChanged
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.MetricSystem
import com.mmdev.me.driver.core.utils.extensions.convertToLocalDateTime
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.databinding.DialogFuelHistoryAddBinding
import com.mmdev.me.driver.domain.fuel.FuelType.*
import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStation
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStationWithPrices
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseDialogFragment
import com.mmdev.me.driver.presentation.ui.MainActivity
import com.mmdev.me.driver.presentation.ui.SharedViewModel
import com.mmdev.me.driver.presentation.ui.common.BaseDropAdapter
import com.mmdev.me.driver.presentation.ui.fuel.FuelStationConstants
import com.mmdev.me.driver.presentation.ui.fuel.history.FuelHistoryViewModel
import com.mmdev.me.driver.presentation.ui.fuel.prices.FuelPricesViewModel
import com.mmdev.me.driver.presentation.utils.extensions.domain.brandIcon
import com.mmdev.me.driver.presentation.utils.extensions.domain.getValue
import com.mmdev.me.driver.presentation.utils.extensions.domain.humanDate
import com.mmdev.me.driver.presentation.utils.extensions.hideKeyboard
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
import com.mmdev.me.driver.presentation.utils.extensions.setupDatePicker
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.toInstant
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * Fullscreen dialog used to add records to Fuel History
 * Hosted by FuelFragmentHistory
 */

class FuelHistoryAddDialog: BaseDialogFragment<FuelHistoryAddViewModel, DialogFuelHistoryAddBinding>(
	layoutId = R.layout.dialog_fuel_history_add
) {
	override val mViewModel: FuelHistoryAddViewModel by viewModel()
	//get same scope as FuelFragmentHistory
	private val parentViewModel: FuelHistoryViewModel by lazy { requireParentFragment().getViewModel() }
	private val fuelPricesViewModel: FuelPricesViewModel by sharedViewModel()
	private val sharedViewModel: SharedViewModel by sharedViewModel()
	
	
	private var mFuelStationWithPrices: List<FuelStationWithPrices> = emptyList()
	
	private val distancePassedAnimator = ValueAnimator.ofInt().apply { duration = 1200 }
	
	private var distancePassedValueDefault = ""
	private var distancePassedSubtitleValueFormatter = ""
	
	
	private var inputFuelStationError = ""
	private var inputPriceError = ""
	private var inputOdometerError = ""
	
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(STYLE_NORMAL, R.style.My_Dialog_FullScreen)
		mFuelStationWithPrices =
			fuelPricesViewModel.fuelPrices.value ?:
			FuelStationConstants.fuelStationList.map { FuelStationWithPrices(it) }
		mViewModel.viewState.observe(this, { renderState(it) })
	}
	
	override fun onStart() {
		super.onStart()
		dialog!!.window!!.setWindowAnimations(R.style.AppTheme_AppearingFromFab)
	}
	
	override fun setupViews() {
		//setup views
		initStringRes()
		
		setupFuelButtons()
		setupInputStationDropList()
		setupInputs()
		
		//setup observers
		observeFuelHistory()
		observeDistancePassed()
		
		binding.apply {
			//hide keyboard + clear focus while tapping somewhere on root view
			root.setOnTouchListener { rootView, _ ->
				rootView.performClick()
				rootView.hideKeyboard(rootView)
			}
			
			btnCancel.setOnClickListener { dismiss() }
			
			btnDone.setDebounceOnClick {
				if (checkAreInputCorrect()) mViewModel.addHistoryRecord(MainActivity.currentUser)
			}
			
			tvDistancePassedSubtitle.text = distancePassedSubtitleValueFormatter.format(
				mViewModel.lastAddedEntry.value?.odometerValueBound?.getValue()
				?: MainActivity.currentVehicle!!.odometerValueBound.getValue()
			)
		
		}
	}
	
	override fun renderState(state: ViewState) {
		when (state) {
			is FuelHistoryAddViewState.Success -> {
				
				//update vehicle with new odometer value
				if (state.fuelHistory.odometerValueBound.getValue() > MainActivity.currentVehicle!!.odometerValueBound.getValue()) {
					sharedViewModel.updateVehicle(
						MainActivity.currentUser,
						MainActivity.currentVehicle!!.copy(
							odometerValueBound = state.fuelHistory.odometerValueBound,
							lastRefillDate = state.fuelHistory.date.date.humanDate(),
							lastUpdatedDate = state.fuelHistory.dateAdded
						)
					)
				}
				
				//trigger parent fragment to update data because new entry was added
				parentViewModel.shouldBeUpdated.postValue(true)
				
				//dismiss dialog
				dismiss()
			}
			is FuelHistoryAddViewState.Error -> {
				
				logError(TAG, "${state.errorMessage}")
				
				showInnerSnack(state.errorMessage ?: getString(R.string.fg_fuel_history_add_adding_error))
				parentViewModel.shouldBeUpdated.postValue(false)
			}
		}
	}
	
	private fun initStringRes() {
		//Show strings according to current metric system
		when (MedriverApp.metricSystem) {
			MetricSystem.KILOMETERS -> {
				
				//todo: strange crash, needs fix
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
				
				//todo: strange crash, needs fix
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
		
		inputFuelStationError = getString(R.string.fg_fuel_history_add_fuel_station_input_error)
		inputPriceError = getString(R.string.fg_fuel_history_add_fuel_price_input_error)
		inputOdometerError = getString(R.string.odometer_input_error)
	}
	
	private fun setupDatePicker(lastEntry: FuelHistory?) {
		val lastHistoryEntryDate =
			lastEntry?.date?.toInstant(currentSystemDefault())?.toEpochMilliseconds() ?: 0
		
		binding.btnDatePickerFuelHistory.btnDatePicker.setupDatePicker(
			minDate = lastHistoryEntryDate
		) {
			mViewModel.pickedDate = convertToLocalDateTime(this.timeInMillis)
		}
		
	}
	
	private fun setupFuelButtons() {
		binding.radioFuelTypes.addOnButtonCheckedListener { group, checkedId, isChecked ->
			when (checkedId) {
				binding.btnFuelTypeGas.id -> mViewModel.selectFuelType(GAS)
				binding.btnFuelTypeDT.id -> mViewModel.selectFuelType(DT)
				binding.btnFuelType92.id -> mViewModel.selectFuelType(A92)
				binding.btnFuelType95.id -> mViewModel.selectFuelType(A95)
				binding.btnFuelType95PLUS.id -> mViewModel.selectFuelType(A95PLUS)
				binding.btnFuelType98.id -> mViewModel.selectFuelType(A98)
				binding.btnFuelType100.id -> mViewModel.selectFuelType(A100)
			}
		}
	}
	
	private fun setupInputStationDropList() {
		val adapter = FuelStationDropAdapter(requireContext(), R.layout.item_drop_image_text,
		                                     mFuelStationWithPrices.map { it.fuelStation })
		// drop down fuel station chooser
		binding.etFuelStationDrop.apply {
			
			setAdapter(adapter)
			
			setOnItemClickListener { _, _, position, _ ->
				
				//cast to global variable current selected station
				mViewModel.findFuelStationBySlug(
					adapter.getItem(position).slug, mFuelStationWithPrices
				)
				hideKeyboard(this@apply)
			}
		}
	}
	
	private fun setupInputs() {
		binding.etFuelStationDrop.doOnTextChanged { text, start, before, count ->
			mViewModel.handleTypedFuelStation(text.toString())
			
			if (text.isNullOrBlank()) binding.layoutInputFuelStation.error = inputFuelStationError
			else binding.layoutInputFuelStation.isErrorEnabled = false
		}
		
		binding.etInputPrice.doOnTextChanged { text, start, before, count ->
			if (text.isNullOrBlank()) binding.layoutInputPrice.error = getString(
				R.string.input_empty_error
			)
			else binding.layoutInputPrice.isErrorEnabled = false
		}
	}
	
	/** return true if all required fields have been inputted correctly  */
	private fun checkAreInputCorrect(): Boolean {
		if (binding.etFuelStationDrop.text.isNullOrBlank())
			binding.layoutInputFuelStation.error = inputFuelStationError
		
		if (binding.etInputPrice.text.isNullOrBlank())
			binding.layoutInputPrice.error = inputPriceError
		
		if (mViewModel.distancePassed.value!! <= 0 || binding.etInputOdometer.text.isNullOrBlank())
			binding.layoutInputOdometer.error = inputOdometerError
		
		if (mViewModel.selectedFuelType.value == null) {
			binding.radioFuelTypes.clearAnimation()
			binding.radioFuelTypes.startAnimation(
				AnimationUtils.loadAnimation(requireContext(), R.anim.horizontal_shake)
			)
		}
		
		
		return !binding.etFuelStationDrop.text.isNullOrBlank() &&
		       !binding.etInputPrice.text.isNullOrBlank() &&
		       !binding.etInputOdometer.text.isNullOrBlank() &&
		       mViewModel.distancePassed.value!! > 0 &&
		       mViewModel.selectedFuelType.value != null
	}
	
	private fun observeFuelHistory() {
		mViewModel.lastAddedEntry.observe(this, { lastEntry ->
			setupDatePicker(lastEntry)
			
			if (lastEntry != null) {
				
				when (lastEntry.fuelPrice.type) {
					GAS -> binding.radioFuelTypes.check(binding.btnFuelTypeGas.id)
					DT -> binding.radioFuelTypes.check(binding.btnFuelTypeDT.id)
					A92 -> binding.radioFuelTypes.check(binding.btnFuelType92.id)
					A95 -> binding.radioFuelTypes.check(binding.btnFuelType95.id)
					A95PLUS -> binding.radioFuelTypes.check(binding.btnFuelType95PLUS.id)
					A98 -> binding.radioFuelTypes.check(binding.btnFuelType98.id)
					A100 -> binding.radioFuelTypes.check(binding.btnFuelType100.id)
				}
				
				mViewModel.findFuelStationBySlug(lastEntry.fuelStation.slug, mFuelStationWithPrices)
				
			}
			
		})
	}
	
	private fun observeDistancePassed() {
		var oldValue = 0
		
		mViewModel.distancePassed.observe(this, { distancePassed ->
			//cancel existing animation if such exists
			distancePassedAnimator.cancel()
			oldValue = if (distancePassed > 0) {
				//if distancePassed > 0 seems it is good input, no error
				binding.layoutInputOdometer.isErrorEnabled = false
				
				/**
				 * check if oldValue is same with given [distancePassed] if no ->
				 * set oldValue as start
				 */
				if (distancePassed != oldValue) distancePassedAnimator.setIntValues(
					oldValue, distancePassed
				)
				else distancePassedAnimator.setIntValues(0, distancePassed)
				
				distancePassedAnimator.start()
				distancePassed
			}
			else {
				//if distancePassed <= 0 seems it is bad input, show error
				if (distancePassed < 0) binding.layoutInputOdometer.error = inputOdometerError
				
				binding.tvDistancePassedValue.text = distancePassedValueDefault
				0
			}
			
		})
	}
	
	
	private class FuelStationDropAdapter(
		context: Context, @LayoutRes private val layoutId: Int, data: List<FuelStation>
	): BaseDropAdapter<FuelStation>(context, layoutId, data) {
		
		private lateinit var childView: View
		private lateinit var fuelStation: FuelStation
		
		override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
			childView = convertView ?:
			            LayoutInflater.from(context).inflate(layoutId, parent, false)
			
			fuelStation = getItem(position)
			childView.findViewById<TextView>(R.id.tvDropItemText).text = fuelStation.brandTitle
			childView.findViewById<ImageView>(R.id.ivDropItemIcon).setImageResource(
				fuelStation.brandIcon()
			)
			return childView
		}
		
	}
	
}