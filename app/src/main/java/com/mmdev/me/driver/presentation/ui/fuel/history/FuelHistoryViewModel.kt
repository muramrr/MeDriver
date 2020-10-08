/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.10.2020 19:24
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.MetricSystem
import com.mmdev.me.driver.core.utils.convertToLocalDateTime
import com.mmdev.me.driver.core.utils.currentTimeAndDate
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.roundTo
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.history.IFuelHistoryRepository
import com.mmdev.me.driver.domain.fuel.history.data.ConsumptionBound
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.fuel.history.data.FuelHistory
import com.mmdev.me.driver.domain.fuel.prices.data.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStation
import com.mmdev.me.driver.domain.fuel.prices.data.FuelStationWithPrices
import com.mmdev.me.driver.domain.user.UserData
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.ui.fuel.getValue
import com.mmdev.me.driver.presentation.utils.extensions.combineWith
import com.mmdev.me.driver.presentation.utils.extensions.domain.getValue
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlin.math.roundToInt

/**
 * ViewModel used to talk with [IFuelHistoryRepository], update Fuel_History_* fragments
 */

class FuelHistoryViewModel (private val repository: IFuelHistoryRepository)
	: BaseViewModel() {
	
	val fuelHistoryState: MutableLiveData<FuelHistoryViewState> = MutableLiveData()
	val history: MutableLiveData<FuelHistory> = MutableLiveData(
		FuelHistory(
			date = convertToLocalDateTime(0),
			odometerValueBound = MedriverApp.currentVehicle?.odometerValueBound ?: DistanceBound(),
			vehicleVinCode = MedriverApp.currentVehicleVinCode
		)
	)
	
	//indicates is history empty or not
	val isHistoryEmpty: MutableLiveData<Boolean> = MutableLiveData()
	
	private val loadedHistory = mutableListOf<FuelHistory>()
	
	/**
	 * selected [FuelStationWithPrices] from list [mFuelStationWithPrices]
	 * passed in [FuelHistoryAddDialog] constructor @param
	 * selecting from [FuelStationDropAdapter]
	 */
	private var selectedFuelStationWithPrices: FuelStationWithPrices? = null
	private var selectedFuelStation: FuelStation? = null
	
	
	
	//mutable values bindable to UI input/label
	var pickedDate: LocalDateTime = currentTimeAndDate()
	val fuelStationInputValue: MutableLiveData<String?> = MutableLiveData()
	private var typedFuelStationTitle: String = ""
	private var typedFuelStationSlug: String = ""
	val priceInputValue: MutableLiveData<String?> = MutableLiveData()
	val selectedFuelType: MutableLiveData<FuelType?> = MutableLiveData()
	val odometerInputValue: MutableLiveData<String?> = MutableLiveData()
	val litersInputValue: MutableLiveData<String?> = MutableLiveData("1.0")
	val commentValue: MutableLiveData<String?> = MutableLiveData()
	
	
	//variables to check if fields requires an input
	var fuelStationReady = false
		set(value) {
			field = value
			checkAllFieldsAreCorrect()
			if (value) handleTypedFuelStation(fuelStationInputValue.value!!)
		}
	var fuelPriceReady = false
		set(value) {
			field = value
			checkAllFieldsAreCorrect()
		}
	var fuelTypeReady = false
		set(value) {
			field = value
			findAndSetPriceByType(selectedFuelStationWithPrices)
			checkAllFieldsAreCorrect()
		}
	var allFieldsAreCorrect: MutableLiveData<Boolean> = MutableLiveData(false)

	
	
	//immutable value computed from (odometerInput - previous odometer value)
	val distancePassed: LiveData<Int> =
		odometerInputValue.combineWith(history) { typedOdometer, lastHistoryRecord ->
			if (!typedOdometer.isNullOrBlank() && lastHistoryRecord != null) {
				//make sure that distance passed > 0
				with(typedOdometer.toInt() - lastHistoryRecord.odometerValueBound.getValue()) {
					//-1 because needed to check for correct input
					if (this < 0) -1 else this
				}
			}
			else 0
		}
	
	val odometerReady: LiveData<Boolean?> =
		distancePassed.combineWith(odometerInputValue) { distance, odometer ->
			if (distance != null && odometer != null)
				when {
					distance > 0 && odometer.isNotBlank() -> { true }
					distance <= 0 || odometer.isBlank() -> false
					else -> null
				}.also { checkAllFieldsAreCorrect() }
			else null.also { checkAllFieldsAreCorrect() }
		}
	
	/**
	 * calculate fuelConsumption based on [distancePassed] and filled liters value from [history]
	 * also check if [distancePassed] is calculated properly because of divide by zero
	 * formula is pretty simple: (filledLiters (liters) / distancePassed (km) ) * 100 (km)
	 * (we want to show consumption per 100 km)
	 */
	val fuelConsumptionValue: LiveData<Double> =
		distancePassed.combineWith(history) { distancePassed,
		                                      lastHistoryRecord ->
			if (distancePassed != null && distancePassed > 0.0 && lastHistoryRecord != null)
				((lastHistoryRecord.filledLiters / distancePassed) * 100).roundTo(1)
			else 0.0
		}
	
	/**
	 * compare calculated [fuelConsumptionValue] based on [distancePassed] value with previous
	 * fuelConsumption index stored in [history]
	 * check which is greater and decide which icon to display (increasing or decreasing)
	 */
	val fuelConsumptionDrawable: LiveData<Int> =
		fuelConsumptionValue.combineWith(history) { calculatedConsumption,
		                                            lastHistoryRecord ->
			if (calculatedConsumption != null
			    && calculatedConsumption > 0.0
			    && lastHistoryRecord != null) {
				
				if (calculatedConsumption > lastHistoryRecord.fuelConsumptionBound.getValue())
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
	
	fun selectFuelType(fuelType: FuelType) = selectedFuelType.postValue(fuelType)
	
	/**
	 * Called only while user click on item from drop down list
	 * Finds [FuelStationWithPrices]
	 * @param list contains [FuelStationWithPrices]
	 * @param slug unique keyword to identify FuelStation
	 * @return Nullable [FuelStationWithPrices]
	 * if list contains value -> assign [typedFuelStationSlug], [typedFuelStationTitle],
	 * [selectedFuelStation]
	 *
	 * or assign empty [String] if such station does not exists in [list]
	 */
	fun findFuelStationBySlug(slug: String, list: List<FuelStationWithPrices>) {
		list.find { it.fuelStation.slug == slug }?.let {
			selectedFuelStationWithPrices = it
					//some title can contain whitespaces, so trim()
			typedFuelStationTitle = it.fuelStation.brandTitle.trim()
			typedFuelStationSlug = it.fuelStation.slug
			selectedFuelStation = it.fuelStation
			fuelStationInputValue.postValue(it.fuelStation.brandTitle)
			
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
		if (typedFuelStationTitle != fuelStationInput.trim()) {
			typedFuelStationTitle = fuelStationInput
			typedFuelStationSlug = fuelStationInput
			selectedFuelStation = null
		}
	}
	
	//clear previously typed values stored in LiveData (mutableLiveData)
	//called when user presses Done button at DialogHistoryAddFragment
	private fun clearInputFields() {
		fuelStationInputValue.postValue(null)
		
		priceInputValue.postValue(null)
		
		selectedFuelType.postValue(null)
		
		odometerInputValue.postValue(null)
		
		litersInputValue.postValue("1.0")
		
		commentValue.postValue(null)
		
		allFieldsAreCorrect.postValue(false)
		
		selectedFuelStation = null
		selectedFuelStationWithPrices = null
	}
	
	/**
	 * Utility fun to check if all fields are correct and we can proceed
	 */
	private fun checkAllFieldsAreCorrect() {
		if (fuelStationReady && fuelPriceReady && fuelTypeReady && (odometerReady.value == true)) {
			allFieldsAreCorrect.postValue(true)
		}
		else allFieldsAreCorrect.postValue(false)
	}
	
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
	 * Used to define [FuelStation] from [selectedFuelStation] if it was selected from drop list
	 * or combine [typedFuelStationTitle] and [typedFuelStationSlug]
	 * @see buildFuelHistoryRecord
	 * @return [FuelStation] data class
	 */
	private fun buildFuelStation(): FuelStation =
		selectedFuelStation ?: FuelStation(typedFuelStationTitle, typedFuelStationSlug).also {
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
	
	/**
	 * Build [DistanceBound] data class for specified [value] according to what metric system app
	 * is using.
	 * Metric system can be changed at SettingsFragment
	 */
	private fun buildDistanceBound(value: Int): DistanceBound =
		when (MedriverApp.metricSystem) {
			MetricSystem.KILOMETERS -> DistanceBound(
				kilometers = value,
				miles = null
			)
			MetricSystem.MILES -> DistanceBound(
				kilometers = null,
				miles = value
			)
		}
	
	/**
	 * combines all calculated values into final [FuelHistory] data class
	 * used in [addHistoryRecord]
	 */
	private fun buildFuelHistoryRecord(): FuelHistory =
		FuelHistory(
			commentary = commentValue.value ?: "",
			date = pickedDate,
			distancePassedBound = buildDistanceBound(distancePassed.value!!),
			filledLiters = litersInputValue.value!!.toDouble(),
			fuelConsumptionBound = buildFuelConsumptionBound(),
			fuelPrice = buildFuelPrice(),
			fuelStation = buildFuelStation(),
			odometerValueBound = buildDistanceBound(odometerInputValue.value!!.toInt()),
			vehicleVinCode = MedriverApp.currentVehicle!!.vin
		)
	
	/**
	 * [IFuelHistoryRepository] call
	 * Can be invoked only when [checkAllFieldsAreCorrect] returns TRUE
	 * This should guarantee that all liveData contains value
	 */
	fun addHistoryRecord(user: UserData?) {
		viewModelScope.launch {
			with(buildFuelHistoryRecord()) {
				repository.addFuelHistoryRecord(user, this).collect { result ->
					result.fold(
						//update screen on success
						success = {
							fuelHistoryState.postValue(FuelHistoryViewState.InsertNewOne(this))
							history.value = this
							isHistoryEmpty.value = false
							clearInputFields()
						},
						//catch error
						failure = {
							fuelHistoryState.postValue(FuelHistoryViewState.Error(it.message!!))
						}
					)
				}
				
			}
		}
	}
	
	/**
	 * [repository] call
	 * @param size defines how many records should be loaded
	 * if [size] is not specified we are probably loading initial data
	 * if [size] is specified then we are probably paginating existing data
	 * If response contains empty list on initial data -> change [isHistoryEmpty] value
	 *
	 * todo: make pagination in both sides. loadNext, loadPrev (this one works only loadNext)
	 */
	fun getHistoryRecords(size: Int? = null) {
		viewModelScope.launch {
			
			fuelHistoryState.postValue(FuelHistoryViewState.Loading)
			
			repository.loadFuelHistory(MedriverApp.currentVehicleVinCode, size).fold(
				success = { data ->
					if (size == null) {
						//in case below no matter if list is empty or not
						fuelHistoryState.postValue(FuelHistoryViewState.Init(data = data))
						
						//handle empty state visibility
						if (data.isNotEmpty()) {
							//init the first one item
							history.postValue(data.first()).also {
								logDebug(TAG,
								         "Last history record = ${data.first().date.date}, " +
								              "${data.first().odometerValueBound.kilometers}")
							}
							isHistoryEmpty.value = false
						}
						else isHistoryEmpty.value = true
					} // if size was specified
					else fuelHistoryState.postValue(FuelHistoryViewState.Paginate(data = data))
					logDebug(TAG,"history empty? = ${isHistoryEmpty.value}")
					loadedHistory.addAll(data)
				},
				failure = {
					if (loadedHistory.isEmpty()) isHistoryEmpty.value = true
					fuelHistoryState.postValue(FuelHistoryViewState.Error(it.localizedMessage!!))
				}
			)
			
		}
	}
}