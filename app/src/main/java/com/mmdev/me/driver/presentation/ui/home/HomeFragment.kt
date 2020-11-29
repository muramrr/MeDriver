/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 29.11.2020 19:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.home

import android.graphics.Color
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.utils.extensions.roundTo
import com.mmdev.me.driver.databinding.FragmentHomeBinding
import com.mmdev.me.driver.domain.vehicle.data.Expenses
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import com.mmdev.me.driver.presentation.utils.extensions.getColorValue
import com.mmdev.me.driver.presentation.utils.extensions.getTypeface
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
import com.mmdev.me.driver.presentation.utils.extensions.setSingleOnClick
import com.mmdev.me.driver.presentation.utils.extensions.showSnack
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 *
 */

class HomeFragment : BaseFlowFragment<HomeViewModel, FragmentHomeBinding>(
	layoutId = R.layout.fragment_home
) {

	override val mViewModel: HomeViewModel by viewModel()
	
	private val colorPalette: ArrayList<Int> = arrayListOf(
		R.color.data1,
		R.color.data2,
		R.color.data3,
		R.color.data4,
		R.color.data5,
		R.color.data6,
		R.color.data7,
		R.color.data8,
		R.color.data9,
		R.color.data10,
		R.color.data11,
		R.color.data12,
	)
	
	private val myGarageAdapter = MyGarageAdapter()
	
	private val checkedExpensesPositions = mutableListOf<Int>(0, 1, 2, 3, 4)
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel.viewState.observe(this, { renderState(it) })
	}
	
	override fun setupViews() {
		binding.tvMyGarageHeader.setSingleOnClick { mViewModel.generateRandomData(requireContext()) }
		
		binding.rvMyGarage.apply {
			adapter = myGarageAdapter
			layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
			
			//adjust auto swipe to item center
			val snapHelper: SnapHelper = LinearSnapHelper()
			snapHelper.attachToRecyclerView(this)
			
			setHasFixedSize(true)
		}
		
		mViewModel.vehicles.observe(this, { vehiclesWithExpenses ->
			myGarageAdapter.setNewData(vehiclesWithExpenses.map { it.first })
			checkedExpensesPositions.removeAll { it > vehiclesWithExpenses.size }
			setupPieChartExpenses(vehiclesWithExpenses)
		})
		
		
	}
	
	private fun setupPieDataExpenses(input: List<Pair<Vehicle, Expenses>>): PieData {
		
		val entries = input.take(if (input.size > 5) 5 else input.size).map {
			PieEntry(it.second.getTotal().toFloat().roundTo(2), it.first.brand)
		}
		val dataSet = PieDataSet(entries, "")
		
		dataSet.apply {
			colors = colorPalette.map { requireContext().getColorValue(it) }
			valueLinePart1OffsetPercentage = 80f
			valueLinePart1Length = 0.2f
			valueLinePart2Length = 0.8f
			valueLineColor = requireContext().getColorValue(R.color.colorOnBackground)
			valueTextColor = requireContext().getColorValue(R.color.colorOnBackground)
			valueTextSize = 12f
			valueTypeface = requireContext().getTypeface(R.font.m_plus_rounded1c_medium)
			yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
		}
		
		return PieData(dataSet)
	}


	private fun setupPieChartExpenses(stats: List<Pair<Vehicle, Expenses>>) {
		binding.btnPieChartExpensesSettings.setDebounceOnClick {
			showDialogPieChartExpensesSettings(
				stats.map {
					with(it.first) { "$brand $model ($year), $engineCapacity" }
				}.toTypedArray()
			)
		}
		
		binding.pieChartExpenses.apply {
			data = setupPieDataExpenses(stats)
			animateY(1400, Easing.EaseInOutQuad)
			
			isRotationEnabled = false
			
			description.apply {
				text = getString(R.string.fg_vehicle_card_expenses_title)
				textColor = requireContext().getColorValue(R.color.colorOnBackground)
				typeface = requireContext().getTypeface(R.font.m_plus_rounded1c_medium)
			}
			legend.apply {
				textColor = requireContext().getColorValue(R.color.colorOnBackground)
				typeface = requireContext().getTypeface(R.font.m_plus_rounded1c_regular)
			}
			
			transparentCircleRadius = 0f
			
			setHoleColor(Color.TRANSPARENT)
			holeRadius = 40f
			
			setEntryLabelColor(Color.BLACK)
			setEntryLabelTypeface(requireContext().getTypeface(R.font.m_plus_rounded1c_medium))
			highlightValues(null)
			invalidate()
		}
		
	}
	
	private fun setPieChartExpensesData(positions: List<Int>) {
		mViewModel.vehicles.value?.let { vehiclesWithExpenses ->
			binding.pieChartExpenses.apply {
				data = setupPieDataExpenses(positions.map { vehiclesWithExpenses[it] })
				animateY(1400, Easing.EaseOutCirc)
				invalidate()
			}
		}
		
	}
	
	private fun showDialogPieChartExpensesSettings(items: Array<String>) {
		val checkedItems = BooleanArray(items.size) {
			checkedExpensesPositions.contains(it)
		}
		
		MaterialAlertDialogBuilder(requireContext())
			.setTitle(R.string.fg_home_dialog_chart_expenses_settings_title)
			.setMultiChoiceItems(items, checkedItems) { dialog, which, isChecked ->
				if (isChecked) checkedExpensesPositions.add(which)
				else checkedExpensesPositions.remove(which)
			}
			.setPositiveButton(R.string.dialog_btn_apply) { dialog, which ->
				setPieChartExpensesData(checkedExpensesPositions)
			}
			.setNegativeButton(R.string.dialog_btn_cancel, null)
			.show()
	}

	override fun renderState(state: ViewState) {
		when(state) {
			is HomeViewState.Error -> binding.root.rootView.showSnack(state.errorMessage ?: "Error")
		}
	}


}