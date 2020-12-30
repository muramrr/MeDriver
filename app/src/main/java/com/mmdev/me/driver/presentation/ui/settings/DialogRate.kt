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

package com.mmdev.me.driver.presentation.ui.settings

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.RatingBar
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mmdev.me.driver.R
import com.mmdev.me.driver.presentation.utils.extensions.showSnack

/**
 *
 */

/**
 * Dialog used for rating application
 */

class DialogRate: DialogFragment() {
	
	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
		MaterialAlertDialogBuilder(requireContext(), R.style.My_MaterialAlertDialog)
			.setView(R.layout.dialog_rate)
			.setTitle(R.string.fg_settings_dialog_rate_title)
			.setMessage(R.string.fg_settings_dialog_rate_message)
			.setPositiveButton(R.string.fg_settings_dialog_rate_btn_positive) { dialog, button ->
				
				if (requireDialog().findViewById<RatingBar>(R.id.ratingBar).rating > 3) {
					var link = "market://details?id="
					try {
						// play market available
						requireContext().packageManager.getPackageInfo("com.android.vending", 0)
						// not available
					} catch (e: PackageManager.NameNotFoundException) {
						e.printStackTrace()
						// should use browser
						link = "https://play.google.com/store/apps/details?id="
					}
					
					// starts external action
					requireContext().startActivity(
						Intent(
							Intent.ACTION_VIEW,
							Uri.parse(link + requireContext().packageName)
						)
					)
				}
				else requireParentFragment()
					.requireView()
					.showSnack(R.string.fg_settings_dialog_rate_snack_text)
				
			}
			.setNegativeButton(R.string.fg_settings_dialog_rate_btn_negative, null)
			//.setNeutralButton(R.string.fg_settings_dialog_rate_btn_neutral, null)
			.create()
	
}