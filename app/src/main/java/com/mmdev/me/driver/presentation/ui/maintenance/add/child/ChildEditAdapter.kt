/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.10.2020 19:38
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance.add.child

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 *
 */

class ChildEditAdapter (
	private var data: List<Child>,
	parentFragment: Fragment
) : FragmentStateAdapter(parentFragment) {
	
	fun setNewData(newData: List<Child>) {
		data = newData
		notifyDataSetChanged()
	}
	
	override fun createFragment(position: Int): Fragment = ChildEditFragment(data[position])
	
	override fun getItemCount(): Int = data.size
	
}