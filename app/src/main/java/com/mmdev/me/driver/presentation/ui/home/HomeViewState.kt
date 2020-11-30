/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.11.2020 20:56
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.home

import com.mmdev.me.driver.presentation.core.ViewState

/**
 *
 */

sealed class HomeViewState: ViewState {
	object GeneratingStarted: HomeViewState() //todo: delete
	object GenerationCompleted: HomeViewState() //todo: delete
	data class Error(val errorMessage: String?): HomeViewState()
	
}