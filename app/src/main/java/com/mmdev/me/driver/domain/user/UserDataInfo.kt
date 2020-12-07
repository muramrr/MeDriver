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

package com.mmdev.me.driver.domain.user

import com.mmdev.me.driver.domain.user.SubscriptionDuration.TRIAL
import com.mmdev.me.driver.domain.user.SubscriptionType.*

data class UserDataInfo(
	val id: String = "",
	val email: String = "",
	val isEmailVerified: Boolean = false,
	val subscription: SubscriptionData = SubscriptionData()
) {
	//todo: check subscription time actuality
	fun isSubscriptionValid(): Boolean =
		subscription.type != FREE && subscription.duration != TRIAL
	
	
	fun isPro(): Boolean = isSubscriptionValid() && subscription.type == PRO //todo
	fun isPremium(): Boolean = isSubscriptionValid() && subscription.type == PREMIUM //todo
}