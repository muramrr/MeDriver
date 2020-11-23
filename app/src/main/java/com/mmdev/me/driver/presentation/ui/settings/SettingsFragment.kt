/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 23.11.2020 20:07
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.google.android.material.snackbar.Snackbar
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.Language
import com.mmdev.me.driver.core.utils.MetricSystem.*
import com.mmdev.me.driver.core.utils.helpers.ThemeHelper.ThemeMode.LIGHT_MODE
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.databinding.FragmentSettingsBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import com.mmdev.me.driver.presentation.ui.common.BaseDropAdapter
import com.mmdev.me.driver.presentation.ui.settings.auth.AuthBottomSheet
import com.mmdev.me.driver.presentation.ui.subscription.SubscriptionBottomSheet
import com.mmdev.me.driver.presentation.utils.extensions.gone
import com.mmdev.me.driver.presentation.utils.extensions.invisible
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
import com.mmdev.me.driver.presentation.utils.extensions.showSnack
import com.mmdev.me.driver.presentation.utils.extensions.visible
import com.mmdev.me.driver.presentation.utils.extensions.visibleIf
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Fragment used to configure app specifications such as theme, account, metric system etc.
 */

class SettingsFragment: BaseFlowFragment<SettingsViewModel, FragmentSettingsBinding>(
	layoutId = R.layout.fragment_settings
) {

	override val mViewModel: SettingsViewModel by viewModel()
	
	private var notSignedIn = ""
	private var accVerified = ""
	private var accNotVerified = ""
	private var emailSent = ""
	private var emailNotSent = ""
	private var languagesArray = emptyArray<String>()
	private var getPremium = ""
	private var premiumObtained = ""
	
	private lateinit var languagesMap: Map<Language, String>

	override fun setupViews() {
		initStringRes()
		
		initSyncSwitcher()
		initThemeSwitcher()
		initMetricSystemCheckable()
		initLanguageChooser()
		
		observeSignedInUser()
		observeSendVerificationStatus()
		
		
		
		binding.apply {
			
			btnSignInPopUp.setDebounceOnClick(2000) {
				AuthBottomSheet().show(childFragmentManager, AuthBottomSheet::class.java.canonicalName)
			}
			
			btnSignOut.setOnClickListener { mViewModel.signOut() }
			
			btnSendVerification.setDebounceOnClick(30000) {
				mViewModel.sendEmailVerification(this.text.toString())
			}
		
			btnGetPremium.setDebounceOnClick {
				SubscriptionBottomSheet().show(childFragmentManager, SubscriptionBottomSheet::class.java .canonicalName)
			}
			
		}
		

	}
	
	private fun initStringRes() {
		notSignedIn = getString(R.string.fg_settings_not_signed_in)
		accVerified = getString(R.string.fg_settings_tv_verified)
		accNotVerified = getString(R.string.fg_settings_tv_not_verified)
		emailSent = getString(R.string.fg_settings_email_confirm_sent_success)
		emailNotSent = getString(R.string.fg_settings_email_confirm_sent_error_message)
		getPremium = getString(R.string.fg_settings_btn_get_subscription_not_active)
		premiumObtained = getString(R.string.fg_settings_btn_get_subscription_active)
		languagesArray = resources.getStringArray(R.array.languages)
		languagesMap = Language.values().zip(languagesArray).toMap()
		
	}
	
	override fun renderState(state: ViewState) {
		when (state) {
			
			is SettingsViewState.Success.SendVerification -> {
				binding.root.showSnack(emailSent)
			}
			
			is SettingsViewState.Error.SendVerification -> {
				binding.root.showSnack(state.errorMsg ?: emailNotSent, Snackbar.LENGTH_LONG)
			}
			
		}
	}
	
	private fun observeSendVerificationStatus() {
		mViewModel.viewState.observe(this, {
			renderState(it)
		})
	}
	
	private fun observeSignedInUser() {
		sharedViewModel.userDataInfo.observe(this, { user ->
			
			logInfo(TAG, "UserData = $user")
			
			binding.apply {
				
				if (user != null) {
					
					// show premium label
					tvYourAccountPremium.visibleIf(otherwise = View.INVISIBLE) { user.isSubscriptionValid() }
					
					// defines visibility of sign in/out buttons
					btnSignOut.setEnabledAndVisible()
					btnSignInPopUp.setDisabledAndInvisible()
					
					tvYourAccountVerificationHint.apply {
						visible()
						text = if (!user.isEmailVerified) accNotVerified else accVerified
					}
					
					// show email confirmed indicator
					tvEmailAddressConfirmed.visibleIf(otherwise = View.INVISIBLE) { user.isEmailVerified }
					tvEmailAddressConfirmed.text = user.email
					
					// show tap to verify hint
					tvTapToVerifyHint.visibleIf(otherwise = View.INVISIBLE) { !user.isEmailVerified }
					
					// if user need to verify his email show related indicator
					btnSendVerification.apply {
						isClickable = true
						text = user.email
						if (user.isEmailVerified) setDisabledAndInvisible()
					}
					
				}
				else {
					// hide premium label
					tvYourAccountPremium.gone()
					
					// defines visibility of sign in/out buttons
					btnSignOut.setDisabledAndInvisible()
					btnSignInPopUp.setEnabledAndVisible()
					
					tvYourAccountVerificationHint.invisible()
					
					// hide email confirmed indicator
					tvEmailAddressConfirmed.apply {
						tvEmailAddressConfirmed.text = notSignedIn
						invisible()
					}
					
					// hide tap to verify hint
					tvTapToVerifyHint.invisible()
					
					btnSendVerification.apply {
						isClickable = false
						text = notSignedIn
						setEnabledAndVisible()
					}
				}
				
				btnGetPremium.isEnabled = user != null  && !user.isSubscriptionValid()
				btnGetPremium.text = if (user != null && user.isSubscriptionValid()) premiumObtained else getPremium
			
				// defines can be accessed synchronization switcher
				switchSync.isEnabled = (user != null && user.isSubscriptionValid()).also { initSyncSwitcher(it) }
				
			}
			
		})
	}
	
	/**
	 * can be accessed only when user is in [AUTHORIZED] status
	 * @see observeSignedInUser
	 */
	private fun initSyncSwitcher(isEnabled: Boolean = false) {
		// remove before changing state, because changing state also invokes onCheckedListener
		if (!isEnabled) binding.switchSync.setSwitcherListener {_, _ -> }
		
		// init default switcher position
		binding.switchSync.setChecked(MedriverApp.currentUser?.isSyncEnabled ?: false)
		
		// add callback to switcher toggle
		if (isEnabled) {
			binding.switchSync.setSwitcherListener { _, isChecked ->
				sharedViewModel.updateUser(MedriverApp.currentUser!!.copy(isSyncEnabled = isChecked))
			}
		}
		
	}
	
	private fun initThemeSwitcher() {
		// init default switcher position
		binding.switchTheme.setChecked(MedriverApp.themeMode != LIGHT_MODE)
		
		// add callback to switcher toggle
		binding.switchTheme.setSwitcherListener { _, isChecked ->
			mViewModel.setThemeMode(isChecked)
		}
	}
	
	private fun initMetricSystemCheckable() {
		// init default checked button
		when (MedriverApp.metricSystem) {
			KILOMETERS -> binding.radioMetricSystem.check(binding.btnSystemKM.id)
			MILES -> binding.radioMetricSystem.check(binding.btnSystemMI.id)
		}
		
		//add callback to check what button is being checked
		binding.radioMetricSystem.addOnButtonCheckedListener { _, checkedId, isChecked ->
			// redundant if (value != field) check because toggling checks this by itself
			when {
				checkedId == binding.btnSystemKM.id && isChecked -> {
					mViewModel.setMetricSystem(KILOMETERS)
				}
				
				checkedId == binding.btnSystemMI.id && isChecked -> {
					mViewModel.setMetricSystem(MILES)
				}
			}
		
		}
	}
	
	private fun initLanguageChooser() {
		val languagesAdapter = LanguageDropAdapter(
			requireContext(), R.layout.item_drop_single_text, languagesArray.toList()
		)
		
		binding.dropLanguage.apply {
			
			setAdapter(languagesAdapter)
			
			setText(languagesMap[MedriverApp.appLanguage], false)
			
			setOnItemClickListener { _, _, position, _ ->
				
				// if different language was chosen
				// apply new language if true and recreate activity to apply changes to resources
				// cant recreate activity from application class, so a bit logic needs here
				with(languagesMap.keys.elementAt(position)) {
					if (MedriverApp.appLanguage != this)
						MedriverApp.changeAppLanguage(this).also { activity?.recreate() }
				}
				
			}
			
		}
		
	}
	
	private fun Button.setDisabledAndInvisible() = apply {
		isEnabled = false
		invisible()
	}
	
	private fun Button.setEnabledAndVisible() = apply {
		isEnabled = true
		visible()
	}
	
	
	
	
	//custom adapter to avoid shitty bugs while recreating activity and catching AutoFocus
	private class LanguageDropAdapter(
		context: Context, @LayoutRes private val layoutId: Int, data: List<String>
	): BaseDropAdapter<String>(context, layoutId, data) {
		
		override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
			val language: String = getItem(position)
			val childView : View = convertView ?:
			                       LayoutInflater.from(context).inflate(layoutId, null)
			
			childView.findViewById<TextView>(R.id.tvDropSingleText).text = language
			return childView
		}
	}
}