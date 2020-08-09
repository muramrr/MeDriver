/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.08.20 17:30
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.core.mappers

/**
 *
 */

internal class ListMapperImpl<I, O>(private val mapper: IMapper<I, O>) : IListMapper<I, O> {
	override fun map(input: List<I>): List<O> {
		return input.map { mapper.map(it) }
	}
}