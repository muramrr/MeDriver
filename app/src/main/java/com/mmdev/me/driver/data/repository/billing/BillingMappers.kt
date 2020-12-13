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

package com.mmdev.me.driver.data.repository.billing

import com.android.billingclient.api.Purchase
import com.mmdev.me.driver.data.repository.billing.data.PurchaseDto
import com.mmdev.me.driver.data.repository.billing.data.SkuDto
import com.mmdev.me.driver.domain.billing.PeriodType
import com.mmdev.me.driver.domain.user.SubscriptionType

/**
 *
 */

class BillingMappers {
	
	private fun toSkuDto(sku: String): SkuDto {
		val identifiers = sku.split("_")
		val type = when (identifiers.first()) {
			"premium" -> SubscriptionType.PREMIUM
			"pro" -> SubscriptionType.PRO
			else -> SubscriptionType.FREE
		}
		val periodDuration = identifiers[1].toInt()
		val periodType = when (identifiers.last()) {
			"day" -> PeriodType.DAY
			"week" -> PeriodType.WEEK
			"month" -> PeriodType.MONTH
			"months" -> PeriodType.MONTH
			"year" -> PeriodType.YEAR
			else -> PeriodType.UNKNOWN
		}
		
		return SkuDto(type, periodDuration, periodType)
	}
	
	fun toPurchaseDto(purchase: Purchase) = PurchaseDto(
		accountId = purchase.accountIdentifiers!!.obfuscatedAccountId!!,
		isAcknowledged = purchase.isAcknowledged,
		isAutoRenewing = purchase.isAutoRenewing,
		orderId = purchase.orderId,
		originalJson = purchase.originalJson,
		purchaseTime = purchase.purchaseTime,
		purchaseToken = purchase.purchaseToken,
		signature = purchase.signature,
		sku = toSkuDto(purchase.sku),
		skuOriginal = purchase.sku
		
	)
	
}