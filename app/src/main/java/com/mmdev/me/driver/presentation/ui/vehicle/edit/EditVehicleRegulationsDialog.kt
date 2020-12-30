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

package com.mmdev.me.driver.presentation.ui.vehicle.edit

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.utils.extensions.currentEpochTime
import com.mmdev.me.driver.core.utils.helpers.DateHelper
import com.mmdev.me.driver.databinding.DialogVehicleRegulationsEditBinding
import com.mmdev.me.driver.domain.vehicle.data.Regulation
import com.mmdev.me.driver.presentation.core.base.BaseDialogFragment
import com.mmdev.me.driver.presentation.ui.MainActivity
import com.mmdev.me.driver.presentation.ui.SharedViewModel
import com.mmdev.me.driver.presentation.ui.common.custom.decorators.LinearItemDecoration
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 *
 */

class EditVehicleRegulationsDialog : BaseDialogFragment<Nothing, DialogVehicleRegulationsEditBinding>(
	layoutId = R.layout.dialog_vehicle_regulations_edit
) {
	override val mViewModel: Nothing? = null
	private val sharedViewModel: SharedViewModel by sharedViewModel()
	
	private val mAdapter = EditRegulationAdapter(emptyList())
	private var positionToEdit = 0
	
	companion object {
		private const val POSITION_TO_EDIT = "position"
		fun newInstance(position: Int) = EditVehicleRegulationsDialog().apply {
			arguments = bundleOf(POSITION_TO_EDIT to position)
		}
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(STYLE_NORMAL, R.style.My_Dialog_FullScreen)
		mAdapter.setNewData(getRegulations())
		arguments?.let {
			positionToEdit = it.getInt(POSITION_TO_EDIT, 0)
		}
	}
	
	override fun onStart() {
		super.onStart()
		dialog!!.window!!.setWindowAnimations(R.style.AnimSlideFromTopToTop)
	}
	
	override fun setupViews() = binding.run {
		
		rvVehicleMaintenanceEdit.run {
			setHasFixedSize(true)
			adapter = mAdapter
			layoutManager = LinearLayoutManager(requireContext())
			addItemDecoration(LinearItemDecoration())
			smoothScrollToPosition(positionToEdit)
		}
		
		btnCancel.setOnClickListener { dismiss() }
		
		btnSave.setOnClickListener {
			sharedViewModel.updateVehicle(
				MainActivity.currentUser,
				MainActivity.currentVehicle!!.copy(
					maintenanceRegulations = convertRegulations(mAdapter.getOutput()),
					lastUpdatedDate = currentEpochTime()
				)
			)
			dismiss()
		}
		
	}
	
	private fun getRegulations(): List<RegulationUi> =
		MainActivity.currentVehicle?.maintenanceRegulations?.map {
			RegulationUi(
				it.key,
				it.value.distance,
				DateHelper.getYearsCount(it.value.time)
			)
		} ?: emptyList()
	
	private fun convertRegulations(input: List<RegulationUi>) = input.map {
		it.part to Regulation(it.distance, it.yearsCount)
	}.toMap()
	
}