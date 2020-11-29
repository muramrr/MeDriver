/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 28.11.2020 18:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.vehicle

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.extensions.roundTo
import com.mmdev.me.driver.databinding.FragmentVehicleBinding
import com.mmdev.me.driver.domain.maintenance.data.components.PlannedParts
import com.mmdev.me.driver.domain.vehicle.data.Expenses
import com.mmdev.me.driver.domain.vehicle.data.PendingReplacement
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import com.mmdev.me.driver.presentation.ui.MainActivity
import com.mmdev.me.driver.presentation.ui.common.BaseDropAdapter
import com.mmdev.me.driver.presentation.ui.common.custom.decorators.ConsumableVerticalItemDecorator
import com.mmdev.me.driver.presentation.ui.common.custom.decorators.GridItemDecoration
import com.mmdev.me.driver.presentation.ui.vehicle.add.VehicleAddBottomSheet
import com.mmdev.me.driver.presentation.utils.extensions.attachClickToCopyText
import com.mmdev.me.driver.presentation.utils.extensions.domain.humanDate
import com.mmdev.me.driver.presentation.utils.extensions.getStringRes
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
	private var mLessFrequentlyConsumablesAdapter1 = ConsumablesAdapter(emptyList())
	private var mLessFrequentlyConsumablesAdapter2 = ConsumablesAdapter(emptyList())
	
	private var currentTextOnDropDownList = ""
	
	
	private var expensesFormatter = ""
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel.shouldBeUpdated.observe(this, {
			if (it) mViewModel.getSavedVehicles()
		})
	}
	
	override fun setupViews() {
		initStringRes()
		
		initDropList()
		
		observeExpenses()
		setupMoreFrequentlyReplacements()
		
		observeVehicleList()
		observeChosenCar()
		observeReplacements()
		
		binding.btnMileageHistory.setDebounceOnClick {
			(requireActivity() as MainActivity).navigateTo(R.id.bottomNavFuel)
		}
		
		binding.btnExpensesChangeType.setDebounceOnClick {
			binding.radioExpensesType.run {
				visibleIf(otherwise = View.INVISIBLE) { this.visibility == View.INVISIBLE }
			}
		}
		
		//add callback to check what button is being checked
		binding.radioTiresType.addOnButtonCheckedListener { _, checkedId, isChecked ->
			// redundant if (value != field) check because toggling checks this by itself
			when {
				checkedId == binding.btnTiresSummer.id && isChecked -> {
					binding.pgTiresWear.updateProgress(20)
				}
				
				checkedId == binding.btnTiresWinter.id && isChecked -> {
					binding.pgTiresWear.updateProgress(70)
				}
			}
		}
		binding.radioTiresType.check(binding.btnTiresSummer.id)
		
		
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
					}
				}
				else setText(currentTextOnDropDownList, false)
				
			}
			
		}
	}
	
	private fun setupMoreFrequentlyReplacements() {
		binding.rvFrequentlyConsumables.apply {
			adapter = mFrequentlyConsumablesAdapter
			layoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
			addItemDecoration(GridItemDecoration(true))
		}
		
		binding.rvLessFrequentlyConsumables1.apply {
			adapter = mLessFrequentlyConsumablesAdapter1
			layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
			addItemDecoration(ConsumableVerticalItemDecorator())
		}
		
		binding.rvLessFrequentlyConsumables2.apply {
			adapter = mLessFrequentlyConsumablesAdapter2
			layoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
			addItemDecoration(GridItemDecoration(true))
		}
	}
	
	private fun setupInsuranceCard(pendingReplacement: PendingReplacement?) {
		binding.apply {
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
	}
	
	private fun setupExpensesCard(expenses: Expenses) {
		binding.apply {
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
	}
	
	private fun observeChosenCar() {
		mViewModel.chosenVehicle.observe(this, { vehicle ->
			if (vehicle != null) {
				binding.dropMyCarChooseCar.setText(
					getString(R.string.two_strings_whitespace_formatter, vehicle.brand, vehicle.model),
					false
				)
				if (vehicle.lastRefillDate.isNotBlank()) binding.tvMileageLastDate.text = vehicle.lastRefillDate
				
				binding.btnCopyVin.attachClickToCopyText(requireContext(), R.string.fg_vehicle_copy_vin)
				binding.btnCopyVin.text = vehicle.vin
			}
			else {
				binding.btnCopyVin.setOnClickListener(null)
				binding.btnCopyVin.text = getString(R.string.fg_vehicle_card_replacements_subtitle_no_vehicle)
			}
			
			
			sharedViewModel.currentVehicle.postValue(vehicle)
			currentTextOnDropDownList = binding.dropMyCarChooseCar.text()
		})
	}
	
	private fun observeVehicleList() {
		mViewModel.vehicleUiList.observe(this, {
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
	}
	
	private fun observeExpenses() = mViewModel.expenses.observe(this, {
		setupExpensesCard(it)
	})
	
	
	private fun observeReplacements() {
		mViewModel.replacements.observe(this, {
			with(mViewModel.buildConsumables(it)) {
				mFrequentlyConsumablesAdapter.setNewData(drop(1).take(4))
				mLessFrequentlyConsumablesAdapter1.setNewData(drop(5).take(2))
				mLessFrequentlyConsumablesAdapter2.setNewData(drop(7))
			}
			
			setupInsuranceCard(it?.get(PlannedParts.INSURANCE))
		})
	}
	
	
	
	
	private fun showAddVehicleBottomSheet() = VehicleAddBottomSheet().show(
		childFragmentManager, VehicleAddBottomSheet::class.java.canonicalName
	)
	
	
	
	
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
				isEnabled = (position == 0) || ((position != 0) && MedriverApp.currentUser != null && MedriverApp.currentUser!!.isSubscriptionValid())
			}
			childView.findViewById<ImageView>(R.id.ivDropCarItemIcon).apply {
				setImageResource(vehicle.icon)
				isEnabled = (position == 0) || ((position != 0) && MedriverApp.currentUser != null && MedriverApp.currentUser!!.isSubscriptionValid())
			}
			//if no premium, only first position will be available
			childView.isEnabled = (position == 0) || ((position != 0) && MedriverApp.currentUser != null && MedriverApp.currentUser!!.isSubscriptionValid())
			childView.findViewById<TextView>(R.id.tvDropCarItemProLabel).visibleIf(View.INVISIBLE, 0) {
				position == count - 1 && !childView.isEnabled
			}
			return childView
		}
		
		
	}
}