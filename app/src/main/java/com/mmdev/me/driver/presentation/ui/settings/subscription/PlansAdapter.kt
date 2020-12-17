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

package com.mmdev.me.driver.presentation.ui.settings.subscription

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mmdev.me.driver.R
import com.mmdev.me.driver.R.*
import com.mmdev.me.driver.databinding.ItemSubscriptionPlanBinding
import com.mmdev.me.driver.domain.billing.SubscriptionType.*
import com.mmdev.me.driver.presentation.ui.MainActivity
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
			android.R.color.transparent,
			listOf(
				FeatureUi(
					string.btm_sheet_subscription_feature1,
					drawable.ic_check_mark_filled_24
				),
				FeatureUi(
					string.btm_sheet_subscription_feature2,
					drawable.ic_cross_mark_filled_24
				),
				FeatureUi(
					string.btm_sheet_subscription_feature3,
					drawable.ic_cross_mark_filled_24
				),
				FeatureUi(
					string.btm_sheet_subscription_feature4,
					drawable.ic_cross_mark_filled_24
				),
				FeatureUi(
					string.btm_sheet_subscription_feature5,
					drawable.ic_cross_mark_filled_24
				)
			),
			!(MainActivity.currentUser?.isSubscribed() ?: false),
			"₴0",
			SubscriptionTypeUi(FREE, string.btm_sheet_subscription_plan_type_free)
		),
		PlanUi(
			color.subscription_premium,
			listOf(
				FeatureUi(
					string.btm_sheet_subscription_feature1,
					drawable.ic_check_mark_filled_24
				),
				FeatureUi(
					string.btm_sheet_subscription_feature2,
					drawable.ic_check_mark_filled_24
				),
				FeatureUi(
					string.btm_sheet_subscription_feature3,
					drawable.ic_check_mark_filled_24
				),
				FeatureUi(
					string.btm_sheet_subscription_feature4,
					drawable.ic_cross_mark_filled_24
				),
				FeatureUi(
					string.btm_sheet_subscription_feature5,
					drawable.ic_cross_mark_filled_24
				)
			),
			MainActivity.currentUser?.isPremium() ?: false,
			"₴29.99",
			SubscriptionTypeUi(PREMIUM, string.btm_sheet_subscription_plan_type_premium)
		),
		PlanUi(
			color.subscription_pro,
			listOf(
				FeatureUi(
					string.btm_sheet_subscription_feature1,
					drawable.ic_check_mark_filled_24
				),
				FeatureUi(
					string.btm_sheet_subscription_feature2,
					drawable.ic_check_mark_filled_24
				),
				FeatureUi(
					string.btm_sheet_subscription_feature3,
					drawable.ic_check_mark_filled_24
				),
				FeatureUi(
					string.btm_sheet_subscription_feature4,
					drawable.ic_check_mark_filled_24
				),
				FeatureUi(
					string.btm_sheet_subscription_feature5,
					drawable.ic_check_mark_filled_24
				)
			),
			MainActivity.currentUser?.isPro() ?: false,
			"₴49.99",
			SubscriptionTypeUi(PRO, string.btm_sheet_subscription_plan_type_pro)
		)
	)
): BaseRecyclerAdapter<PlanUi>(data, R.layout.item_subscription_plan) {
	
	companion object {
		const val CHILD_MARGIN = 300
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<PlanUi> {
		val binding = ItemSubscriptionPlanBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		binding.root.post {
			binding.root.layoutParams.width = parent.width - CHILD_MARGIN
			binding.root.requestLayout()
		}
		return BaseViewHolder(binding)
		
	}
}