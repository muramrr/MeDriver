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

package com.mmdev.me.driver.presentation.ui.vehicle

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineDataSet.Mode.HORIZONTAL_BEZIER
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.MetricSystem.*
import com.mmdev.me.driver.core.utils.extensions.convertToLocalDateTime
import com.mmdev.me.driver.core.utils.extensions.roundTo
import com.mmdev.me.driver.databinding.FragmentVehicleBinding
import com.mmdev.me.driver.domain.vehicle.data.ConsumptionHistory
import com.mmdev.me.driver.domain.vehicle.data.Expenses
import com.mmdev.me.driver.domain.vehicle.data.PendingReplacement
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import com.mmdev.me.driver.presentation.ui.MainActivity
import com.mmdev.me.driver.presentation.ui.common.BaseDropAdapter
import com.mmdev.me.driver.presentation.ui.common.ChartsConstants
import com.mmdev.me.driver.presentation.ui.common.custom.decorators.GridItemDecoration
import com.mmdev.me.driver.presentation.ui.vehicle.add.VehicleAddBottomSheet
import com.mmdev.me.driver.presentation.ui.vehicle.data.VehicleUi
import com.mmdev.me.driver.presentation.ui.vehicle.edit.EditVehicleRegulationsDialog
import com.mmdev.me.driver.presentation.utils.extensions.attachClickToCopyText
import com.mmdev.me.driver.presentation.utils.extensions.domain.dateMonthText
import com.mmdev.me.driver.presentation.utils.extensions.domain.getValue
import com.mmdev.me.driver.presentation.utils.extensions.domain.humanDate
import com.mmdev.me.driver.presentation.utils.extensions.domain.humanDay
import com.mmdev.me.driver.presentation.utils.extensions.getColorValue
import com.mmdev.me.driver.presentation.utils.extensions.getStringRes
import com.mmdev.me.driver.presentation.utils.extensions.getTypeface
import com.mmdev.me.driver.presentation.utils.extensions.invisible
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
import com.mmdev.me.driver.presentation.utils.extensions.text
import com.mmdev.me.driver.presentation.utils.extensions.visibleIf
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *
 */

class VehicleFragment : BaseFlowFragment<VehicleViewModel, FragmentVehicleBinding>(
	layoutId = R.layout.fragment_vehicle
) {
	
	override val mViewModel: VehicleViewModel by viewModel()
	
	private lateinit var mVehicleDropAdapter: VehicleDropAdapter
	
	private var mFrequentlyConsumablesAdapter = ConsumablesAdapter(emptyList())
	private var mLessFrequentlyConsumablesAdapter = ConsumablesAdapter(emptyList())
	
	private var currentTextOnDropDownList = ""
	private var chosenVehicleUi: VehicleUi? = null
	
	private var expensesFormatter = ""
	
	private var colorPaletteValues = listOf<Int>()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel.newWasAdded.observe(this, {
			if (it) mViewModel.getSavedVehicles()
		})
	}
	
	override fun setupViews() {
		colorPaletteValues = ChartsConstants.colorPalette.map { requireContext().getColorValue(it) }
		initStringRes()
		
		initDropList()
		
		observeExpenses()
		//setupTiresWear()
		
		setupLineChartConsumption()
		setupReplacements()
		
		observeVehicleList()
		observeChosenCar()
		observeReplacements()
		observeFuelConsumption()
		
		binding.apply {
			btnMileageHistory.setDebounceOnClick {
				(requireActivity() as MainActivity).navigateTo(R.id.bottomNavFuel)
			}
			
			btnExpensesChangeType.setDebounceOnClick {
				radioExpensesType.run {
					visibleIf(otherwise = View.INVISIBLE) { this.visibility == View.INVISIBLE }
				}
			}
			
			btnEditRegulations.setDebounceOnClick {
				EditVehicleRegulationsDialog().show(
					childFragmentManager, EditVehicleRegulationsDialog::class.java.canonicalName
				)
			}
			
			btnDeleteVehicle.setDebounceOnClick { showDeleteVehicleConfirmationDialog() }
		}
		
	}
	
	private fun initStringRes() {
		expensesFormatter = getString(R.string.price_formatter_right)
	}
	
	private fun initDropList() {
		mVehicleDropAdapter = VehicleDropAdapter(
			requireContext(),
			R.layout.item_drop_vehicles,
			emptyList()
		)
		
		binding.dropMyCarChooseCar.apply {
			setAdapter(mVehicleDropAdapter)
			
			setOnItemClickListener { _, view, position, _ ->
				
				if (view.isEnabled) {
					if (position == mVehicleDropAdapter.count - 1) {
						
						setText(currentTextOnDropDownList, false)
						showAddVehicleBottomSheet()
						
					}
					else {
						mViewModel.setVehicle(position)
						chosenVehicleUi = mViewModel.vehicleUiList.value?.get(position)
					}
				}
				else setText(currentTextOnDropDownList, false)
				
			}
			
		}
	}
	
