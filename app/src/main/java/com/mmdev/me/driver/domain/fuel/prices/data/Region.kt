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

package com.mmdev.me.driver.domain.fuel.prices.data

/**
 * Used for retrieving fuel prices from api by regions (ids as a parameter in retrofit query)
 * Sorted by name in ukrainian language
 */

enum class Region(val id: Int) {
	VINNYTSKA(11),
	VOLYNSKA(12),
	SICHESLAVSKA(1),
	DONETSKA(2),
	ZHYTOMYRSKA(13),
	ZAKARPATSKA(14),
	ZAPORIZSKA(15),
	IVANO_FRANKSIVSKA(16),
	KYIV(3),
	KYIVSKA(17),
	KIROVOHRADSKA(4),
	LUHANSKA(18),
	LVIVSKA(5),
	MYKOLAIVSKA(19),
	ODESKA(6),
	POLTAVSKA(7),
	RIVNENSKA(29),
	SUMSKA(20),
	TERNOPILSKA(21),
	KHARKIVSKA(8),
	KHMELNITSKA(24),
	KHERSONSKA(23),
	CHERKASKA(25),
	CHERNIVETSKA(26),
	CHERNIHIVSKA(9),
}