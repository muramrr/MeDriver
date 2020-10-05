/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.10.2020 20:31
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.repository.fuel.prices

import com.mmdev.me.driver.data.datasource.fuel.prices.remote.dto.FuelPricesDtoResponse
import com.mmdev.me.driver.data.repository.fuel.prices.mappers.FuelPriceMappersFacade
import com.mmdev.me.driver.domain.fuel.FuelType
import com.mmdev.me.driver.domain.fuel.FuelType.*
import com.mmdev.me.driver.repository.fuel.FuelConstants.dtoResponse
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
	
	private val mappedPair =
		mappers.listApiDtosToDbEntities(dtoResponse, "2020-12-12")
	
	@Test
	fun testNetworkResultForA100FuelType() {
		val fuelPricesDtoResponse: FuelPricesDtoResponse = dtoResponse.getValue(A100)
		assertEquals(fuelPricesDtoResponse.result.fuelPriceAndStationDtos[0].slug, "wog")
		assertEquals(fuelPricesDtoResponse.result.fuelPriceAndStationDtos[0].price, 30.49, 0.0)
		assertEquals(fuelPricesDtoResponse.result.fuelPriceAndStationDtos.size, 1)
		assertEquals(fuelPricesDtoResponse.result.fuelPriceAndStationDtos[0].brand, "WOG")
	}
	
	
	@Test
	fun testFuelResponseToDbMapper() {
		//check if list of fuelStations contains exactly 10/10 stations
		assertEquals(mappedPair.first.size, 10)
		
		//check stations contains exactly 6/6 FuelPrices
		val socarStationPrices = mappedPair.second.filter {
			it.fuelStationId == "socar"
		}
		
		val anpStationPrices = mappedPair.second.filter {
			it.fuelStationId == "anp"
		}
		
		assertTrue(anpStationPrices.find { it.typeCode == A100.code } == null)
		assertTrue(anpStationPrices.size != FuelType.values().size)
		
		//check prices socar
		assertTrue(socarStationPrices.find { it.typeCode == A98.code }!!.price == 30.48)
		assertTrue(socarStationPrices.find { it.typeCode == A95PLUS.code }!!.price == 27.49)
		assertTrue(socarStationPrices.find { it.typeCode == A95.code }!!.price == 26.49)
		assertTrue(socarStationPrices.find { it.typeCode == A92.code }!!.price == 25.49)
		assertTrue(socarStationPrices.find { it.typeCode == DT.code }!!.price == 25.49)
		assertTrue(socarStationPrices.find { it.typeCode == GAS.code }!!.price == 12.48)
		
		//check prices anp
		assertTrue(anpStationPrices.find { it.typeCode == A95PLUS.code }!!.price == 21.45)
		assertTrue(anpStationPrices.find { it.typeCode == A95.code }!!.price == 20.95)
		assertTrue(anpStationPrices.find { it.typeCode == A92.code }!!.price == 19.95)
		assertTrue(anpStationPrices.find { it.typeCode == DT.code }!!.price == 21.toDouble())
		
	}
	
}