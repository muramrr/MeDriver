/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.10.2020 16:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.prices

import com.mmdev.me.driver.domain.fuel.prices.data.FuelStationWithPrices
import com.mmdev.me.driver.presentation.core.ViewState


sealed class FuelPricesViewState: ViewState {
	object Loading : FuelPricesViewState()
	data class Success(val data: List<FuelStationWithPrices>) : FuelPricesViewState()
	data class Error(val errorMessage: String) : FuelPricesViewState()
}