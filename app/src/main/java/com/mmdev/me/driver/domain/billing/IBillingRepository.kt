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

import android.app.Activity
import com.android.billingclient.api.Purchase
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository used to retrieve all billing info needed for app functionality
 */

interface IBillingRepository {
	
	/**
	 * Get list of [Purchase] as [StateFlow], that will be converted to LiveData in ViewModel
	 */
	fun getPurchasesFlow(): StateFlow<List<Purchase>>
	
	/**
	 * Launch purchasing 'flow' for given
	 * @param skuPos defines sku position from item list which user wants to order
	 * this is very relates on positioning inside repository implementation
	 * @param accountId indicates current user unique id
	 */
	fun launchPurchase(activity: Activity, skuPos: Int, accountId: String)
	
}