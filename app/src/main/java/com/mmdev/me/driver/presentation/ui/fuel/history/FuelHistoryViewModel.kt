/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 26.08.2020 02:45
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.utils.roundTo
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.history.IFuelHistoryRepository
import com.mmdev.me.driver.domain.fuel.history.model.FuelHistoryRecord
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStationWithPrices
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.utils.combineWith
import java.util.*

/**
 * ViewModel used to connect between [IFuelHistoryRepository] and UI
 */

class FuelHistoryViewModel (private val repository: IFuelHistoryRepository) : BaseViewModel() {
	
	
	val historyRecord: MutableLiveData<FuelHistoryRecord> = MutableLiveData(
		FuelHistoryRecord(
			odometerValue = 20003,
			filledLiters = 48.0,
			fuelConsumption = 12.6,
			date = Calendar.getInstance().time
		)
	)
	
	/**
	 * selected [FuelStationWithPrices] from list [mFuelStationWithPrices]
	 * passed in [DialogFragmentHistoryAdd] constructor @param
	 * selecting from [FuelStationDropAdapter]
	 */
	private var selectedFuelStationWithPrices: FuelStationWithPrices? = null
	
	
	//mutable values binded to editText input
	val fuelStationInputValue: MutableLiveData<String> = MutableLiveData()
	private var selectedFuelStationTitle: String = ""
	private var selectedFuelStationSlug: String = ""
	val priceInputValue: MutableLiveData<String> = MutableLiveData()
	val selectedFuelType: MutableLiveData<FuelType> = MutableLiveData()
	val odometerInputValue: MutableLiveData<String> = MutableLiveData()
	val sliderLitersValue: MutableLiveData<Float> = MutableLiveData(1.0f)
	
	//variables to check if fields requires an input
	val fuelStationRequires: MediatorLiveData<Boolean?> = MediatorLiveData()
	val fuelPriceRequires: MediatorLiveData<Boolean?> = MediatorLiveData()
	val fuelTypeRequires: MediatorLiveData<Boolean?> = MediatorLiveData()
	val odometerRequires: MediatorLiveData<Boolean?> = MediatorLiveData()
	val allFieldsAreCorrect: MutableLiveData<Boolean> = MutableLiveData(false)
	
	init {
		fuelStationRequires.addSource(fuelStationInputValue) {
			fuelStationRequires.value = when {
				it != null && it.isBlank() -> { true }
				it == null -> { null }
				else -> {
					handleTypedFuelStation(it)
					false
				}
			}
			checkAllFieldsAreCorrect()
		}
		
		fuelPriceRequires.addSource(priceInputValue) {
			fuelPriceRequires.value = when {
				it != null && it.isBlank() -> { true }
				it == null -> { null }
				else -> { false }
			}
			checkAllFieldsAreCorrect()
		}
		
		fuelTypeRequires.addSource(selectedFuelType) {
			if (it != null) fuelTypeRequires.value = false
			findAndSetPriceByType(selectedFuelStationWithPrices)
			checkAllFieldsAreCorrect()
		}
		
		odometerRequires.addSource(odometerInputValue) {
			odometerRequires.value = when {
				it != null && it.isBlank() -> { true }
				it == null -> { true }
				else -> { false }
			}
			checkAllFieldsAreCorrect()
		}
	}
	
	//immutable value computed from (price * liters) inputs
	val totalCostValue: LiveData<Double> =
		priceInputValue.combineWith(sliderLitersValue) { price, liters ->
			if (!price.isNullOrBlank() && liters != null)
				(price.toDouble() * liters).roundTo(2)
			else 0.0
		}
	
	//immutable value computed from (odometerInput - previous odometer value)
	val distancePassed: LiveData<Int> =
		odometerInputValue.combineWith(historyRecord) { typedOdometer, lastHistoryRecord ->
			if (!typedOdometer.isNullOrBlank()
			    && lastHistoryRecord != null
			    && typedOdometer.toInt() > lastHistoryRecord.odometerValue) {
				
				typedOdometer.toInt() - lastHistoryRecord.odometerValue
			}
			else 0
		}
	
