/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 13.11.2020 19:58
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.subscription

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Represents feature with mark (checked or crossed) that indicates availability
 */

data class FeatureUi(
	@StringRes val feature: Int,
	@DrawableRes val availability: Int
)