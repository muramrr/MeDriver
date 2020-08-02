/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.08.20 18:43
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.mycar


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mmdev.me.driver.R
import com.mmdev.me.driver.presentation.ui.mycar.MyCarFragment.CarInDropDown


/**
 *
 */


internal class DropAdapter(context: Context, private val data: ArrayList<CarInDropDown>):
		ArrayAdapter<CarInDropDown>(context, R.layout.item_drop_mycar, data) {
	
	
	
	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
		val car: CarInDropDown = getItem(position)
		val childView : View = convertView ?:
		                       LayoutInflater.from(context).inflate(R.layout.item_drop_mycar, null)
		
		childView.findViewById<TextView>(R.id.tvDropCar).text = car.getFullCarTitle()
		childView.findViewById<ImageView>(R.id.ivDropCarBrand).setImageResource(car.brandIcon)
		return childView
	}
	
	
	
	override fun getItem(position: Int): CarInDropDown = data[position]
}