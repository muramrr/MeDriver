/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.09.2020 20:36
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.settings

import android.view.View
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
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
	
	private var notSignedIn = ""

	override fun setupViews() {
		initStringRes()
		
		initThemeSwitcher()
		initMetricSystemCheckable()
		
		observeSignedInUser()
		observeSendVerificationStatus()
		
		
		
		binding.btnSignInPopUp.setDebounceOnClick (2000) {
			SettingsAuthDialog().show(
				childFragmentManager, SettingsAuthDialog::class.java.canonicalName
			)
		}
		
		binding.btnSignOut.setOnClickListener { mViewModel.signOut() }
		
		binding.btnSendVerification.setDebounceOnClick(5000) {
			mViewModel.sendEmailVerification(this.text.toString())
		}
	}
	
	override fun renderState(state: ViewState) {
		super.renderState(state)
		
		when (state) {
			
			is AuthViewState.Success.SendVerification -> {
				binding.root.showSnack(
					getString(R.string.fg_settings_auth_email_sent_success)
				)
			}
			
			is AuthViewState.Error.SendVerification -> {
				binding.root.showSnack(
					state.errorMsg ?:
					getString(R.string.fg_settings_auth_email_sent_error_message),
					Snackbar.LENGTH_LONG
				)
			}
			
		}
	}
	
	private fun initStringRes() {
		notSignedIn = getString(R.string.fg_settings_not_signed_in)
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
			
			binding.tvEmailAddressConfirmed.visibleIf(otherwise = View.INVISIBLE) {
				user != null && user.isEmailVerified
			}
			
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