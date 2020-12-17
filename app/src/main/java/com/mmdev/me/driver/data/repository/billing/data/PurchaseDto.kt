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

package com.mmdev.me.driver.data.repository.billing.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.mmdev.me.driver.domain.billing.PeriodType

/**
 * It will be stored in the server database and assigned to the user who made the purchase.
 */

data class PurchaseDto(
	@PropertyName("accountId") val accountId: String = "",
	@JvmField @PropertyName("isAcknowledged") val isAcknowledged: Boolean = false,
	@JvmField @PropertyName("isAutoRenewing") val isAutoRenewing: Boolean = false,
	@Exclude @PropertyName("orderId") val orderId: String = "",
	@Exclude @PropertyName("originalJson") val originalJson: String = "",
	@PropertyName("purchaseTime") val purchaseTime: Long = 0,
	@PropertyName("purchaseToken") val purchaseToken: String = "",
	@Exclude @PropertyName("signature") val signature: String = "",
	@PropertyName("sku") val sku: SkuDto = SkuDto(),
	@Exclude @PropertyName("skuOriginal") val skuOriginal: String = "",
	@PropertyName("nextPayment") val nextPayment: Long =
		purchaseTime + (PeriodType.getDurationInMillisByType(sku.periodType) * sku.periodDuration),
)
