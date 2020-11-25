/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 25.11.2020 23:11
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.home

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.FragmentHomeBinding
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import com.mmdev.me.driver.presentation.utils.extensions.showSnack
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *
 */

class HomeFragment : BaseFlowFragment<HomeViewModel, FragmentHomeBinding>(
	layoutId = R.layout.fragment_home
) {

	override val mViewModel: HomeViewModel by viewModel()
	
	private val myGarageAdapter = MyGarageAdapter(
		listOf(
			Vehicle(
				"Ford",
				"Focus",
				2003,
				"11111111111111111",
				DistanceBound(kilometers = 201321, null),
				1.8,
				"03.10.2020"
			),
			Vehicle(
				"Land Rover",
				"Range Rover Sport",
				2013,
				"22222222222222222",
				DistanceBound(kilometers = 120144, null),
				5.0,
				"25.11.2020"
			),
			Vehicle(
				"Hyundai",
				"Sonata",
				2018,
				"33333333333333333",
				DistanceBound(kilometers = 10024, null),
				1.6,
				"20.11.2020"
			),
		)
	)
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel.viewState.observe(this, { renderState(it) })
	}
	
	override fun setupViews() {
		binding.rvMyGarage.apply {
			adapter = myGarageAdapter
			layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
			
			//adjust auto swipe to item center
			val snapHelper: SnapHelper = LinearSnapHelper()
			snapHelper.attachToRecyclerView(this)
			
			setHasFixedSize(true)
		}
		
		mViewModel.vehicles.observe(this, {
			myGarageAdapter.setNewData(it)
		})
		
//		binding.btnCrash.setOnClickListener {
//			throw RuntimeException("Test Crash") // Force a crash
//		}
		//SubscriptionBottomSheet().show(childFragmentManager, SubscriptionBottomSheet::class.java.canonicalName)

	}
	
	

	override fun renderState(state: ViewState) {
		when(state) {
			is HomeViewState.Error -> binding.root.rootView.showSnack(state.errorMessage ?: "Error")
		}
	}


}