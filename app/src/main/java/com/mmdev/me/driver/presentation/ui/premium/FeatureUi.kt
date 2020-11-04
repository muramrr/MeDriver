/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.11.2020 19:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.premium

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Represents features visual appearance in [PremiumBottomSheet]
 */

data class FeatureUi(
	@StringRes val title: Int,
	@StringRes val subtitle: Int,
	@DrawableRes val icon: Int
)