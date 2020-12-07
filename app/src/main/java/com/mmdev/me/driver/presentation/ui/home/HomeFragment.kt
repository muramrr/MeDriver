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

package com.mmdev.me.driver.presentation.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.extensions.roundTo
import com.mmdev.me.driver.databinding.FragmentHomeBinding
import com.mmdev.me.driver.domain.vehicle.data.Expenses
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import com.mmdev.me.driver.presentation.utils.extensions.domain.dateMonthText
import com.mmdev.me.driver.presentation.utils.extensions.getColorValue
import com.mmdev.me.driver.presentation.utils.extensions.getTypeface
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
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
	
	private val checkedExpensesPositions = mutableListOf(0, 1, 2, 3, 4)
	
	private var priceFormatter = ""
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel.viewState.observe(this, { renderState(it) })
	}
	
	override fun renderState(state: ViewState) {
		when(state) {
			is HomeViewState.Error -> binding.root.rootView.showSnack(state.errorMessage ?: "Error")
			is HomeViewState.GeneratingStarted -> binding.viewLoading.visibility = View.VISIBLE
			is HomeViewState.GenerationCompleted -> {
				binding.viewLoading.visibility = View.INVISIBLE
				binding.root.rootView.showSnack("Generating completed")
			}
		}
	}
	
	override fun setupViews() {
		if (!MedriverApp.dataGenerated) {
			var count = 0
			binding.tvMyGarageHeader.setOnClickListener {
				if (count < 10) {
					count++
				}
				else {
					 mViewModel.generateRandomData(requireContext())
				}
				
			}
		}
		
		initStringRes()
		binding.rvMyGarage.apply {
			adapter = myGarageAdapter
			layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
			
			//adjust auto swipe to item center
			val snapHelper: SnapHelper = LinearSnapHelper()
			snapHelper.attachToRecyclerView(this)
			
			setHasFixedSize(true)
		}
		
		setupBarChartExpenses()
		
		mViewModel.vehicles.observe(this, { vehiclesWithExpenses ->
			myGarageAdapter.setNewData(vehiclesWithExpenses.map { it.first })
			// if vehicles < 5 -> leave only positions <= 5
			checkedExpensesPositions.removeAll { it >= vehiclesWithExpenses.size }
			setupPieChartExpenses(vehiclesWithExpenses)
		})
		
		mViewModel.expensesPerYear.observe(this, { expenses ->
			setupBarChartExpensesData(expenses)
		})
		
	}

	private fun initStringRes() {
		priceFormatter = getString(R.string.price_formatter_right)
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
			data = setupPieExpensesData(stats)
			animateY(1500, Easing.EaseInOutQuad)
			
			isRotationEnabled = false
			
			description.apply {
				text = getString(R.string.fg_vehicle_card_expenses_title)
				textColor = requireContext().getColorValue(R.color.colorOnBackground)
				textSize = 14f
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
	private fun setupPieExpensesData(input: List<Pair<Vehicle, Expenses>>): PieData {
		
		val entries = input.take(if (input.size > 5) 5 else input.size).map {
			PieEntry(it.second.getTotal().toFloat().roundTo(2), it.first.brand)
		}
		val dataSet = PieDataSet(entries, "")
		
		dataSet.apply {
			colors = colorPalette.map { requireContext().getColorValue(it) }.shuffled()
			valueFormatter = object : ValueFormatter() {
				override fun getFormattedValue(value: Float): String {
					return String.format(priceFormatter, value)
				}
			}
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
	private fun setNewPieChartExpensesData(positions: List<Int>) {
		mViewModel.vehicles.value?.let { vehiclesWithExpenses ->
			binding.pieChartExpenses.apply {
				data = setupPieExpensesData(positions.map { vehiclesWithExpenses[it] })
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
				setNewPieChartExpensesData(checkedExpensesPositions)
			}
			.setNegativeButton(R.string.dialog_btn_cancel, null)
			.show()
	}
	

	
	private fun setupBarChartExpenses() {
		
		binding.barChartExpenses.apply {
			setDrawBarShadow(false)
			setDrawValueAboveBar(true)
			
			description.isEnabled = false
			legend.isEnabled = false
			// if more than 12 entries are displayed in the chart, no values will be drawn
			setMaxVisibleValueCount(12)
			setPinchZoom(false) // scaling can now only be done on x- and y-axis separately
			
			animateY(1500)
			
			xAxis.apply {
				position = BOTTOM
				this.setDrawGridLines(false)
				granularity = 1f // only intervals of 1 day
				labelCount = 12
				
				textColor = requireContext().getColorValue(R.color.colorOnBackground)
				typeface = requireContext().getTypeface(R.font.m_plus_rounded1c_regular)
				
				valueFormatter = object : ValueFormatter() {
					override fun getAxisLabel(value: Float, axis: AxisBase?): String =
						value.toInt().dateMonthText().take(3)
				}
			}
			
			axisLeft.apply {
				axisMinimum = 0f // this replaces setStartAtZero(true)
				this.setDrawGridLines(false)
				setLabelCount(8, false)
				spaceTop = 15f
				
				textColor = requireContext().getColorValue(R.color.colorOnBackground)
				typeface = requireContext().getTypeface(R.font.m_plus_rounded1c_regular)
			}
			
			axisRight.apply {
				axisMinimum = 0f // this replaces setStartAtZero(true)
				setLabelCount(8, false)
				spaceTop = 15f
				
				typeface = requireContext().getTypeface(R.font.m_plus_rounded1c_regular)
				textColor = requireContext().getColorValue(R.color.colorOnBackground)
			}
		}
		
	}
	private fun setupBarChartExpensesData(input: List<Expenses>) {
		
		val entries = input.mapIndexed { index, expenses ->
			BarEntry(
				(index + 1).toFloat(),
				expenses.getTotal().roundTo(2).toFloat()
			)
		}
		
		if (binding.barChartExpenses.data != null && binding.barChartExpenses.data.dataSetCount > 0) {
			(binding.barChartExpenses.data.getDataSetByIndex(0) as BarDataSet).values = entries
			binding.barChartExpenses.data.notifyDataChanged()
			binding.barChartExpenses.notifyDataSetChanged()
		}
		else {
			val set1 = BarDataSet(entries, "2020").apply {
				colors = colorPalette.map { requireContext().getColorValue(it) }
			}
			
			val data = BarData(set1).apply {
				setValueTextColor(requireContext().getColorValue(R.color.colorOnBackground))
				setValueTypeface(requireContext().getTypeface(R.font.m_plus_rounded1c_regular))
			}
			
			binding.barChartExpenses.data = data
		}
	}
	
	
	
}