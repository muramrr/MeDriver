/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.09.2020 04:36
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.repository.fuel.prices

import android.util.Log
import com.mmdev.me.driver.data.datasource.fuel.prices.remote.dto.NetworkFuelModelResponse
import com.mmdev.me.driver.data.repository.fuel.prices.mappers.FuelPriceMappersFacade
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.FuelType.*
import com.mmdev.me.driver.domain.fuel.prices.model.FuelPrice
import com.mmdev.me.driver.repository.fuel.FuelConstants.networkResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Tests primary for [FuelPriceMappersFacade]
 */

@RunWith(JUnit4::class)
class FuelPriceMapperTest {
	
	
	private val mappers = FuelPriceMappersFacade()
	
	private val listOfMappedFuelStationsAndPricesDb =
		mappers.mapFuelResponseToDb(networkResponse, "2020-12-12")
	
	private val listOfMappedFuelStationsDm = mappers.mapFuelResponseToDm(networkResponse)
	
	
	@Test
	fun testNetworkResultForA100FuelType() {
		val networkFuelModelResponse: NetworkFuelModelResponse = networkResponse.getValue(A100)
		assertEquals(networkFuelModelResponse.result.networkFuelStations[0].slug, "wog")
		assertEquals(networkFuelModelResponse.result.networkFuelStations[0].price, 30.49, 0.0)
		assertEquals(networkFuelModelResponse.result.networkFuelStations.size, 1)
		assertEquals(networkFuelModelResponse.result.networkFuelStations[0].brand, "WOG")
	}
	
	
	@Test
	fun testFuelResponseToDmMapper() {
		//check if list of fuelStations contains exactly 10/10 stations
		assertEquals(listOfMappedFuelStationsDm.size, 10)
		
		//check stations contains exactly 6/6 FuelPrices
		val socarStationDm = listOfMappedFuelStationsDm.find { it.fuelStation.slug == "socar" }!!
		val anpStationDm = listOfMappedFuelStationsDm.find { it.fuelStation.slug == "anp" }!!
		Log.wtf("mylogs", "${socarStationDm.prices.size}")
		assertTrue(socarStationDm.prices.find { it.type == A100 } == null)
		assertTrue(anpStationDm.prices.size != FuelType.values().size)
		
		//check prices socar
		assertTrue(socarStationDm.prices.find { it.type == A98 }!!.price == 30.48)
		assertTrue(socarStationDm.prices.find { it.type == A95PLUS }!!.price == 27.49)
		assertTrue(socarStationDm.prices.find { it.type == A95 }!!.price == 26.49)
		assertTrue(socarStationDm.prices.find { it.type == A92 }!!.price == 25.49)
		assertTrue(socarStationDm.prices.find { it.type == DT }!!.price == 25.49)
		assertTrue(socarStationDm.prices.find { it.type == GAS }!!.price == 12.48)
		//check prices anp
		assertTrue((anpStationDm.prices.find { it.type == A100 } ?: FuelPrice()).priceString() == "--.--")
		assertTrue(anpStationDm.prices.find { it.type == A95PLUS }!!.price == 21.45)
		assertTrue(anpStationDm.prices.find { it.type == A95 }!!.price == 20.95)
		assertTrue(anpStationDm.prices.find { it.type == A92 }!!.price == 19.95)
		assertTrue(anpStationDm.prices.find { it.type == DT }!!.price == (21.toDouble()))
		
	}
	
	
	@Test
	fun testFuelResponseToDbMapper() {
		//check if list of fuelStations contains exactly 10/10 stations
		assertEquals(listOfMappedFuelStationsAndPricesDb.size, 10)
		
		//check stations contains exactly 6/6 FuelPrices
		val socarStationDb = listOfMappedFuelStationsAndPricesDb.find {
			it.fuelStation.slug == "socar"
		}!!
		
		val anpStationDb = listOfMappedFuelStationsAndPricesDb.find {
			it.fuelStation.slug == "anp"
		}!!
		
		assertTrue(socarStationDb.prices.find { it.type == A100.code } == null)
		assertTrue(anpStationDb.prices.size != FuelType.values().size)
		
		//check prices socar
		assertTrue(socarStationDb.prices.find { it.type == A98.code }!!.price == 30.48)
		assertTrue(socarStationDb.prices.find { it.type == A95PLUS.code }!!.price == 27.49)
		assertTrue(socarStationDb.prices.find { it.type == A95.code }!!.price == 26.49)
		assertTrue(socarStationDb.prices.find { it.type == A92.code }!!.price == 25.49)
		assertTrue(socarStationDb.prices.find { it.type == DT.code }!!.price == 25.49)
		assertTrue(socarStationDb.prices.find { it.type == GAS.code }!!.price == 12.48)
		
		//check station Id
		assertTrue(socarStationDb.prices.find { it.type == A98.code }!!.fuelStationId == "socar")
		assertTrue(socarStationDb.prices.find { it.type == A95PLUS.code }!!.fuelStationId == "socar")
		assertTrue(socarStationDb.prices.find { it.type == A95.code }!!.fuelStationId == "socar")
		assertTrue(socarStationDb.prices.find { it.type == A92.code }!!.fuelStationId == "socar")
		assertTrue(socarStationDb.prices.find { it.type == DT.code }!!.fuelStationId == "socar")
		assertTrue(socarStationDb.prices.find { it.type == GAS.code }!!.fuelStationId == "socar")
		
		//check prices anp
		assertTrue(anpStationDb.prices.find { it.type == A95PLUS.code }!!.price == 21.45)
		assertTrue(anpStationDb.prices.find { it.type == A95.code }!!.price == 20.95)
		assertTrue(anpStationDb.prices.find { it.type == A92.code }!!.price == 19.95)
		assertTrue(anpStationDb.prices.find { it.type == DT.code }!!.price == 21.toDouble())
		
	}
	
	private fun FuelPrice.priceString(): String = if (price != 0.0) price.toString() else "--.--"
	
}