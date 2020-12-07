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