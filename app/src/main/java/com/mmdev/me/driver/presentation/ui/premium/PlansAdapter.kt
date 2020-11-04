/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.11.2020 21:09
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.premium

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.ItemPremiumPlanBinding
import com.mmdev.me.driver.presentation.ui.common.BaseRecyclerAdapter

/**
 * Used to display premium plans available
 * @see PlanUi
 *
 * Expand each viewHolder = (half of screen height / 3) -
 */

class PlansAdapter(
	data: List<PlanUi> = listOf(
		PlanUi(
			R.string.btm_sheet_premium_plan_monthly_duration,
			"₴59.99",
			"₴59.99",
			"0%"
		),
		PlanUi(
			R.string.btm_sheet_premium_plan_semi_annual_duration,
			"₴259.99",
			"₴42.99",
			"20%"
		),
		PlanUi(
			R.string.btm_sheet_premium_plan_annual_duration,
			"₴334.99",
			"₴27.99",
			"50%"
		)
	)
): BaseRecyclerAdapter<PlanUi>(data, R.layout.item_premium_plan) {
	
	companion object {
		const val SPACE_PER_ITEM = 30
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<PlanUi> {
		val binding = ItemPremiumPlanBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		binding.root.post {
			val height = parent.height / 3 - SPACE_PER_ITEM
			binding.root.layoutParams.height = height
			binding.root.layoutParams.width = parent.width - 300
			binding.root.requestLayout()
		}
		return BaseViewHolder(binding)
		
	}
}