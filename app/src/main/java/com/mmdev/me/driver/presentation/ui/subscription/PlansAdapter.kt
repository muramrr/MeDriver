/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 14.11.2020 15:57
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.subscription

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.databinding.ItemSubscriptionPlanBinding
import com.mmdev.me.driver.domain.user.SubscriptionType.FREE
import com.mmdev.me.driver.domain.user.SubscriptionType.PREMIUM
import com.mmdev.me.driver.domain.user.SubscriptionType.PRO
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
			R.color.colorOnSurface,
			listOf(
				FeatureUi(
					R.string.btm_sheet_subscription_feature1,
					R.drawable.ic_cross_mark_filled_24
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
			if (MedriverApp.currentUser != null)
				MedriverApp.currentUser!!.subscription.type == FREE
			else false,
			"₴0",
			SubscriptionTypeUi(FREE, R.string.btm_sheet_subscription_plan_type_free)
		),
		PlanUi(
			R.color.colorPrimary,
			R.color.colorOnPrimary,
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
			if (MedriverApp.currentUser != null)
				MedriverApp.currentUser!!.subscription.type == PREMIUM
			else false,
			"₴19.99",
			SubscriptionTypeUi(PREMIUM, R.string.btm_sheet_subscription_plan_type_premium)
		),
		PlanUi(
			R.color.colorSecondary,
			R.color.colorOnSecondary,
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
			if (MedriverApp.currentUser != null)
				MedriverApp.currentUser!!.subscription.type == PRO
			else false,
			"₴29.99",
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