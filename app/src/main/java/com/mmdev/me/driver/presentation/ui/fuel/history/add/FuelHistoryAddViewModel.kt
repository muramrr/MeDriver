/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 18:47
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.history.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.MetricSystem
import com.mmdev.me.driver.core.utils.extensions.currentEpochTime
import com.mmdev.me.driver.core.utils.extensions.currentTimeAndDate
import com.mmdev.me.driver.core.utils.extensions.roundTo
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logWtf
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.history.IFuelHistoryRepository
import com.mmdev.me.driver.domain.fuel.history.data.ConsumptionBound
import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory
import com.mmdev.me.driver.domain.fuel.prices.data.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStation
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStationWithPrices
import com.mmdev.me.driver.domain.user.UserDataInfo
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.ui.MainActivity
import com.mmdev.me.driver.presentation.utils.extensions.combineWith
import com.mmdev.me.driver.presentation.utils.extensions.domain.buildDistanceBound
import com.mmdev.me.driver.presentation.utils.extensions.domain.getConsumptionValue
import com.mmdev.me.driver.presentation.utils.extensions.domain.getValue
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlin.math.roundToInt

/**
 * ViewModel for [FuelHistoryAddDialog]
 */

class FuelHistoryAddViewModel(private val repository: IFuelHistoryRepository): BaseViewModel() {
	
	val viewState: MutableLiveData<FuelHistoryAddViewState> = MutableLiveData()
	
	val lastAddedEntry: MutableLiveData<FuelHistory?> = MutableLiveData(null)
	
	init {
		viewModelScope.launch {
			repository.loadFirstFuelHistoryEntry(MedriverApp.currentVehicleVinCode).fold(
				success = { lastAddedEntry.value = it },
				failure = { logWtf(TAG, "$it") }
			)
		}
	}
	
	//mutable values bindable to UI input/label
	var pickedDate: LocalDateTime = currentTimeAndDate()
	val fuelStationInputValue: MutableLiveData<String?> = MutableLiveData()
	val priceInputValue: MutableLiveData<String?> = MutableLiveData()
	val selectedFuelType: MutableLiveData<FuelType?> = MutableLiveData()
	val odometerInputValue: MutableLiveData<String?> = MutableLiveData()
	val litersInputValue: MutableLiveData<String?> = MutableLiveData("1.0")
	val commentValue: MutableLiveData<String?> = MutableLiveData()
	
	
	/**
	 * selected [FuelStationWithPrices] from dropDownList in [FuelHistoryAddDialog]
	 * used primary to find prices associated with chosen [FuelStation]
	 */
	private var listFuelStationWithPrices: FuelStationWithPrices? = null
	private var fuelStationFromList: FuelStation? = null
	
	/** if [FuelStation] was not chosen from drop list but typed by user */
	private var typedFuelStationTitle: String = ""
	private var typedFuelStationSlug: String = ""
	
	
	//immutable value computed from (odometerInput - previous odometer value)
	val distancePassed: LiveData<Int> =
		odometerInputValue.combineWith(lastAddedEntry) { typedOdometer, lastAddedEntry ->
			if (!typedOdometer.isNullOrBlank()) {
				//if history is not empty
				if (lastAddedEntry != null) {
					//make sure that distance passed > 0
					with(typedOdometer.toInt() - lastAddedEntry.odometerValueBound.getValue()) {
						//-1 because needed to check for correct input
						if (this < 0) -1 else this
					}
				}
				else with(typedOdometer.toInt() - MainActivity.currentVehicle!!.odometerValueBound.getValue()) {
					//-1 because needed to check for correct input
					if (this < 0) -1 else this
				}
			}
			else 0
		}
	
	/**
	 * calculate fuelConsumption based on [distancePassed] and filled liters value from [history]
	 * also check if [distancePassed] is calculated properly because of divide by zero
	 * formula is pretty simple: (filledLiters (liters) / distancePassed (km) ) * 100 (km)
	 * (we want to show consumption per 100 km)
	 */
	val fuelConsumptionValue: LiveData<Double> =
		distancePassed.combineWith(lastAddedEntry) { distancePassed, lastAddedEntry ->
			if (distancePassed != null && distancePassed > 0.0 && lastAddedEntry != null)
				((lastAddedEntry.filledLiters / distancePassed) * 100).roundTo(1)
			else 0.0
		}
	
	/**
	 * compare calculated [fuelConsumptionValue] based on [distancePassed] value with previous
	 * fuelConsumption index, which stored in [history]
	 * check which is greater and decide which icon to display (increasing or decreasing)
	 */
	val fuelConsumptionDrawable: LiveData<Int> =
		fuelConsumptionValue.combineWith(lastAddedEntry) { calculatedConsumption,
		                                            lastAddedEntry ->
			if (calculatedConsumption != null
			    && calculatedConsumption > 0.0
			    && lastAddedEntry != null) {
				
				if (calculatedConsumption > lastAddedEntry.getConsumptionValue())
					R.drawable.ic_arrow_up_24
				else R.drawable.ic_arrow_down_24
				
			}
			else 0
		}
	
