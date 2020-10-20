/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 15.10.2020 15:51
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance.add.parent

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mmdev.me.driver.R
import com.mmdev.me.driver.R.string
import com.mmdev.me.driver.databinding.ItemMaintenanceParentNodeBinding
import com.mmdev.me.driver.presentation.ui.common.BaseRecyclerAdapter

/**
 * Used to display parent nodes list
 * @see ParentNodeUi
 *
 * Expand each viewHolder to be 1/4 screen height and 1/2 screen width
 */

class ParentNodeAdapter (
	data: List<ParentNodeUi> = listOf(
		ParentNodeUi(
			string.btm_sheet_maintenance_add_engine_title,
			string.btm_sheet_maintenance_add_engine_components,
			R.array.maintenance_engine_components,
			R.drawable.ic_node_engine_48
		),
		ParentNodeUi(
			string.btm_sheet_maintenance_add_transmission_title,
			string.btm_sheet_maintenance_add_transmission_components,
			R.array.maintenance_transmission_components,
			R.drawable.ic_node_transmission_48
		),
		ParentNodeUi(
			string.btm_sheet_maintenance_add_electrics_title,
			string.btm_sheet_maintenance_add_electrics_components,
			R.array.maintenance_electric_components,
			R.drawable.ic_node_electrics_48
		),
		ParentNodeUi(
			string.btm_sheet_maintenance_add_suspension_title,
			string.btm_sheet_maintenance_add_suspension_components,
			R.array.maintenance_suspension_components,
			R.drawable.ic_node_suspension_48
		),
		ParentNodeUi(
			string.btm_sheet_maintenance_add_brakes_title,
			string.btm_sheet_maintenance_add_brakes_components,
			R.array.maintenance_brakes_components,
			R.drawable.ic_node_brakes_48
		),
		ParentNodeUi(
			string.btm_sheet_maintenance_add_vehicle_body_title,
			string.btm_sheet_maintenance_add_vehicle_body_components,
			R.array.maintenance_body_components,
			R.drawable.ic_node_body_48
		),
		ParentNodeUi(
			string.btm_sheet_maintenance_add_other_title,
			string.btm_sheet_maintenance_add_other_components,
			0,
			0
		),
		ParentNodeUi(
			string.btm_sheet_maintenance_add_planned_title,
			string.btm_sheet_maintenance_add_planned_components,
			R.array.maintenance_planned_components,
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
			binding.root.layoutParams.height = parent.height / 4 - 30
			binding.root.requestLayout()
		}
		return BaseViewHolder(binding)

	}
}