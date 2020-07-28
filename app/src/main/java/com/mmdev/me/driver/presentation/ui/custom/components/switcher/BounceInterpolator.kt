/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 28.07.20 17:09
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.custom.components.switcher

import android.view.animation.Interpolator
import kotlin.math.cos
import kotlin.math.pow

/**
 * Created by Evgenii Neumerzhitckii
 * (read more on https://evgenii.com/blog/spring-button-animation-on-android/)
 */

class BounceInterpolator(private val amplitude: Double, private val frequency: Double) : Interpolator {

	override fun getInterpolation(time: Float): Float =
		(-1 * Math.E.pow(-time / amplitude) * cos(frequency * time) + 1).toFloat()

}