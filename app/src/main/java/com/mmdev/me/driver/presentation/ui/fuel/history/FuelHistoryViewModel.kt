/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.09.2020 20:42
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.MetricSystem
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.roundTo
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.history.IFuelHistoryRepository
import com.mmdev.me.driver.domain.fuel.history.model.ConsumptionBound
import com.mmdev.me.driver.domain.fuel.history.model.DistanceBound
import com.mmdev.me.driver.domain.fuel.history.model.FuelHistoryRecord
import com.mmdev.me.driver.domain.fuel.prices.model.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStation
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStationWithPrices
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.utils.combineWith
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToInt

/**
 * ViewModel used to connect between [IFuelHistoryRepository] and UI
 */

class FuelHistoryViewModel (private val repository: IFuelHistoryRepository)
	: BaseViewModel() {
	
	val fuelHistoryState: MutableLiveData<FuelHistoryViewState> = MutableLiveData()
	val historyRecord: MutableLiveData<FuelHistoryRecord> = MutableLiveData(FuelHistoryRecord(0))
	//indicates is history empty or not
	val isHistoryEmpty: MutableLiveData<Boolean> = MutableLiveData()
	
	
	
	/**
	 * selected [FuelStationWithPrices] from list [mFuelStationWithPrices]
	 * passed in [FuelHistoryAddDialog] constructor @param
	 * selecting from [FuelStationDropAdapter]
	 */
	private var selectedFuelStationWithPrices: FuelStationWithPrices? = null
	private var selectedFuelStation: FuelStation? = null
	
	
	
	//mutable values bindable to UI input/label
	val fuelStationInputValue: MutableLiveData<String> = MutableLiveData()
	private var typedFuelStationTitle: String = ""
	private var typedFuelStationSlug: String = ""
	val priceInputValue: MutableLiveData<String> = MutableLiveData()
	private val selectedFuelType: MutableLiveData<FuelType> = MutableLiveData()
	val odometerInputValue: MutableLiveData<String> = MutableLiveData()
	val sliderLitersValue: MutableLiveData<Float> = MutableLiveData(1.0f)
	val commentValue: MutableLiveData<String> = MutableLiveData()
	
	
	//variables to check if fields requires an input
	val fuelStationRequires: MediatorLiveData<Boolean?> = MediatorLiveData()
	val fuelPriceRequires: MediatorLiveData<Boolean?> = MediatorLiveData()
	val fuelTypeRequires: MediatorLiveData<Boolean?> = MediatorLiveData()
	val allFieldsAreCorrect: MutableLiveData<Boolean> = MutableLiveData(false)
	
	init {
		fuelStationRequires.addSource(fuelStationInputValue) {
			fuelStationRequires.value = when {
				it == null -> { null }
				it.isBlank() -> { true }
				else -> {
					handleTypedFuelStation(it)
					false
				}
			}
			checkAllFieldsAreCorrect()
		}
		
		fuelPriceRequires.addSource(priceInputValue) {
			fuelPriceRequires.value = when {
				it == null -> { null }
				it.isBlank() -> { true }
				!it.isNullOrBlank() -> { false }
				else -> { null }
			}
			checkAllFieldsAreCorrect()
		}
		
		fuelTypeRequires.addSource(selectedFuelType) {
			if (it != null) fuelTypeRequires.value = false
			findAndSetPriceByType(selectedFuelStationWithPrices)
			checkAllFieldsAreCorrect()
		}
		
	}
	
	
	
	
	//immutable value computed from (odometerInput - previous odometer value)
	val distancePassed: LiveData<Int> =
		odometerInputValue.combineWith(historyRecord) { typedOdometer, lastHistoryRecord ->
			if (!typedOdometer.isNullOrBlank() && lastHistoryRecord != null) {
				//make sure that distance passed > 0
				with(typedOdometer.toInt() - lastHistoryRecord.odometerValue()) {
					//-1 because needed to check for correct input
					if (this < 0) -1 else this
				}
			}
			else 0
		}
	
	val odometerRequires: LiveData<Boolean?> =
		distancePassed.combineWith(odometerInputValue) { distance, input ->
			if (distance != null && input != null)
				when {
					distance > 0 && input.isNotBlank() -> { false }
					distance <= 0 || input.isBlank() -> true
					else -> null
				}.also { checkAllFieldsAreCorrect() }
			else null.also { checkAllFieldsAreCorrect() }
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
				
				if (calculatedConsumption > lastHistoryRecord.fuelConsumption())
					R.drawable.ic_arrow_up_24
				else R.drawable.ic_arrow_down_24
				
			}
			else 0
		}
	
	/** immutable value computed from ([priceInputValue] * [sliderLitersValue]) inputs */
	val totalCostValue: LiveData<Double> =
		priceInputValue.combineWith(sliderLitersValue) { price, liters ->
			if (!price.isNullOrBlank() && liters != null)
				(price.toDouble() * liters).roundTo(2)
			else 0.0
		}
	
	/** immutable value computed from ([sliderLitersValue] / [fuelConsumptionValue] * 100) inputs */
	val estimateDistance: LiveData<Int> =
		sliderLitersValue.combineWith(fuelConsumptionValue) { liters, consumption ->
			if (liters != null && consumption != null && consumption > 0)
				(liters / consumption * 100).roundToInt()
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
		
		odometerInputValue.postValue(null)
		
		sliderLitersValue.postValue(1.0f)
		
		commentValue.postValue(null)
		
		allFieldsAreCorrect.postValue(false)
		
		selectedFuelStation = null
		selectedFuelStationWithPrices = null
	}
	
	/**
	 * Utility fun to check if all fields are correct and we can proceed
	 */
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
	
	/**
	 * Used to combine [priceInputValue] and [selectedFuelType]
	 * @see buildFuelHistoryRecord
	 * @return [FuelPrice] data class
	 */
	private fun buildFuelPrice(): FuelPrice = FuelPrice(
		price = priceInputValue.value?.toDouble() ?: 0.0,
		type = selectedFuelType.value ?: FuelType.A95
	)
	
	/**
	 * Used to define [FuelStation] from [selectedFuelStation] if it was selected from drop list
	 * or combine [typedFuelStationTitle] and [typedFuelStationSlug]
	 * @see buildFuelHistoryRecord
	 * @return [FuelStation] data class
	 */
	private fun buildFuelStation(): FuelStation =
		selectedFuelStation ?: FuelStation(typedFuelStationTitle, typedFuelStationSlug).also {
			logDebug(message = "FuelStation built = $it")
		}
	
	
	/**
	 * Build [DistanceBound] data class for [distancePassed] value according to what metric system app
	 * is using. 
	 * Metric system could be changed at SettingsFragment
	 */
	private fun buildDistancePassedBound(): DistanceBound =
		when (MedriverApp.metricSystem) {
			MetricSystem.KILOMETERS -> DistanceBound(
				kilometers = distancePassed.value ?: 0, 
				miles = null
			)
			MetricSystem.MILES -> DistanceBound(
				kilometers = null,
				miles = distancePassed.value ?: 0
			)
		}
	
	/**
	 * Build [ConsumptionBound] data class for [fuelConsumptionValue] accordding to what metric
	 * system app is using.
	 * Metric system could be changed at SettingsFragment
	 */
	private fun buildFuelConsumptionBound(): ConsumptionBound =
		when (MedriverApp.metricSystem) {
			MetricSystem.KILOMETERS -> ConsumptionBound(
				consumptionKM = fuelConsumptionValue.value ?: 0.0,
				consumptionMI = null
			)
			MetricSystem.MILES -> ConsumptionBound(
				consumptionKM = null,
				consumptionMI = fuelConsumptionValue.value ?: 0.0
			)
		}
	
	/**
	 * Build [DistanceBound] data class for [odometerInputValue] according to what metric system app
	 * is using.
	 * Metric system could be changed at SettingsFragment
	 */
	private fun buildOdometerValueBound(): DistanceBound =
		when (MedriverApp.metricSystem) {
			MetricSystem.KILOMETERS -> DistanceBound(
				kilometers = odometerInputValue.value?.toInt() ?: 0,
				miles = null
			)
			MetricSystem.MILES -> DistanceBound(
				kilometers = null,
				miles = odometerInputValue.value?.toInt() ?: 0
			)
		}
	
	/**
	 * combines all calculated values into final [FuelHistoryRecord] data class
	 * used in [addHistoryRecord]
	 */
	private fun buildFuelHistoryRecord(): FuelHistoryRecord =
		FuelHistoryRecord(
			id = historyRecord.value!!.id + 1,
			commentary = commentValue.value ?: "",
			date = Calendar.getInstance().time,
			distancePassedBound = buildDistancePassedBound(),
			filledLiters = sliderLitersValue.value?.toDouble() ?: 0.0,
			fuelConsumptionBound = buildFuelConsumptionBound(),
			fuelPrice = buildFuelPrice(),
			fuelStation = buildFuelStation(),
			odometerValueBound = buildOdometerValueBound()
		)
	
	/**
	 * [IFuelHistoryRepository] call
	 * Can be invoked only when [checkAllFieldsAreCorrect] returns TRUE
	 * This should guarantee that all liveData contains value
	 */
	fun addHistoryRecord(builtHistoryRecord: FuelHistoryRecord = buildFuelHistoryRecord()) {
		viewModelScope.launch {
			with(builtHistoryRecord) {
				repository.addFuelHistoryRecord(this).fold(
					//update screen on success
					success = {
						historyRecord.postValue(this)
						fuelHistoryState.postValue(FuelHistoryViewState.InsertNewOne(listOf(this)))
						isHistoryEmpty.value = false
						clearInputFields()
					},
					//catch error
					failure = { }
				)
				
			}
		}
	}
	
	/**
	 * [repository] call
	 * @param size defines how many records should be loaded
	 * if [size] is not specified we are probably loading initial data
	 * if [size] is specified then we are probably paginating existing data
	 * If response contains empty list on initial data -> change [isHistoryEmpty] value
	 */
	fun getHistoryRecords(size: Int? = null) {
		viewModelScope.launch {
			
			fuelHistoryState.postValue(FuelHistoryViewState.Loading)
			
			repository.loadFuelHistory(size).fold(
				success = { data ->
					if(size == null) {
						fuelHistoryState.postValue(FuelHistoryViewState.Init(data = data))
						//handle empty state visibility
						if (data.isNotEmpty()) {
							//init the first one item
							historyRecord.postValue(data.first()).also {
								logDebug(message = "Last history record = ${data.first().fuelStation}")
							}
							isHistoryEmpty.value = false
						}
						else isHistoryEmpty.value = true
					}
					else fuelHistoryState.postValue(FuelHistoryViewState.Paginate(data = data))
					logDebug(TAG,"history empty? = ${isHistoryEmpty.value}")
				},
				failure = {
					fuelHistoryState.postValue(FuelHistoryViewState.Error(it.localizedMessage!!))
				}
			)
			
		}
	}
}