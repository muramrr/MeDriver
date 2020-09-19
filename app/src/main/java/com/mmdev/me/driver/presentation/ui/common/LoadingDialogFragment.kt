/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.09.2020 18:27
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.common

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.mmdev.me.driver.R

/**
 *
 */

class LoadingDialogFragment : DialogFragment() {
	
	override fun onStart() {
		super.onStart()
		dialog?.apply {
			setCancelable(false)
			window?.apply {
				setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
				setDimAmount(0.9f)
			}
		}
	}
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View = inflater.inflate(R.layout.dialog_loading, container, false)
}