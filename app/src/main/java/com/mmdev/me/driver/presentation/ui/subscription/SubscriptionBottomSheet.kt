/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.12.2020 15:40
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.subscription

import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.BottomSheetSubscriptionBinding
import com.mmdev.me.driver.presentation.core.base.BaseBottomSheetFragment
import com.mmdev.me.driver.presentation.ui.MainActivity
import com.mmdev.me.driver.presentation.ui.common.custom.HorizontalCarouselLayoutManager
import com.mmdev.me.driver.presentation.ui.common.custom.decorators.CentralFirstLastItemDecoration

/**
 *
 */

class SubscriptionBottomSheet: BaseBottomSheetFragment<Nothing, BottomSheetSubscriptionBinding>(
	layoutId = R.layout.bottom_sheet_subscription
) {
	
	override val mViewModel: Nothing? = null
	
	//attach callback, force dismiss with animation, set state
	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		//dismissWithAnimation = arguments?.getBoolean(ARG_DISMISS_WITH_ANIMATION) ?: true
		(requireDialog() as BottomSheetDialog).apply {
			dismissWithAnimation = true
			behavior.state = BottomSheetBehavior.STATE_EXPANDED
			behavior.addBottomSheetCallback(bottomSheetCallback)
		}
	}
	
	//make bottomSheet fit all screen height
	override fun onStart() {
		super.onStart()
		val sheetContainer = requireView().parent as? ViewGroup ?: return
		sheetContainer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
	}
	
	
	override fun setupViews() {
		
		binding.rvSubscriptionPlans.apply {
			adapter = PlansAdapter().apply {
				setOnItemClickListener { view, position, item ->
					when (position) {
						1 -> (requireActivity() as MainActivity).launchPurchaseFlow("3_month_premium")
						2 -> (requireActivity() as MainActivity).launchPurchaseFlow("pro_3_months")
					}
					
				}
			}
			
			layoutManager = HorizontalCarouselLayoutManager(this.context,false)
			//adjust auto swipe to item center
			val snapHelper: SnapHelper = LinearSnapHelper()
			snapHelper.attachToRecyclerView(this)
			
			addItemDecoration(
				CentralFirstLastItemDecoration(PlansAdapter.CHILD_MARGIN)
			)
		}
	}
	
}