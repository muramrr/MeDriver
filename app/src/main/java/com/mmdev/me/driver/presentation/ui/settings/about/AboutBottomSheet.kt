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

package com.mmdev.me.driver.presentation.ui.settings.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mmdev.me.driver.BuildConfig
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.BtmSheetAboutBinding
import com.mmdev.me.driver.presentation.core.base.BaseBottomSheetFragment
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
import com.mmdev.me.driver.presentation.utils.extensions.showToast

/**
 * About application screen
 */

class AboutBottomSheet: BaseBottomSheetFragment<Nothing, BtmSheetAboutBinding>(
	layoutId = R.layout.btm_sheet_about
) {
	
	override val mViewModel: Nothing? = null
	
	//attach callback, force dismiss with animation, set state
	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		//dismissWithAnimation = arguments?.getBoolean(ARG_DISMISS_WITH_ANIMATION) ?: true
		(requireDialog() as BottomSheetDialog).apply {
			dismissWithAnimation = true
			behavior.state = BottomSheetBehavior.STATE_EXPANDED
			behavior.addBottomSheetCallback(bottomSheetCallback)
		}
	}
	
	//make bottomSheet fit all screen height
	override fun onStart() {
		super.onStart()
		val sheetContainer = requireView().parent as? ViewGroup ?: return
		sheetContainer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
	}
	
	override fun setupViews() {
		
		val url = "https://github.com/muramrr/MeDriver"
		val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
		
		val emailIntent = Intent(Intent.ACTION_SENDTO)
		emailIntent.data = Uri.parse("mailto:" + getString(R.string.about_email_address))
		val chooserIntent = Intent.createChooser(emailIntent, "")
		
		binding.run {
			var clickCounter = 0
			ivAppLogo.setOnClickListener {
				if (clickCounter == 100) {
					clickCounter = 0
					try {
						startActivity(browserIntent)
					} catch (e: ActivityNotFoundException) {
						it.showToast(getString(R.string.not_found))
					}
				}
				else clickCounter++
			}
			
			tvAppVersion.text = BuildConfig.VERSION_NAME
			
			chipSendEmail.setDebounceOnClick {
				try {
					startActivity(chooserIntent)
				} catch (e: ActivityNotFoundException) {
					showToast(getString(R.string.not_found))
				}
			}
			
			tvPrivacyPolicy.setDebounceOnClick {
				val intent = Intent(
					Intent.ACTION_VIEW,
					Uri.parse("https://sites.google.com/view/medriver-privacypolicy")
				)
				startActivity(intent)
			}
			
			tvTermsOfService.setDebounceOnClick {
				val intent = Intent(
					Intent.ACTION_VIEW,
					Uri.parse("https://sites.google.com/view/medriver-termsconditions")
				)
				startActivity(intent)
			}
		}
	}
	
	
}