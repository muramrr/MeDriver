/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 18:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.subscription

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.ItemSubscriptionPlanBinding
import com.mmdev.me.driver.domain.user.SubscriptionType.*
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
					R.string.btm_sheet_subscription_feature1,
					R.drawable.ic_check_mark_filled_24
				),
				FeatureUi(
					R.string.btm_sheet_subscription_feature2,
					R.drawable.ic_cross_mark_filled_24
				),
				FeatureUi(
					R.string.btm_sheet_subscription_feature3,
					R.drawable.ic_cross_mark_filled_24
				),
				FeatureUi(
					R.string.btm_sheet_subscription_feature4,
					R.drawable.ic_cross_mark_filled_24
				),
				FeatureUi(
					R.string.btm_sheet_subscription_feature5,
					R.drawable.ic_cross_mark_filled_24
				)
			),
			if (MainActivity.currentUser != null)
				MainActivity.currentUser!!.subscription.type == FREE
			else false,
			"₴0",
			SubscriptionTypeUi(FREE, R.string.btm_sheet_subscription_plan_type_free)
		),
		PlanUi(
			R.color.subscription_premium,
			listOf(
				FeatureUi(
					R.string.btm_sheet_subscription_feature1,
					R.drawable.ic_check_mark_filled_24
				),
				FeatureUi(
					R.string.btm_sheet_subscription_feature2,
					R.drawable.ic_check_mark_filled_24
				),
				FeatureUi(
					R.string.btm_sheet_subscription_feature3,
					R.drawable.ic_check_mark_filled_24
				),
				FeatureUi(
					R.string.btm_sheet_subscription_feature4,
					R.drawable.ic_cross_mark_filled_24
				),
				FeatureUi(
					R.string.btm_sheet_subscription_feature5,
					R.drawable.ic_cross_mark_filled_24
				)
			),
			if (MainActivity.currentUser != null)
				MainActivity.currentUser!!.subscription.type == PREMIUM
			else false,
			"₴29.99",
			SubscriptionTypeUi(PREMIUM, R.string.btm_sheet_subscription_plan_type_premium)
		),
		PlanUi(
			R.color.subscription_pro,
			listOf(
				FeatureUi(
					R.string.btm_sheet_subscription_feature1,
					R.drawable.ic_check_mark_filled_24
				),
				FeatureUi(
					R.string.btm_sheet_subscription_feature2,
					R.drawable.ic_check_mark_filled_24
				),
				FeatureUi(
					R.string.btm_sheet_subscription_feature3,
					R.drawable.ic_check_mark_filled_24
				),
				FeatureUi(
					R.string.btm_sheet_subscription_feature4,
					R.drawable.ic_check_mark_filled_24
				),
				FeatureUi(
					R.string.btm_sheet_subscription_feature5,
					R.drawable.ic_check_mark_filled_24
				)
			),
			if (MainActivity.currentUser != null)
				MainActivity.currentUser!!.subscription.type == PRO
			else false,
			"₴49.99",
			SubscriptionTypeUi(PRO, R.string.btm_sheet_subscription_plan_type_pro)
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