	/**
	 * calculate fuelConsumption based on [distancePassed] and filled liters value from [historyRecord]
	 * also check if [distancePassed] is calculated properly because of divide by zero
	 * formula is pretty simple: (filledLiters (liters) / distancePassed (km) ) * 100 (km)
	 * (we want to show consumption per 100 km)
	 */
	val fuelConsumptionValue: LiveData<Double> =
		distancePassed.combineWith(historyRecord) { distancePassed, lastHistoryRecord ->
			if (distancePassed != null && distancePassed > 0.0 && lastHistoryRecord != null)
				((lastHistoryRecord.filledLiters / distancePassed) * 100).roundTo(1)
			else 0.0
		}
	
	/**
	 * compare calculated [fuelConsumptionValue] based on [distancePassed] value with previous
	 * fuelConsumption index stored in [historyRecord]
	 * check which is greater and decide which icon to display (increasing or decreasing)
	 */
	val fuelConsumptionDrawable: LiveData<Int> =
		fuelConsumptionValue.combineWith(historyRecord) { calculatedConsumption,
		                                                  lastHistoryRecord ->
			if (calculatedConsumption != null
			    && calculatedConsumption > 0.0
			    && lastHistoryRecord != null) {
				
				if (calculatedConsumption > lastHistoryRecord.fuelConsumption) R.drawable.ic_up_arrow_24
				else R.drawable.ic_down_arrow_24
				
			}
			else 0
		}
	
	/**
	 * Finds [FuelStationWithPrices]
	 * @param list contains [FuelStationWithPrices]
	 * @param slug unique keyword to identify FuelStation
	 * @return Nullable [FuelStationWithPrices]
	 * also assigns [selectedFuelStationSlug] and [selectedFuelStationTitle] if list contains
	 * and empty [String] if such station does not exists in [list]
	 */
	fun findFuelStationBySlug(slug: String, list: List<FuelStationWithPrices>) {
		selectedFuelStationWithPrices = list.find { it.fuelStation.slug == slug }.also {
			selectedFuelStationTitle = it?.fuelStation?.brandTitle ?: ""
			selectedFuelStationSlug = it?.fuelStation?.slug ?: ""
			findAndSetPriceByType(it)
		}
	}
	
	/**
	 * find price by [fuelType] and set price [String] to [priceInputValue]
	 */
	private fun findAndSetPriceByType(fuelStationWithPrices: FuelStationWithPrices?,
	                                  fuelType: FuelType? = selectedFuelType.value) {
		fuelStationWithPrices?.prices?.find { it.type == fuelType }?.let {
			priceInputValue.value = it.price.toString()
		}
	}
	
	private fun handleTypedFuelStation(fuelStationInput: String) {
		if (selectedFuelStationTitle != fuelStationInput) {
			selectedFuelStationTitle = fuelStationInput
			selectedFuelStationSlug = fuelStationInput
		}
	}
	
	//clear previously typed values stored in LiveData (mutableLiveData)
	//called when user press Done button on DialogHistoryAddFragment
	fun clearInputFields() {
		fuelStationInputValue.postValue(null).also {
			fuelStationRequires.postValue(null)
		}
		
		priceInputValue.postValue(null).also {
			fuelPriceRequires.postValue(null)
		}
		
		selectedFuelType.postValue(null).also {
			fuelTypeRequires.postValue(null)
		}
		
		odometerInputValue.postValue(null).also {
			odometerRequires.postValue(null)
		}
		
		sliderLitersValue.postValue(1.0f)
		
		allFieldsAreCorrect.postValue(false)
	}
	
	private fun checkAllFieldsAreCorrect() {
		allFieldsAreCorrect.postValue(
			if (fuelStationRequires.value != null
			    && fuelPriceRequires.value != null
			    && fuelTypeRequires.value != null
			    && odometerRequires.value != null) {
				
				!fuelStationRequires.value!!
				&& !fuelPriceRequires.value!!
				&& !fuelTypeRequires.value!!
				&& !odometerRequires.value!!
			}
			else false
		)
	}
}