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

package com.mmdev.me.driver.presentation.ui.fuel.history

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.databinding.FragmentFuelHistoryBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFragment
import com.mmdev.me.driver.presentation.ui.MainActivity
import com.mmdev.me.driver.presentation.ui.fuel.history.add.FuelHistoryAddDialog
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
import org.koin.androidx.viewmodel.ext.android.viewModel


class FuelHistoryFragment: BaseFragment<FuelHistoryViewModel, FragmentFuelHistoryBinding>(
	R.layout.fragment_fuel_history
) {
	override val mViewModel: FuelHistoryViewModel by viewModel()
	
	private val mAdapter = FuelHistoryAdapter().apply {
		setToBottomScrollListener {
			mViewModel.loadNextHistory()
		}
		
		setToTopScrollListener {
			mViewModel.loadPreviousHistory()
		}
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel.viewState.observe(this, { renderState(it) })
		mViewModel.shouldBeUpdated.observe(this, { if (it) mViewModel.loadInitHistory() })
	}
	
	override fun setupViews() = binding.run {
		
		binding.fabAddHistoryEntry.isEnabled = MainActivity.currentVehicle != null
		
		binding.rvFuelHistory.run {
			setHasFixedSize(true)
			//register data observer to automatically scroll to top when new history record added
			adapter = mAdapter
			
			layoutManager = LinearLayoutManager(requireContext())
		}
		
		mAdapter.setOnDeleteClickListener { view, position, item ->
			mViewModel.delete(item, position)
		}
		
		
		binding.fabAddHistoryEntry.setDebounceOnClick {
			
			FuelHistoryAddDialog().show(childFragmentManager, FuelHistoryAddDialog::class.java.canonicalName)
		}
		
		binding.fabAddHistoryEntry.setOnLongClickListener {
			mViewModel.generateFuelHistoryData()
			true
		}
	}
	
	override fun renderState(state: ViewState) {
		
		when (state) {
			is FuelHistoryViewState.Init -> {
				logInfo(TAG, "init data size = ${state.data.size}")
				mAdapter.setInitData(state.data)
				
			}
			is FuelHistoryViewState.LoadPrevious -> {
				logInfo(TAG, "loaded previous = ${state.data.size}")
				mAdapter.insertPreviousData(state.data)
			}
			is FuelHistoryViewState.LoadNext -> {
				logInfo(TAG, "loaded next = ${state.data.size}")
				mAdapter.insertNextData(state.data)
			}
			is FuelHistoryViewState.Delete -> {
				logInfo(TAG, "deleted at position ${state.position}")
				mAdapter.delete(state.position)
			}
			is FuelHistoryViewState.Error -> {
				logError(TAG, "${state.errorMessage}")
			}
		}
	}
	
}