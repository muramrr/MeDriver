/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.11.2020 21:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.premium

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.ItemPremiumFeatureBinding
import com.mmdev.me.driver.presentation.ui.common.BaseRecyclerAdapter
import com.mmdev.me.driver.presentation.ui.premium.FeaturesAdapter.Companion.SPACE_PER_ITEM


/**
 * Used to display premium features
 * @see FeatureUi
 *
 * Expand each viewHolder = (1/3 screen width - [SPACE_PER_ITEM])
 */

class FeaturesAdapter(
	data: List<FeatureUi> = listOf(
		FeatureUi(
			R.string.btm_sheet_premium_feature_multiple_vehicles_title,
			R.string.btm_sheet_premium_feature_multiple_vehicles_subtitle,
			R.drawable.ic_premium_multiple_cars_24
		),
		FeatureUi(
			R.string.btm_sheet_premium_feature_sync_title,
			R.string.btm_sheet_premium_feature_sync_subtitle,
			R.drawable.ic_premium_sync_24
		),
		FeatureUi(
			R.string.btm_sheet_premium_feature_multiple_devices_title,
			R.string.btm_sheet_premium_feature_multiple_devices_subtitle,
			R.drawable.ic_premium_multiple_devices_24
		)
	)
): BaseRecyclerAdapter<FeatureUi>(data, R.layout.item_premium_feature) {
	
	companion object {
		const val SPACE_PER_ITEM = 30
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<FeatureUi> {
		val binding = ItemPremiumFeatureBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		binding.root.post {
			val width = parent.width / 3 - SPACE_PER_ITEM
			binding.root.layoutParams.width = width
			binding.root.requestLayout()
		}
		return BaseViewHolder(binding)
		
	}
}