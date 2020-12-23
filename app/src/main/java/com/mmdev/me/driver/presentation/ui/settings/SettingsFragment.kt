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

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.Language
import com.mmdev.me.driver.core.utils.MetricSystem.*
import com.mmdev.me.driver.core.utils.extensions.convertToLocalDateTime
import com.mmdev.me.driver.core.utils.extensions.currentEpochTime
import com.mmdev.me.driver.core.utils.helpers.DateHelper
import com.mmdev.me.driver.core.utils.helpers.ThemeHelper.ThemeMode.LIGHT_MODE
import com.mmdev.me.driver.databinding.FragmentSettingsBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import com.mmdev.me.driver.presentation.ui.common.BaseDropAdapter
import com.mmdev.me.driver.presentation.ui.settings.about.AboutBottomSheet
import com.mmdev.me.driver.presentation.ui.settings.auth.AuthBottomSheet
import com.mmdev.me.driver.presentation.ui.settings.faq.FaqBottomSheet
import com.mmdev.me.driver.presentation.ui.settings.subscription.SubscriptionBottomSheet
import com.mmdev.me.driver.presentation.utils.extensions.domain.dateMonthText
import com.mmdev.me.driver.presentation.utils.extensions.domain.humanDate
import com.mmdev.me.driver.presentation.utils.extensions.domain.humanDay
import com.mmdev.me.driver.presentation.utils.extensions.gone
import com.mmdev.me.driver.presentation.utils.extensions.invisible
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
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
	private var syncedNever = ""
	private var syncedJustNow = ""
	private var syncedFormatter = ""
	
	private lateinit var languagesMap: Map<Language, String>
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel.viewState.observe(this, { renderState(it) })
	}
	
	override fun renderState(state: ViewState) {
		when (state) {
			is SettingsViewState.SendVerification.Success -> showActivitySnack(emailSent)
			is SettingsViewState.SendVerification.Error -> showActivitySnack(state.errorMsg ?: emailNotSent)
		}
	}
	
	
	
	override fun setupViews() {
		initStringRes()
		
		initLastTimeSynced()
		initThemeSwitcher()
		initMetricSystemCheckable()
		initLanguageChooser()
		
		observeSignedInUser()
		
		
		
		binding.apply {
			
			btnSignInPopUp.setDebounceOnClick(2000) {
				AuthBottomSheet().show(childFragmentManager, AuthBottomSheet::class.java.canonicalName)
			}
			
			btnSignOut.setOnClickListener { mViewModel.signOut() }
			
			btnSendVerification.setDebounceOnClick(30000) {
				mViewModel.sendEmailVerification(this.text.toString())
			}
		
			btnMoreFeatures.setDebounceOnClick {
				SubscriptionBottomSheet().show(childFragmentManager, SubscriptionBottomSheet::class.java.canonicalName)
			}
			
			cvFAQ.setDebounceOnClick {
				FaqBottomSheet().show(childFragmentManager, FaqBottomSheet::class.java.canonicalName)
			}
			
			cvAbout.setDebounceOnClick {
				AboutBottomSheet().show(childFragmentManager, AboutBottomSheet::class.java.canonicalName)
			}
		}
		

	}
	
	private fun initStringRes() {
		notSignedIn = getString(R.string.fg_settings_not_signed_in)
		accVerified = getString(R.string.fg_settings_tv_verified)
		accNotVerified = getString(R.string.fg_settings_tv_not_verified)
		emailSent = getString(R.string.fg_settings_email_confirm_sent_success)
		emailNotSent = getString(R.string.fg_settings_email_confirm_sent_error_message)
		syncedNever = getString(R.string.fg_settings_sync_subtitle_never)
		syncedJustNow = getString(R.string.fg_settings_sync_subtitle_just_now)
		syncedFormatter = getString(R.string.fg_settings_sync_subtitle_formatter)
		languagesArray = resources.getStringArray(R.array.languages)
		languagesMap = Language.values().zip(languagesArray).toMap()
		
	}
	
	private fun initLastTimeSynced() {
		binding.tvSyncSubtitle.text = with(MedriverApp.lastSyncedDate) {
			when {
				this <= 0L -> syncedNever
				(currentEpochTime() - this) < DateHelper.HOUR_DURATION -> syncedJustNow
				else -> syncedFormatter.format(convertToLocalDateTime(this).date.humanDate())
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
						MedriverApp.appLanguage = this.also { activity?.recreate() }
				}
				
			}
			
		}
		
	}
	
	private fun observeSignedInUser() {
		sharedViewModel.userDataInfo.observe(this, { user ->
			
			binding.apply {
				
				if (user != null) {
					
					setUserIsNotNull()
					tvYourAccountVerificationHint.apply {
						visible()
						text = if (!user.isEmailVerified) accNotVerified else accVerified
					}
					// show email confirmed indicator
					tvEmailAddressConfirmed.visibleIf(
						otherwise = View.INVISIBLE, 0
					) { user.isEmailVerified }
					tvEmailAddressConfirmed.text = user.email
					
					// show tap to verify hint
					tvTapToVerifyHint.visibleIf(
						otherwise = View.INVISIBLE, 0
					) { !user.isEmailVerified }
					
					// if user need to verify his email show related indicator
					btnSendVerification.apply {
						isClickable = true
						text = user.email
						if (user.isEmailVerified) setDisabledAndInvisible()
					}
					when {
						user.isPremium() -> {
							val date = user.subscription.expires!!
							val day = humanDay(date.dayOfMonth)
							val month = date.monthNumber.dateMonthText().take(3)
							tvSubscriptionExpiresValue.text = "$day $month ${date.year}"
							setUserIsPremium()
						}
						user.isPro() -> {
							val date = user.subscription.expires!!
							val day = humanDay(date.dayOfMonth)
							val month = date.monthNumber.dateMonthText().take(3)
							tvSubscriptionExpiresValue.text = "$day $month ${date.year}"
							setUserIsPro()
						}
						else -> setUserIsNotSubscribed()
					}
				}
				else setUserIsNull()
				
				//allow to get premium only when user verifies email
				btnMoreFeatures.isEnabled = user != null && user.isEmailVerified
				
			}
			
		})
	}
	
	private fun setUserIsNotNull() {
		binding.btnSignOut.setEnabledAndVisible()
		binding.btnSignInPopUp.setDisabledAndInvisible()
	}
	
	private fun setUserIsPremium() {
		binding.tvSubscriptionObtainedPremium.visible(0)
		binding.tvSubscriptionObtainedPro.gone(0)
	}
	
	private fun setUserIsPro() {
		binding.tvSubscriptionObtainedPremium.gone(0)
		binding.tvSubscriptionObtainedPro.visible(0)
	}
	
	private fun setUserIsNotSubscribed() {
		binding.tvSubscriptionExpiresValue.text = getString(R.string.fg_settings_tv_subscription_expires_value)
		binding.tvSubscriptionObtainedPremium.gone(0)
		binding.tvSubscriptionObtainedPro.gone(0)
	}
	
	private fun setUserIsNull() {
		binding.apply {
			// hide premium label
			setUserIsNotSubscribed()
			
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
	}
	
	private fun Button.setDisabledAndInvisible() = apply {
		isEnabled = false
		invisible(0)
	}
	
	private fun Button.setEnabledAndVisible() = apply {
		isEnabled = true
		visible(0)
	}
	
	
	
	
	//custom adapter to avoid shitty bugs while recreating activity and catching AutoFocus
	private class LanguageDropAdapter(
		context: Context,
		@LayoutRes private val layoutId: Int,
		data: List<String>
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