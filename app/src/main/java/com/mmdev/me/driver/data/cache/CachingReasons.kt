/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.12.2020 18:40
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.cache

/**
 * Primary reasons why [CachedOperation] exists in table
 * Each reason directly depends on previous one
 * E.g.: If reason was [NO_SUBSCRIPTION] that's means that user was not null etc
 */

enum class CachingReasons {
	NO_USER,
	NO_SUBSCRIPTION,
	NO_INTERNET
}