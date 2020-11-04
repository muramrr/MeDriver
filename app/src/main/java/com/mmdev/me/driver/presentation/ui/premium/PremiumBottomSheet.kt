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

import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.BottomSheetPremiumBinding
import com.mmdev.me.driver.presentation.core.base.BaseBottomSheetFragment
import com.mmdev.me.driver.presentation.ui.common.custom.decorators.LinearItemDecoration
import com.mmdev.me.driver.presentation.utils.extensions.showToast

/**
 *
 */

class PremiumBottomSheet: BaseBottomSheetFragment<Nothing, BottomSheetPremiumBinding>(
	layoutId = R.layout.bottom_sheet_premium
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
	
		binding.rvPremiumFeatures.apply {
			adapter = FeaturesAdapter()
			layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
			
			addItemDecoration(
				LinearItemDecoration(
					RecyclerView.HORIZONTAL, FeaturesAdapter.SPACE_PER_ITEM * 3
				)
			)
		}
		
		binding.rvPremiumPlans.apply {
			adapter = PlansAdapter().apply {
				setOnItemClickListener { view, position, item ->
					showToast("Clicked = ${item.duration}")
				}
			}
			layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
			
			addItemDecoration(
				LinearItemDecoration(
					RecyclerView.VERTICAL,
					PlansAdapter.SPACE_PER_ITEM * 3,
					300
				)
			)
		}
	}
	
}