/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 12.10.2020 20:57
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance.add.child

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart


/**
 *
 */

class EditChildAdapter (
	private var data: List<Pair<String, SparePart>>,
	fm: FragmentManager,
	lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {
	
	fun setNewData(newData: List<Pair<String, SparePart>>) {
		data = newData
		notifyDataSetChanged()
	}
	
	override fun createFragment(position: Int): Fragment = EditChildFragment(data[position])
	
	override fun getItemCount(): Int = data.size
	
}