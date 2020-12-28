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

package com.mmdev.me.driver.presentation.ui.garage

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorInt
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
import com.mmdev.me.driver.databinding.FragmentGarageBinding
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

class GarageFragment : BaseFlowFragment<GarageViewModel, FragmentGarageBinding>(
	layoutId = R.layout.fragment_garage
) {

	override val mViewModel: GarageViewModel by viewModel()
	
	private val myGarageAdapter = MyGarageAdapter()
	
	private val currentYear = currentLocalDateTime().year
	
	private val checkedExpensesPositions = mutableListOf(0, 1, 2, 3, 4)
	
	private var priceFormatter = ""
	private var hryvnia = ""
	@ColorInt private var colorOnBackground = 0x000000
	
	private var colorPaletteValues = listOf<Int>()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel.viewState.observe(this, { renderState(it) })
	}
	
	override fun renderState(state: ViewState) {
		when(state) {
			is GarageViewState.Error -> showActivitySnack(state.errorMessage ?: "Error")
			is GarageViewState.GeneratingStarted -> binding.viewLoading.visibility = View.VISIBLE
			is GarageViewState.GenerationCompleted -> {
				binding.viewLoading.visibility = View.INVISIBLE
				showActivitySnack("Generating completed")
			}
		}
	}
	
	override fun setupViews() {
		initRes()
		
		if (!MedriverApp.dataGenerated && MedriverApp.debug.isEnabled) {
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
		setupPieChartExpenses()
		setupBarChartExpenses()
		
		binding.btnPieChartExpensesSettings.setDebounceOnClick {
			showDialogPieChartExpensesSettings()
		}
		
		binding.tvBarChartDescription.text = getString(
			R.string.fg_home_bar_char_expenses_title, currentYear
		)
		
		mViewModel.vehicles.observe(this, {
			if (!it.isNullOrEmpty()) myGarageAdapter.setNewData(it)
		})
		
		mViewModel.vehiclesWithExpenses.observe(this, { vehiclesWithExpenses ->
			// if vehicles < 5 -> leave only positions <= 5
			checkedExpensesPositions.removeAll { it >= vehiclesWithExpenses.size }
			if (!vehiclesWithExpenses.isNullOrEmpty()) setupPieChartExpensesData(vehiclesWithExpenses)
		})
		
		mViewModel.expensesPerYear.observe(this, { expenses ->
			if (!expenses.isNullOrEmpty()) setupBarChartExpensesData(expenses)
		})
		
	}

	private fun initRes() {
		colorPaletteValues = ChartsConstants.colorPalette.map { requireContext().getColorValue(it) }
		colorOnBackground = requireContext().getColorValue(R.color.colorOnBackground)
		priceFormatter = getString(R.string.price_formatter_right)
		hryvnia = getString(R.string.hryvnia_symbol)
	}
	
	private fun setupMyGarage() = binding.rvMyGarage.run {
		setHasFixedSize(true)
		
		adapter = myGarageAdapter
		layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
		
		//adjust auto swipe to item center
		val snapHelper: SnapHelper = LinearSnapHelper()
		snapHelper.attachToRecyclerView(this)
	}
	
	private fun setupPieChartExpenses() = binding.pieChartExpenses.run {
		setNoDataText(getString(R.string.not_enough_data))
		setNoDataTextColor(context.getColorValue(R.color.colorPrimarySecondary))
		setNoDataTextTypeface(context.getTypeface(R.font.m_plus_rounded1c_regular))
		
		centerText = hryvnia
		setCenterTextTypeface(context.getTypeface(R.font.m_plus_rounded1c_medium))
		setCenterTextColor(colorOnBackground)
		setCenterTextSize(24f)
		
		description.apply {
			text = getString(R.string.fg_home_pie_chart_expenses_description)
			textColor = colorOnBackground
			textSize = 16f
			typeface = context.getTypeface(R.font.m_plus_rounded1c_medium)
		}
		legend.apply {
			form = CIRCLE
			textColor = colorOnBackground
			textSize = 12f
			typeface = context.getTypeface(R.font.m_plus_rounded1c_regular)
			xEntrySpace = 24f
		}
		
		transparentCircleRadius = 0f
		
		setHoleColor(Color.TRANSPARENT)
		holeRadius = 40f
		
		setEntryLabelColor(Color.BLACK)
		setEntryLabelTypeface(context.getTypeface(R.font.m_plus_rounded1c_medium))
		
		animateY(1500, Easing.EaseInOutQuad)
		invalidate()
	}
	private fun setupPieChartExpensesData(input: List<Pair<Vehicle, Expenses>>) {
		
		val entries = input.take(if (input.size > 5) 5 else input.size).map {
			PieEntry(it.second.getTotal().toFloat().roundTo(2), it.first.brand)
		}
		with(binding.pieChartExpenses) {
			//update existing data set
			if (data != null && data.dataSetCount > 0) {
				val existingDataSet = (data.getDataSetByIndex(0) as PieDataSet)
				existingDataSet.values = entries
				existingDataSet.notifyDataSetChanged()
				data.notifyDataChanged()
				notifyDataSetChanged()
			}
			//create new data set
			else {
				val dataSet = PieDataSet(entries, "").apply {
					colors = colorPaletteValues.shuffled()
					valueFormatter = object : ValueFormatter() {
						override fun getFormattedValue(value: Float): String =
							if (value < 100000) String.format(priceFormatter, value)
							else MyLargeValueFormatter(hryvnia).getFormattedValue(value)
					}
					
					valueLineColor = colorOnBackground
					valueTextColor = colorOnBackground
					valueTextSize = 14f
					valueTypeface = requireContext().getTypeface(R.font.m_plus_rounded1c_medium)
					yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
				}
				val pieData = PieData(dataSet)
				
				// set data
				data = pieData
			}
			animateY(1400, Easing.EaseOutCirc)
		
		}
		
	}
	private fun setNewPieChartExpensesData(positions: List<Int>) {
		mViewModel.vehiclesWithExpenses.value?.let { vehiclesWithExpenses ->
			setupPieChartExpensesData(positions.map { vehiclesWithExpenses[it] })
		}
	}
	
	private fun showDialogPieChartExpensesSettings() =
		mViewModel.vehiclesWithExpenses.value?.map {
			with(it.first) { "$brand $model ($year), $engineCapacity" }
		}?.toTypedArray()?.let { items ->
			
			//pre-check initial items
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
				.create()
				.show()
		}
	
	

	
	private fun setupBarChartExpenses() = binding.barChartExpenses.run {
		setNoDataText(getString(R.string.not_enough_data))
		setNoDataTextColor(context.getColorValue(R.color.colorPrimarySecondary))
		setNoDataTextTypeface(context.getTypeface(R.font.m_plus_rounded1c_regular))
		
		extraBottomOffset = 10f
		
		description.run {
			text = hryvnia
			textSize = 20f
			typeface = context.getTypeface(R.font.m_plus_rounded1c_light)
		}
		legend.isEnabled = false
		
		xAxis.run {
			position = BOTTOM
			setDrawGridLines(false)
			labelCount = 12
			
			textColor = colorOnBackground
			typeface = context.getTypeface(R.font.m_plus_rounded1c_regular)
			
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
			
			textColor = colorOnBackground
			typeface = context.getTypeface(R.font.m_plus_rounded1c_regular)
			
			valueFormatter = MyLargeValueFormatter()
		}
		
		axisRight.run {
			axisMinimum = 0f // this replaces setStartAtZero(true)
			setLabelCount(8, false)
			spaceTop = 15f
			
			textColor = colorOnBackground
			typeface = context.getTypeface(R.font.m_plus_rounded1c_regular)
			
			valueFormatter = MyLargeValueFormatter()
		}
		
		animateY(1500)
		invalidate()
	}
	private fun setupBarChartExpensesData(input: List<Expenses>) {
		
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
				setValueTextColor(colorOnBackground)
				setValueTypeface(requireContext().getTypeface(R.font.m_plus_rounded1c_regular))
			}
			
			binding.barChartExpenses.data = data
		}
	}
	
	
	
}