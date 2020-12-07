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

package com.mmdev.me.driver.core.billing

import com.android.billingclient.api.BillingClient.BillingResponseCode

/**
 * Human representation of [BillingResponseCode] codes
 */

enum class BillingResponses (val code: Int) {
	SERVICE_TIMEOUT(-3),
	FEATURE_NOT_SUPPORTED(-2),
	SERVICE_DISCONNECTED(-1),
	OK(0),
	USER_CANCELED(1),
	SERVICE_UNAVAILABLE(2),
	BILLING_UNAVAILABLE(3),
	ITEM_UNAVAILABLE(4),
	DEVELOPER_ERROR(5),
	ERROR(6),
	ITEM_ALREADY_OWNED(7),
	ITEM_NOT_OWNED(8);
	
	companion object {
		fun getResponseType(code: Int) = when (code) {
			-3 -> SERVICE_TIMEOUT
			-2 -> FEATURE_NOT_SUPPORTED
			-1 -> SERVICE_DISCONNECTED
			0 -> OK
			1 -> USER_CANCELED
			2 -> SERVICE_UNAVAILABLE
			3 -> BILLING_UNAVAILABLE
			4 -> ITEM_UNAVAILABLE
			5 -> DEVELOPER_ERROR
			6 -> ERROR
			7 -> ITEM_ALREADY_OWNED
			8 -> ITEM_NOT_OWNED
			else -> throw NoSuchElementException("Such response code doesn't exists")
		}
	}
}