	/** immutable value computed from ([priceInputValue] * [litersInputValue]) inputs */
	val totalCostValue: LiveData<Double> =
		priceInputValue.combineWith(litersInputValue) { price, liters ->
			if (!price.isNullOrBlank() && liters != null)
				(price.toDouble() * liters.toDouble()).roundTo(2)
			else 0.0
		}
	
	/** immutable value computed from ([litersInputValue] / [fuelConsumptionValue] * 100) inputs */
	val estimateDistance: LiveData<Int> =
		litersInputValue.combineWith(fuelConsumptionValue) { liters, consumption ->
			if (liters != null && consumption != null && consumption > 0)
				(liters.toDouble() / consumption * 100).roundToInt()
			else 0
		}
	
	
	
	fun selectFuelType(fuelType: FuelType) {
		selectedFuelType.value = fuelType
		findAndSetPriceByType(listFuelStationWithPrices, fuelType)
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
	
	
	/**
	 * Called only while user click on item from drop down list
	 * Finds [FuelStationWithPrices]
	 * @param list contains [FuelStationWithPrices]
	 * @param identifier unique keyword to identify FuelStation
	 * @return Nullable [FuelStationWithPrices]
	 * if list contains value -> assign [typedFuelStationSlug], [typedFuelStationTitle],
	 * [fuelStationFromList]
	 *
	 * or assign empty [String] if such station does not exists in [list]
	 */
	fun findFuelStationBySlug(identifier: String, list: List<FuelStationWithPrices>) {
		list.find { it.fuelStation.slug == identifier }?.let {
			listFuelStationWithPrices = it
			//some title can contain whitespaces, so trim()
			typedFuelStationTitle = it.fuelStation.brandTitle.trim()
			typedFuelStationSlug = it.fuelStation.slug
			fuelStationFromList = it.fuelStation
			fuelStationInputValue.postValue(it.fuelStation.brandTitle)
			
			findAndSetPriceByType(it)
		}
	}
	
	
	
	fun handleTypedFuelStation(fuelStationInput: String) {
		if (typedFuelStationTitle != fuelStationInput.trim()) {
			typedFuelStationTitle = fuelStationInput.trim()
			typedFuelStationSlug = fuelStationInput.trim()
			fuelStationFromList = null
		}
	}
	
	
	
	/**
	 * [IFuelHistoryRepository] call
	 * This should guarantee that all liveData contains value
	 */
	fun addHistoryRecord(user: UserDataInfo?) {
		viewModelScope.launch {
			with(buildFuelHistoryRecord()) {
				repository.addFuelHistoryRecord(user, this).collect { result ->
					result.fold(
						//update screen on success
						success = {
							viewState.postValue(FuelHistoryAddViewState.Success(this))
						},
						//catch error
						failure = {
							viewState.postValue(FuelHistoryAddViewState.Error(it.localizedMessage))
						}
					)
				}
				
			}
		}
	}
	
	
	/**
	 * combines all calculated values into final [FuelHistory] data class
	 * used in [addHistoryRecord]
	 */
	private fun buildFuelHistoryRecord(): FuelHistory =
		FuelHistory(
			commentary = commentValue.value ?: "",
			date = pickedDate,
			dateAdded = currentEpochTime(),
			distancePassedBound = buildDistanceBound(distancePassed.value!!),
			filledLiters = litersInputValue.value!!.toDouble(),
			fuelConsumptionBound = buildFuelConsumptionBound(),
			fuelPrice = buildFuelPrice(),
			fuelStation = buildFuelStation(),
			odometerValueBound = buildDistanceBound(odometerInputValue.value!!.toInt()),
			vehicleVinCode = MainActivity.currentVehicle!!.vin
		)
	
	
	/**
	 * Used to combine [priceInputValue] and [selectedFuelType]
	 * @see buildFuelHistoryRecord
	 * @return [FuelPrice] data class
	 */
	private fun buildFuelPrice(): FuelPrice = FuelPrice(
		price = priceInputValue.value!!.toDouble(),
		type = selectedFuelType.value!!
	)
	
	/**
	 * Used to define [FuelStation] from [fuelStationFromList] if it was selected from drop list
	 * or combine [typedFuelStationTitle] and [typedFuelStationSlug]
	 * @see buildFuelHistoryRecord
	 * @return [FuelStation] data class
	 */
	private fun buildFuelStation(): FuelStation =
		fuelStationFromList ?: FuelStation(typedFuelStationTitle, typedFuelStationSlug).also {
			logDebug(TAG, "FuelStation built = $it")
		}
	
	
	/**
	 * Build [ConsumptionBound] data class for [fuelConsumptionValue] accordding to what metric
	 * system app is using.
	 * Metric system could be changed at SettingsFragment
	 */
	private fun buildFuelConsumptionBound(): ConsumptionBound =
		when (MedriverApp.metricSystem) {
			MetricSystem.KILOMETERS -> ConsumptionBound(
				consumptionKM = fuelConsumptionValue.value!!,
				consumptionMI = null
			)
			MetricSystem.MILES -> ConsumptionBound(
				consumptionKM = null,
				consumptionMI = fuelConsumptionValue.value!!
			)
		}
	
	
}