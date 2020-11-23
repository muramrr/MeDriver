/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 24.11.2020 00:44
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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