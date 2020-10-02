/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 02.10.2020 18:32
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
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.Language
import com.mmdev.me.driver.core.utils.MetricSystem.KILOMETERS
import com.mmdev.me.driver.core.utils.MetricSystem.MILES
import com.mmdev.me.driver.core.utils.helpers.ThemeHelper.ThemeMode.LIGHT_MODE
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.databinding.FragmentSettingsBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseFlowFragment
import com.mmdev.me.driver.presentation.ui.common.BaseDropAdapter
import com.mmdev.me.driver.presentation.utils.invisible
import com.mmdev.me.driver.presentation.utils.setDebounceOnClick
import com.mmdev.me.driver.presentation.utils.showSnack
import com.mmdev.me.driver.presentation.utils.visible
import com.mmdev.me.driver.presentation.utils.visibleIf
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Fragment used to configure app specifications such as theme, account, metric system etc.
 */

class SettingsFragment: BaseFlowFragment<SettingsViewModel, FragmentSettingsBinding>(
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
		
		initSyncSwitcher()
		initThemeSwitcher()
		initMetricSystemCheckable()
		initLanguageChooser()
		
		observeSignedInUser()
		observeSendVerificationStatus()
		
		
		
		binding.apply {
			btnSignInPopUp.setDebounceOnClick(2000) {
				
				// DialogFragment.show() will take care of adding the fragment
				// in a transaction.  We also want to remove any currently showing
				// dialog, so make our own transaction and take care of that here.
				val ft: FragmentTransaction = childFragmentManager.beginTransaction().apply {
					val prev = childFragmentManager.findFragmentByTag(
						SettingsAuthDialog::class.java.canonicalName
					)
					prev?.let { remove(it) }
					addToBackStack(null)
				}
				
				authDialog.show(ft, SettingsAuthDialog::class.java.canonicalName)
			}
			
			btnSignOut.setOnClickListener { mViewModel.signOut() }
			
			btnSendVerification.setDebounceOnClick(30000) {
				mViewModel.sendEmailVerification(this.text.toString())
			}
			
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
			is AuthViewState.Success.SignIn -> { closeAuthDialog() }
		
			// sign up success
			is AuthViewState.Success.SignUp -> { closeAuthDialog() }
			
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
			
			binding.apply {
				
				// defines visibility of sign in/out buttons
				btnSignOut.setDisabledIfConditionElseViceVersa { user == null }
				btnSignInPopUp.setDisabledIfConditionElseViceVersa { user != null }
				
				// show premium label
				tvYourAccountPremium.visibleIf(otherwise = View.GONE) {
					user != null && user.isPremium
				}
				
				// if user need to verify his email show related indicator
				btnSendVerification.isClickable = user != null
				btnSendVerification.setDisabledIfConditionElseViceVersa {
					user != null && user.isEmailVerified
				}
				btnSendVerification.text = user?.email ?: notSignedIn
				
				// show email not verified hint
				tvYourAccountIsNotVerifiedHint.visibleIf(otherwise = View.INVISIBLE) {
					user != null && !user.isEmailVerified
				}
				
				// enable tap to verify hint
				tvTapToVerifyHint.visibleIf(otherwise = View.INVISIBLE) {
					user != null && !user.isEmailVerified
				}
				
				// control email confirmed indicator visibility (is non-clickable by default)
				tvEmailAddressConfirmed.visibleIf(otherwise = View.INVISIBLE) {
					user != null && user.isEmailVerified
				}
				
				// control email confirmed text
				tvEmailAddressConfirmed.text = user?.email ?: notSignedIn
				
				btnGetPremium.isEnabled = user != null && user.isEmailVerified && !user.isPremium
			
				// defines can be accessed synchronization switcher
				switchSync.isEnabled = (user != null && user.isPremium).also { initSyncSwitcher(it) }
				
			}
			
		})
	}
	
	/**
	 * can be accessed only when user is in [AUTHORIZED] status
	 * @see observeSignedInUser
	 */
	private fun initSyncSwitcher(isEnabled: Boolean = false) {
		// remove before changing state, because changing state also invokes onCheckedListener
		if (!isEnabled) binding.switchSync.removeOnCheckedChangeListener()
		
		// init default switcher position
		binding.switchSync.isChecked(MedriverApp.currentUser?.isSyncEnabled ?: false)
		
		// add callback to switcher toggle
		if (isEnabled) {
			binding.switchSync.setOnCheckedChangeListener { _, isChecked ->
				sharedViewModel.updateUser(MedriverApp.currentUser!!.copy(isSyncEnabled = isChecked))
			}
		}
		
	}
	
	private fun initThemeSwitcher() {
		// init default switcher position
		binding.switchTheme.isChecked(MedriverApp.themeMode != LIGHT_MODE)
		
		// add callback to switcher toggle
		binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
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
			requireContext(), R.layout.drop_item_single_text, languagesArray.toList()
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
	
	private fun closeAuthDialog() {
		if (childFragmentManager.findFragmentByTag(
					SettingsAuthDialog::class.java.canonicalName
				) != null) {
			childFragmentManager.beginTransaction().remove(authDialog).commitAllowingStateLoss()
			
		}
		
	}
	
	/**
	 * makes button [invisible] and disabled if [condition] == true
	 * else make button [visible] and enabled
	 */
	private inline fun Button.setDisabledIfConditionElseViceVersa(condition: () -> Boolean) =
		if (condition())
			apply {
				isEnabled = false
				invisible()
			}
		else apply {
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