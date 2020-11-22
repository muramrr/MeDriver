/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 01:21
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.core.base

/**
 * Base class for all repositories classes
 * Idk why I need it, but just in case
 */

abstract class BaseRepository {
	
	protected val TAG = "mylogs_${javaClass.simpleName}"
	
}