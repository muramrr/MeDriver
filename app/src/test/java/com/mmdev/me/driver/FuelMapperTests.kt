/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 13.08.20 18:19
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver

import com.google.gson.Gson
import com.mmdev.me.driver.data.datasource.remote.fuel.model.NetworkFuelModelResponse
import com.mmdev.me.driver.data.repository.fuel.mappers.FuelDataMappersFacade
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.FuelType.*
import com.mmdev.me.driver.domain.fuel.model.FuelStation.FuelPrice
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 *
 */

@RunWith(JUnit4::class)
class FuelMapperTests {
	
	private val responseList = listOf(
		readJson("A100"),
		readJson("A95PLUS"),
		readJson("A95"),
		readJson("A92"),
		readJson("DT"),
		readJson("GAS")
	)
	
	private val responseMap = mutableMapOf<FuelType, NetworkFuelModelResponse>()
	
	private val gson: Gson = Gson()
	private val mappers = FuelDataMappersFacade()
	
	@Before
	fun setup() {
		responseMap.putAll(
			values().zip(
				responseList.map { gson.fromJson(it, NetworkFuelModelResponse::class.java) }
			).toMap())
	}
	
	@Test
	fun testNetworkResult() {
		val networkFuelModelResponse = responseMap[A100]!!
		assertEquals(networkFuelModelResponse.result.networkFuelStations[0].slug, "socar")
		assertEquals(networkFuelModelResponse.result.networkFuelStations[0].price, 29.99, 0.0)
		assertEquals(networkFuelModelResponse.result.networkFuelStations.size, 2)
		assertEquals(networkFuelModelResponse.result.networkFuelStations[1].brand, "WOG")
	}
	
	
	@Test
	fun testFuelResponseToDmMapper() {
		//check if list of fuelStations contains exactly 10/10 stations
		val listOfMappedFuelStationsDm = mappers.mapFuelResponseToDm(responseMap)
		assertEquals(listOfMappedFuelStationsDm.size, 10)
		
		//check stations contains exactly 6/6 FuelPrices
		val socarStationDm = listOfMappedFuelStationsDm.find { it.slug == "socar" }!!
		val anpStationDm = listOfMappedFuelStationsDm.find { it.slug == "anp" }!!
		assertTrue(socarStationDm.prices.size == 6)
		assertFalse(anpStationDm.prices.size == 6)
		
		//check prices socar
		assertTrue(socarStationDm.prices.find { it.type == A100 }!!.price == 29.99.toString())
		assertTrue(socarStationDm.prices.find { it.type == A95PLUS }!!.price == 27.49.toString())
		assertTrue(socarStationDm.prices.find { it.type == A95 }!!.price == 26.49.toString())
		assertTrue(socarStationDm.prices.find { it.type == A92 }!!.price == 25.49.toString())
		assertTrue(socarStationDm.prices.find { it.type == DT }!!.price == 25.49.toString())
		assertTrue(socarStationDm.prices.find { it.type == GAS }!!.price == 12.48.toString())
		//check prices anp
		assertTrue((anpStationDm.prices.find { it.type == A100 } ?: FuelPrice(A100)).price == "--.--")
		assertTrue(anpStationDm.prices.find { it.type == A95PLUS }!!.price == 21.45.toString())
		assertTrue(anpStationDm.prices.find { it.type == A95 }!!.price == 20.95.toString())
		assertTrue(anpStationDm.prices.find { it.type == A92 }!!.price == 19.95.toString())
		assertTrue(anpStationDm.prices.find { it.type == DT }!!.price == (21.toDouble()).toString())
		
	}
	
	@Test
	fun testFuelResponseToDbMapper() {
		//check if list of fuelStations contains exactly 10/10 stations
		val listOfMappedFuelStationsAndPricesDb = mappers.mapFuelResponseToDb(responseMap)
		assertEquals(listOfMappedFuelStationsAndPricesDb.size, 10)
		
		//check stations contains exactly 6/6 FuelPrices
		val socarStationDb = listOfMappedFuelStationsAndPricesDb.find {
			it.fuelStation.slug == "socar"
		}!!
		
		val anpStationDb = listOfMappedFuelStationsAndPricesDb.find {
			it.fuelStation.slug == "anp"
		}!!
		
		assertTrue(socarStationDb.prices.size == 6)
		assertFalse(anpStationDb.prices.size == 6)
		
		//check prices socar
		assertTrue(socarStationDb.prices.find { it.type == A100.code }!!.price == 29.99)
		assertTrue(socarStationDb.prices.find { it.type == A95PLUS.code }!!.price == 27.49)
		assertTrue(socarStationDb.prices.find { it.type == A95.code }!!.price == 26.49)
		assertTrue(socarStationDb.prices.find { it.type == A92.code }!!.price == 25.49)
		assertTrue(socarStationDb.prices.find { it.type == DT.code }!!.price == 25.49)
		assertTrue(socarStationDb.prices.find { it.type == GAS.code }!!.price == 12.48)
		//check station Id
		assertTrue(socarStationDb.prices.find { it.type == A100.code }!!.fuelStationId == "socar")
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
	
}