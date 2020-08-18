/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 18.08.2020 20:17
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.fuel.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.DialogFragment
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.FragmentFuelHistoryAddBinding
import com.mmdev.me.driver.presentation.utils.hideKeyboard

/**
 * Fullscreen dialog used to add records to Fuel History
 * Hosted by FuelFragmentHistory
 */

class DialogFragmentHistoryAdd: DialogFragment() {
	

	
	private lateinit var binding: FragmentFuelHistoryAddBinding
	
	companion object {
		
		
		fun newInstance() = DialogFragmentHistoryAdd().apply {
			arguments = Bundle().apply {
			
			}
		}
	}
	
	
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(STYLE_NORMAL, R.style.My_Dialog_FullScreen)
		arguments?.let {
		
		}
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View =
		FragmentFuelHistoryAddBinding.inflate(inflater, container, false)
			.apply {
				binding = this
				executePendingBindings()
			}.root
	
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		setupViews()
	}
	
	private fun setupViews() {
		//clear focus + hide keyboard
		binding.root.setOnTouchListener { view, _ ->
			view.performClick()
			view.hideKeyboard(binding.etInputOdometer)
		}
		
		
		binding.etInputOdometer.setOnEditorActionListener{ v, actionId, event ->
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				v.hideKeyboard(v)
				return@setOnEditorActionListener true
			}
			return@setOnEditorActionListener false
		}
		
		
	}
}