//	private fun setupTiresWear() {
//		binding.apply {
//			radioTiresType.addOnButtonCheckedListener { _, checkedId, isChecked ->
//				// redundant if (value != field) check because toggling checks this by itself
//				when {
//					checkedId == btnTiresSummer.id && isChecked -> {
//						pgTiresWear.updateProgress(20)
//					}
//
//					checkedId == btnTiresWinter.id && isChecked -> {
//						pgTiresWear.updateProgress(70)
//					}
//				}
//			}
//
//			radioTiresType.check(btnTiresSummer.id)
//		}
//	}
	
	private fun setupReplacements() {
		binding.rvFrequentlyConsumables.apply {
			adapter = mFrequentlyConsumablesAdapter
			layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
			addItemDecoration(GridItemDecoration(true))
		}
		
		binding.rvLessFrequentlyConsumables.apply {
			adapter = mLessFrequentlyConsumablesAdapter
			layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
			addItemDecoration(GridItemDecoration(true))
		}
	}
	
	
	
	
	
	private fun observeChosenCar() = mViewModel.chosenVehicle.observe(this, { vehicle ->
		if (vehicle != null) { setVehicle(vehicle) }
		else { setVehicleNull() }
		
		sharedViewModel.currentVehicle.postValue(vehicle)
		currentTextOnDropDownList = binding.dropMyCarChooseCar.text()
	})
	
	private fun setVehicle(vehicle: Vehicle) {
		binding.dropMyCarChooseCar.setText(
			getString(R.string.two_strings_whitespace_formatter, vehicle.brand, vehicle.model),
			false
		)
		if (vehicle.lastRefillDate.isNotBlank())
			binding.tvMileageLastDate.text = vehicle.lastRefillDate
		
		binding.btnCopyVin.attachClickToCopyText {
			showActivitySnack(getString(R.string.fg_vehicle_copy_vin).format(it))
		}
		binding.btnCopyVin.text = vehicle.vin
		
		chosenVehicleUi = mViewModel.convertToUiVehicle(vehicle)
	}
	private fun setVehicleNull() {
		chosenVehicleUi = null
		binding.dropMyCarChooseCar.setText(getString(R.string.fg_vehicle_choose_vehicle), false)
		binding.btnCopyVin.setOnClickListener(null)
		binding.btnCopyVin.text = getString(R.string.fg_vehicle_card_replacements_subtitle_no_vehicle)
	}
	
	private fun observeVehicleList() = mViewModel.vehicleUiList.observe(this, {
		mVehicleDropAdapter.setNewData(it)
		when {
			!it.isNullOrEmpty() -> {
				binding.dropMyCarChooseCar.setOnClickListener(null)
				if (mViewModel.chosenVehicle.value == null)
					binding.dropMyCarChooseCar.setText(R.string.fg_vehicle_choose_vehicle)
			}
			it.isNullOrEmpty() -> {
				binding.dropMyCarChooseCar.setOnClickListener { showAddVehicleBottomSheet() }
				binding.dropMyCarChooseCar.setText(R.string.fg_vehicle_add_new_vehicle)
			}
		}
		currentTextOnDropDownList = binding.dropMyCarChooseCar.text()
	})
	
	
	private fun observeExpenses() = mViewModel.expensesData.observe(this, {
		setExpensesCard(it)
	})
	private fun setExpensesCard(expenses: Expenses) = binding.run {
		tvExpensesValue.text = expensesFormatter.format(
			expenses.getTotal().roundTo(2)
		)
		
		radioExpensesType.setOnCheckedChangeListener { group, checkedId ->
			when (checkedId) {
				R.id.btnExpensesTotal -> {
					tvExpensesType.text = getString(R.string.fg_vehicle_card_expenses_type_total)
					tvExpensesValue.text = expensesFormatter.format(
						expenses.getTotal().roundTo(2)
					)
				}
				
				R.id.btnExpensesMaintenance -> {
					tvExpensesType.text = getString(R.string.fg_vehicle_card_expenses_type_maintenance)
					
					tvExpensesValue.text = expensesFormatter.format(
						expenses.maintenance.roundTo(2)
					)
				}
				
				R.id.btnExpensesFuel -> {
					tvExpensesType.text = getString(R.string.fg_vehicle_card_expenses_type_fuel)
					
					tvExpensesValue.text = expensesFormatter.format(
						expenses.fuel.roundTo(2)
					)
				}
			}
			group.invisible()
		}
	}
	
	
	
	private fun observeReplacements() = mViewModel.replacements.observe(this, {
		setInsuranceCard(it.first().replacement)
		//first is insurance, we have separate card for it, so drop it
		mFrequentlyConsumablesAdapter.setNewData(it.drop(1).take(4))
		mLessFrequentlyConsumablesAdapter.setNewData(it.drop(5))
	
	})
	private fun setInsuranceCard(pendingReplacement: PendingReplacement?) = binding.run {
		if (pendingReplacement != null) {
			tvInsuranceSubtitle.text = getString(
				R.string.fg_vehicle_insurance_subtitle,
				pendingReplacement.finalDate.humanDate()
			)
			tvInsuranceValue.text = pendingReplacement.componentSpecs
		}
		else {
			tvInsuranceSubtitle.text = getString(R.string.fg_vehicle_card_replacements_value_not_replaced)
			tvInsuranceValue.text = getString(R.string.undefined)
		}
		
	}
	
	
	
	private fun showDeleteVehicleConfirmationDialog() = MaterialAlertDialogBuilder(requireContext())
		.setTitle(R.string.fg_vehicle_dialog_delete_title)
		.setMessage(
			getString(R.string.fg_vehicle_dialog_delete_message_formatter).format(
				MainActivity.currentVehicle?.getVehicleUiName(),
				MainActivity.currentVehicle?.vin
			)
		)
		.setIcon(chosenVehicleUi?.icon ?: 0)
		.setNeutralButton(R.string.fg_vehicle_dialog_delete_btn_neutral, null)
		.setPositiveButton(R.string.fg_vehicle_dialog_delete_btn_positive) { _, _ ->
			mViewModel.deleteVehicle()
		}
		.create()
		.show()
	
	private fun showAddVehicleBottomSheet() = VehicleAddBottomSheet().show(
		childFragmentManager, VehicleAddBottomSheet::class.java.canonicalName
	)
	
	
	
	
	private fun observeFuelConsumption() = mViewModel.fuelConsumptionData.observe(this, {
		if (it.isNotEmpty()) setupLineChartConsumptionData(it)
	})
	
	private fun setupLineChartConsumption() = binding.lineChartFuelConsumption.run {
		extraBottomOffset = 10f
		setDrawGridBackground(false)
		// no description text
		description.apply {
			text = when (MedriverApp.metricSystem) {
				KILOMETERS -> getString(R.string.item_fuel_history_entry_consumption_km, "")
				MILES -> getString(R.string.item_fuel_history_entry_consumption_mi, "")
			}
			textSize = 14f
			typeface = requireContext().getTypeface(R.font.m_plus_rounded1c_light)
		}
		legend.isEnabled = false
		
		//isLogEnabled = MedriverApp.debug.isEnabled
		// enable scaling and dragging
		isDoubleTapToZoomEnabled = false
		isDragYEnabled = false
		isScaleYEnabled = false
		
		xAxis.run {
			position = BOTTOM
			setDrawGridLines(false)
			textColor = context.getColorValue(R.color.colorOnBackground)
			typeface = requireContext().getTypeface(R.font.m_plus_rounded1c_regular)
			
			valueFormatter = object : ValueFormatter() {
				override fun getFormattedValue(value: Float): String {
					/**
					 * 'If' statement here fixes back pressure appearing while notifying DataSetChanged
					 * some indexes exists for some amount of time after changing data,
					 * this because our current focus relies on last values i.e. end of data array
					 */
					val date = data.getDataSetByIndex(0).run {
						if (this.entryCount < value.toInt()) 0
						else getEntryForIndex(value.toInt()).data as Long
					}
					return convertToLocalDateTime(date).run {
						"${humanDay(dayOfMonth)} " +
						monthNumber.dateMonthText().take(3) +
						"'" + "$year".drop(2)
					}
				}
			}
		}
		
		axisRight.run {
			setDrawGridLines(false)
			textColor = context.getColorValue(R.color.colorOnBackground)
			typeface = requireContext().getTypeface(R.font.m_plus_rounded1c_regular)
		}
		axisLeft.isEnabled = false
		
		val highlightMarker = object : MarkerView(context, R.layout.chart_marker) {
			override fun refreshContent(e: Entry, highlight: Highlight) {
				findViewById<TextView>(R.id.tvChartMarker).text = xAxis.valueFormatter.getFormattedValue(e.x)
				super.refreshContent(e, highlight)
			}
		}
		highlightMarker.chartView = this
		marker = highlightMarker
		
		animateX(1000)
		
	}
	private fun setupLineChartConsumptionData(input: List<ConsumptionHistory>) {
		val entries = input.mapIndexed { index, consumptionHistory ->
			Entry(
				index.toFloat(),
				consumptionHistory.consumptionBound.getValue().toFloat(),
				consumptionHistory.date
			)
		}
		
		with(binding.lineChartFuelConsumption) {
			if (data != null && data.dataSetCount > 0) {
				val existingDataSet = (data.getDataSetByIndex(0) as LineDataSet)
				existingDataSet.values = entries
				existingDataSet.notifyDataSetChanged()
				data.notifyDataChanged()
				notifyDataSetChanged()
			} else {
				// create a LineDataSet and customize it
				val dataSet: LineDataSet = LineDataSet(entries, "Fuel Consumption").apply {
					setDrawFilled(true)
					fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.fade_primary)
					mode = HORIZONTAL_BEZIER
					setDrawCircles(true)
					setCircleColor(Color.BLACK)
					circleRadius = 4f
					
					color = context.getColorValue(R.color.colorPrimary)
					lineWidth = 2f
					valueTextColor = context.getColorValue(R.color.colorOnBackground)
					valueTextSize = 12f
				}
				
				// create a data object with the data sets
				val lineData = LineData(dataSet).apply {
					setValueTextColor(context.getColorValue(R.color.colorOnBackground))
					setValueTypeface(requireContext().getTypeface(R.font.m_plus_rounded1c_regular))
				}
				// set data
				data = lineData
			}
			
			setVisibleXRange(10f, if (entries.size < 10) (entries.size - 1).toFloat() else 0f)
			moveViewToX(xChartMax)
		}
		
	}
	
	
	private class VehicleDropAdapter(
		context: Context,
		@LayoutRes private val layoutId: Int,
		data: List<VehicleUi>
	): BaseDropAdapter<VehicleUi>(context, layoutId, data) {
		
		override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
			val vehicle: VehicleUi = getItem(position)
			val childView: View = convertView ?: LayoutInflater.from(context).inflate(layoutId, null)
			
			childView.findViewById<TextView>(R.id.tvDropCarItemText).apply {
				text = if (vehicle.titleRes != null) getStringRes(vehicle.titleRes) else vehicle.title
				isEnabled = (position == 0) || ((position != 0) && MainActivity.currentUser?.isSubscribed() ?: false)
			}
			childView.findViewById<ImageView>(R.id.ivDropCarItemIcon).apply {
				setImageResource(vehicle.icon)
				isEnabled = (position == 0) || ((position != 0) && MainActivity.currentUser?.isSubscribed() ?: false)
			}
			//if no premium, only first position will be available//todo: uncomment for release
//			childView.isEnabled = (position == 0) || ((position != 0) && MainActivity.currentUser?.isSubscribed() ?: false)
			childView.findViewById<TextView>(R.id.tvDropCarItemPremiumLabel).visibleIf(View.INVISIBLE, 0) {
				!childView.isEnabled
			}
			return childView
		}
	}
	
}