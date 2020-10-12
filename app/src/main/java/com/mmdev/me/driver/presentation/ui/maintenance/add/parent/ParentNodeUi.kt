/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 12.10.2020 18:44
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance.add.parent

import androidx.annotation.ArrayRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Data class that represents UI behavior of parent system node
 */

data class ParentNodeUi (
	@StringRes val title: Int,
	@StringRes val components: Int,
	@ArrayRes val children: Int,
	@DrawableRes val icon: Int
)