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
	
	override fun createFragment(position: Int): Fragment = ChildEditFragment.newInstance(position)
	
	override fun getItemCount(): Int = data.size
	
}