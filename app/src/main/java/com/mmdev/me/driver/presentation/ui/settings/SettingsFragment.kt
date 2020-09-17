/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 17.09.2020 21:05
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.settings

import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.Language
import com.mmdev.me.driver.core.utils.MetricSystem.KILOMETERS
import com.mmdev.me.driver.core.utils.MetricSystem.MILES
import com.mmdev.me.driver.core.utils.logInfo
import com.mmdev.me.driver.databinding.FragmentSettingsBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import com.mmdev.me.driver.presentation.utils.invisible
import com.mmdev.me.driver.presentation.utils.setDebounceOnClick
import com.mmdev.me.driver.presentation.utils.showSnack
import com.mmdev.me.driver.presentation.utils.visible
import com.mmdev.me.driver.presentation.utils.visibleIf
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Fragment used to configure app specifications such as theme, account, metric system etc.
 */

internal class SettingsFragment: BaseFlowFragment<SettingsViewModel, FragmentSettingsBinding>(
	layoutId = R.layout.fragment_settings
) {

	override val mViewModel: SettingsViewModel by viewModel()
	
	private val authDialog = SettingsAuthDialog()
	
	private var notSignedIn = ""
	private var emailSent = ""
	private var emailNotSent = ""
	private var languagesArray = emptyArray<String>()
	
	private lateinit var languagesMap: Map<Language, String>

	override fun setupViews() {
		initStringRes()
		
		initThemeSwitcher()
		initMetricSystemCheckable()
		initLanguageChooser()
		
		observeSignedInUser()
		observeSendVerificationStatus()
		
		val languagesAdapter = ArrayAdapter(
			requireContext(),
			android.R.layout.simple_dropdown_item_1line,
			languagesArray
		)
		
		binding.apply {
			btnSignInPopUp.setDebounceOnClick(2000) {
				authDialog.show(childFragmentManager, SettingsAuthDialog::class.java.canonicalName)
			}
			
			btnSignOut.setOnClickListener { mViewModel.signOut() }
			
			btnSendVerification.setDebounceOnClick(30000) {
				mViewModel.sendEmailVerification(this.text.toString())
			}
			
			dropLanguage.setAdapter(languagesAdapter)
			
			
		}

	}
	
	private fun initStringRes() {
		notSignedIn = getString(R.string.fg_settings_not_signed_in)
		emailSent = getString(R.string.fg_settings_email_confirm_sent_success)
		emailNotSent = getString(R.string.fg_settings_email_confirm_sent_error_message)
		languagesArray = resources.getStringArray(R.array.languages)
		languagesMap = Language.values().zip(languagesArray).toMap()
	}
	
	override fun renderState(state: ViewState) {
		super.renderState(state)
		
		when (state) {
			
			is AuthViewState.Success.SendVerification -> {
				binding.root.showSnack(emailSent)
			}
			
			is AuthViewState.Error.SendVerification -> {
				binding.root.showSnack(state.errorMsg ?: emailNotSent, Snackbar.LENGTH_LONG)
			}
			
			// sign in success
			is AuthViewState.Success.SignIn -> {
				authDialog.dismiss()
				mViewModel.clearInput()
			}
		
			// sign up success
			is AuthViewState.Success.SignUp -> {
				authDialog.dismiss()
				mViewModel.clearInput()
			}
			
		}
	}
	
	private fun observeSendVerificationStatus() {
		mViewModel.authViewState.observe(this, {
			renderState(it)
		})
	}
	
	private fun observeSignedInUser() {
		sharedViewModel.userModel.observe(this, { user ->
			
			logInfo(TAG, "UserModel = $user")
			
			binding.btnSignOut.setInvisibleAndDisabledIf { user == null }
			binding.btnSignInPopUp.setInvisibleAndDisabledIf { user != null }
			
			binding.tvYourAccountIsNotVerifiedHint.visibleIf(otherwise = View.INVISIBLE) {
				user != null && !user.isEmailVerified
			}
			binding.tvTapToVerifyHint.visibleIf(otherwise = View.INVISIBLE) {
				user != null && !user.isEmailVerified
			}
			
			binding.tvEmailAddressConfirmed.visibleIf(otherwise = View.INVISIBLE) {
				user != null && user.isEmailVerified
			}
			
			binding.btnSendVerification.isClickable = user != null
			
			binding.btnSendVerification.setInvisibleAndDisabledIf {
				user != null && user.isEmailVerified
			}
			
			
			binding.btnYourAccountPremium.visibleIf(otherwise = View.GONE) {
				user != null && user.isPremium
			}
			
			
			binding.btnSendVerification.text = user?.email ?: notSignedIn
			binding.tvEmailAddressConfirmed.text = user?.email ?: notSignedIn
			
		})
	}
	
	private fun initThemeSwitcher() {
		//init default switcher position
		binding.switchTheme.isChecked(!MedriverApp.isLightMode)
		
		//add callback to switcher toggle
		binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
			mViewModel.setThemeMode(isChecked)
		}
	}
	
	private fun initMetricSystemCheckable() {
		//init default checked button
		when (MedriverApp.metricSystem) {
			KILOMETERS -> binding.radioMetricSystem.check(binding.btnSystemKM.id)
			MILES -> binding.radioMetricSystem.check(binding.btnSystemMI.id)
		}
		
		//add callback to check what button is being checked
		binding.radioMetricSystem.addOnButtonCheckedListener { _, checkedId, isChecked ->
			
			when {
				checkedId == binding.btnSystemKM.id && isChecked -> mViewModel.setMetricSystem(
					KILOMETERS
				)
				checkedId == binding.btnSystemMI.id && isChecked-> mViewModel.setMetricSystem(MILES)
			}
		
		}
	}
	
	private fun initLanguageChooser() {
		binding.dropLanguage.setText(languagesMap[MedriverApp.appLanguage], false)
		
		binding.dropLanguage.setOnItemClickListener { _, _, position, _ ->
			
			binding.dropLanguage.setText(
				languagesMap[languagesMap.keys.elementAt(position)], false
			)
			
			MedriverApp.changeLanguage(languagesMap.keys.elementAt(position))
		}
		
	}
	
	
	//makes button invisible and non-clickable if condition() is true
	//else make button visible and clickable
	private fun Button.setInvisibleAndDisabledIf(condition: () -> Boolean) =
		if (condition())
			apply {
				isEnabled = false
				invisible()
			}
		else apply {
			isEnabled = true
			visible()
		}
}