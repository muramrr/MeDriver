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

package com.mmdev.me.driver.presentation.ui.maintenance.add.parent

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.ItemMaintenanceParentNodeBinding
import com.mmdev.me.driver.presentation.ui.common.BaseRecyclerAdapter
import com.mmdev.me.driver.presentation.ui.maintenance.VehicleSystemNodeConstants

/**
 * Used to display parent nodes list
 * @see ParentNodeUi
 *
 * Expand each viewHolder to be 1/4 screen height and 1/2 screen width
 */

class ParentNodeAdapter (
	data: List<ParentNodeUi> = listOf(
		ParentNodeUi(
			R.string.maintenance_parent_node_engine,
			R.string.maintenance_parent_node_engine_components,
			VehicleSystemNodeConstants.engineComponents,
			R.drawable.ic_node_engine_48
		),
		ParentNodeUi(
			R.string.maintenance_parent_node_transmission,
			R.string.maintenance_parent_node_transmission_components,
			VehicleSystemNodeConstants.transmissionComponents,
			R.drawable.ic_node_transmission_48
		),
		ParentNodeUi(
			R.string.maintenance_parent_node_electrics,
			R.string.maintenance_parent_node_electrics_components,
			VehicleSystemNodeConstants.electricsComponents,
			R.drawable.ic_node_electrics_48
		),
		ParentNodeUi(
			R.string.maintenance_parent_node_suspension,
			R.string.maintenance_parent_node_suspension_components,
			VehicleSystemNodeConstants.suspensionComponents,
			R.drawable.ic_node_suspension_48
		),
		ParentNodeUi(
			R.string.maintenance_parent_node_brakes,
			R.string.maintenance_parent_node_brakes_components,
			VehicleSystemNodeConstants.brakesComponents,
			R.drawable.ic_node_brakes_48
		),
		ParentNodeUi(
			R.string.maintenance_parent_node_body,
			R.string.maintenance_parent_node_vehicle_body_components,
			VehicleSystemNodeConstants.bodyComponents,
			R.drawable.ic_node_body_48
		),
		ParentNodeUi(
			R.string.maintenance_parent_node_other,
			R.string.maintenance_parent_node_other_components,
			IntArray(0),
			0
		),
		ParentNodeUi(
			R.string.maintenance_parent_node_planned,
			R.string.maintenance_parent_node_planned_components,
			VehicleSystemNodeConstants.plannedComponents,
			R.drawable.ic_node_planned_48
		)
	)
): BaseRecyclerAdapter<ParentNodeUi>(data, R.layout.item_maintenance_parent_node) {
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ParentNodeUi> {
		val binding = ItemMaintenanceParentNodeBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		binding.root.post {
			binding.root.layoutParams.height = parent.height / 4 - 20
			binding.root.requestLayout()
		}
		return BaseViewHolder(binding)

	}
}