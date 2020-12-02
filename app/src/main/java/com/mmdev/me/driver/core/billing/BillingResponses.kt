/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.12.2020 20:48
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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