/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. medriver
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.me.driver.domain.billing

import com.mmdev.me.driver.core.utils.helpers.DateHelper

/**
 *
 */

enum class PeriodType {
	UNKNOWN,
	DAY,
	WEEK,
	MONTH,
	YEAR;
	
	companion object {
		fun getDurationInMillisByType(type: PeriodType): Long = when(type) {
			UNKNOWN -> 0
			DAY -> DateHelper.DAY_DURATION
			WEEK -> DateHelper.WEEK_DURATION
			MONTH -> DateHelper.MONTH_DURATION
			YEAR -> DateHelper.YEAR_DURATION
		}
	}
	
}