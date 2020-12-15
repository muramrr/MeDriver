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

package com.mmdev.me.driver.data.repository.auth.mappers

import com.android.billingclient.api.Purchase
import com.google.firebase.auth.FirebaseUser
import com.mmdev.me.driver.data.core.mappers.mapList
import com.mmdev.me.driver.data.datasource.billing.data.PurchaseDto
import com.mmdev.me.driver.data.datasource.billing.data.SkuDto
import com.mmdev.me.driver.data.datasource.user.remote.dto.FirestoreUserDto
import com.mmdev.me.driver.domain.billing.PeriodType
import com.mmdev.me.driver.domain.billing.SubscriptionType
import com.mmdev.me.driver.domain.user.UserDataInfo

/**
 * Mapping between user DTO/domain data classes
 */

class UserMappers {
	
	// domain
//	fun domainToDto(domain: UserDataInfo): FirestoreUserDto = FirestoreUserDto(
//		id = domain.id,
//		email = domain.email,
//		isEmailVerified = domain.isEmailVerified
//	)
	
	// dto
	fun dtoToDomain(
		dto: FirestoreUserDto,
		subscriptionType: SubscriptionType = SubscriptionType.FREE
	): UserDataInfo = UserDataInfo(
		id = dto.id,
		email = dto.email,
		isEmailVerified = dto.isEmailVerified,
		subscriptionType = subscriptionType
	)
	
	// framework based
	fun firebaseUserToDto(firebaseUser: FirebaseUser): FirestoreUserDto = FirestoreUserDto(
		id = firebaseUser.uid,
		email = firebaseUser.email!!,
		isEmailVerified = firebaseUser.isEmailVerified
	)
	
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
	
	fun toPurchaseDto(purchases: List<Purchase>) = mapList(purchases) {
		PurchaseDto(
			accountId = it.accountIdentifiers!!.obfuscatedAccountId!!,
			isAcknowledged = it.isAcknowledged,
			isAutoRenewing = it.isAutoRenewing,
			orderId = it.orderId,
			originalJson = it.originalJson,
			purchaseTime = it.purchaseTime,
			purchaseToken = it.purchaseToken,
			signature = it.signature,
			sku = toSkuDto(it.sku),
			skuOriginal = it.sku
		
		)
	}
}