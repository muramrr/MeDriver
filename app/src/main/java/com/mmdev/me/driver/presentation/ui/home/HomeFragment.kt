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
import com.github.mikephil.charting.components.Legend.LegendForm.CIRCLE
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
import com.mmdev.me.driver.core.utils.extensions.currentLocalDateTime
import com.mmdev.me.driver.core.utils.extensions.roundTo
import com.mmdev.me.driver.databinding.FragmentHomeBinding
import com.mmdev.me.driver.domain.vehicle.data.Expenses
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import com.mmdev.me.driver.presentation.ui.common.ChartsConstants
import com.mmdev.me.driver.presentation.utils.extensions.domain.dateMonthText
import com.mmdev.me.driver.presentation.utils.extensions.getColorValue
import com.mmdev.me.driver.presentation.utils.extensions.getTypeface
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 *
 */

class HomeFragment : BaseFlowFragment<HomeViewModel, FragmentHomeBinding>(
	layoutId = R.layout.fragment_home
) {

	override val mViewModel: HomeViewModel by viewModel()
	
	private val myGarageAdapter = MyGarageAdapter()
	
	private val checkedExpensesPositions = mutableListOf(0, 1, 2, 3, 4)
	
	private var priceFormatter = ""
	private var hryvnia = ""
	
	private var colorPaletteValues = listOf<Int>()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel.viewState.observe(this, { renderState(it) })
	}
	
	override fun renderState(state: ViewState) {
		when(state) {
			is HomeViewState.Error -> showActivitySnack(state.errorMessage ?: "Error")
			is HomeViewState.GeneratingStarted -> binding.viewLoading.visibility = View.VISIBLE
			is HomeViewState.GenerationCompleted -> {
				binding.viewLoading.visibility = View.INVISIBLE
				showActivitySnack("Generating completed")
			}
		}
	}
	
	override fun setupViews() {
		colorPaletteValues = ChartsConstants.colorPalette.map { requireContext().getColorValue(it) }
		initStringRes()
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
		
		setupMyGarage()
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
		hryvnia = getString(R.string.hryvnia_symbol)
	}
	
	private fun setupMyGarage() = binding.rvMyGarage.run {
		setHasFixedSize(true)
		
		adapter = myGarageAdapter
		layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
		
		//adjust auto swipe to item center
		val snapHelper: SnapHelper = LinearSnapHelper()
		snapHelper.attachToRecyclerView(this)
	}
	
	private fun setupPieChartExpenses(stats: List<Pair<Vehicle, Expenses>>) = binding.run {
		btnPieChartExpensesSettings.setDebounceOnClick {
			showDialogPieChartExpensesSettings(
				stats.map {
					with(it.first) { "$brand $model ($year), $engineCapacity" }
				}.toTypedArray()
			)
		}
		
		pieChartExpenses.run {
			data = setupPieChartExpensesData(stats)
			
			description.apply {
				text = getString(R.string.fg_home_pie_chart_expenses_description)
				textColor = requireContext().getColorValue(R.color.colorOnBackground)
				textSize = 16f
				typeface = requireContext().getTypeface(R.font.m_plus_rounded1c_medium)
			}
			legend.apply {
				form = CIRCLE
				textColor = requireContext().getColorValue(R.color.colorOnBackground)
				textSize = 12f
				typeface = requireContext().getTypeface(R.font.m_plus_rounded1c_regular)
				xEntrySpace = 24f
			}
			
			transparentCircleRadius = 0f
			
			setHoleColor(Color.TRANSPARENT)
			holeRadius = 40f
			
			setEntryLabelColor(Color.BLACK)
			setEntryLabelTypeface(requireContext().getTypeface(R.font.m_plus_rounded1c_medium))
			
			animateY(1500, Easing.EaseInOutQuad)
			invalidate()
		}
	}
	private fun setupPieChartExpensesData(input: List<Pair<Vehicle, Expenses>>): PieData {
		
		val entries = input.take(if (input.size > 5) 5 else input.size).map {
			PieEntry(it.second.getTotal().toFloat().roundTo(2), it.first.brand)
		}
		val dataSet = PieDataSet(entries, "").apply {
			colors = colorPaletteValues.shuffled()
			valueFormatter = object : ValueFormatter() {
				override fun getFormattedValue(value: Float): String =
					if (value < 100000) String.format(priceFormatter, value)
					else MyLargeValueFormatter(hryvnia).getFormattedValue(value)
			}
			
			valueLineColor = requireContext().getColorValue(R.color.colorOnBackground)
			valueTextColor = requireContext().getColorValue(R.color.colorOnBackground)
			valueTextSize = 14f
			valueTypeface = requireContext().getTypeface(R.font.m_plus_rounded1c_medium)
			yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
		}
		
		return PieData(dataSet)
	}
	private fun setNewPieChartExpensesData(positions: List<Int>) {
		mViewModel.vehicles.value?.let { vehiclesWithExpenses ->
			binding.pieChartExpenses.apply {
				data = setupPieChartExpensesData(positions.map { vehiclesWithExpenses[it] })
				animateY(1400, Easing.EaseOutCirc)
			}
		}
	}
	private fun showDialogPieChartExpensesSettings(items: Array<String>) {
		val checkedItems = BooleanArray(items.size) {
			checkedExpensesPositions.contains(it)
		}
		
		MaterialAlertDialogBuilder(requireContext())
			.setTitle(R.string.fg_home_dialog_pie_chart_expenses_settings_title)
			.setMultiChoiceItems(items, checkedItems) { _, which, isChecked ->
				if (isChecked) checkedExpensesPositions.add(which)
				else checkedExpensesPositions.remove(which)
			}
			.setPositiveButton(R.string.dialog_btn_apply) { _, _ -> setNewPieChartExpensesData(checkedExpensesPositions) }
			.setNegativeButton(R.string.dialog_btn_cancel, null)
			.show()
	}
	

	
	private fun setupBarChartExpenses() = binding.barChartExpenses.run {
		extraBottomOffset = 10f
		
		description.run {
			text = hryvnia
			textSize = 20f
			typeface = requireContext().getTypeface(R.font.m_plus_rounded1c_light)
		}
		legend.isEnabled = false
		
		xAxis.run {
			position = BOTTOM
			setDrawGridLines(false)
			labelCount = 12
			
			textColor = requireContext().getColorValue(R.color.colorOnBackground)
			typeface = requireContext().getTypeface(R.font.m_plus_rounded1c_regular)
			
			//get first 3 letters from full month name
			valueFormatter = object : ValueFormatter() {
				override fun getAxisLabel(value: Float, axis: AxisBase?): String =
					value.toInt().dateMonthText().take(3)
			}
		}
		
		axisLeft.run {
			axisMinimum = 0f // this replaces setStartAtZero(true)
			setDrawGridLines(false)
			setLabelCount(8, false)
			spaceTop = 15f
			
			textColor = requireContext().getColorValue(R.color.colorOnBackground)
			typeface = requireContext().getTypeface(R.font.m_plus_rounded1c_regular)
			
			valueFormatter = MyLargeValueFormatter()
		}
		
		axisRight.run {
			axisMinimum = 0f // this replaces setStartAtZero(true)
			setLabelCount(8, false)
			spaceTop = 15f
			
			typeface = requireContext().getTypeface(R.font.m_plus_rounded1c_regular)
			textColor = requireContext().getColorValue(R.color.colorOnBackground)
			
			valueFormatter = MyLargeValueFormatter()
		}
		
		animateY(1500)
		invalidate()
	}
	private fun setupBarChartExpensesData(input: List<Expenses>) {
		val currentYear = currentLocalDateTime().year
		binding.tvBarChartDescription.text = getString(
			R.string.fg_home_bar_char_expenses_title, currentYear
		)
		val entries = input.mapIndexed { index, expenses ->
			BarEntry(
				(index + 1).toFloat(), // use index as month number, but months starts from 1
				expenses.getTotal().roundTo(2).toFloat()
			)
		}
		
		if (binding.barChartExpenses.data != null && binding.barChartExpenses.data.dataSetCount > 0) {
			(binding.barChartExpenses.data.getDataSetByIndex(0) as BarDataSet).values = entries
			binding.barChartExpenses.data.notifyDataChanged()
			binding.barChartExpenses.notifyDataSetChanged()
		}
		else {
			val dataSet = BarDataSet(entries, "$currentYear").apply {
				colors = colorPaletteValues
			}
			
			val data = BarData(dataSet).apply {
				barWidth = 0.9f
				setValueTextSize(10f)
				setValueFormatter(MyLargeValueFormatter())
				setValueTextColor(requireContext().getColorValue(R.color.colorOnBackground))
				setValueTypeface(requireContext().getTypeface(R.font.m_plus_rounded1c_regular))
			}
			
			binding.barChartExpenses.data = data
		}
	}
	
	
	
}