/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.11.2020 18:42
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.settings.auth

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.BottomSheetAuthBinding
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.core.base.BaseBottomSheetFragment
import com.mmdev.me.driver.presentation.ui.settings.auth.AuthViewState.Error
import com.mmdev.me.driver.presentation.ui.settings.auth.AuthViewState.Success
import com.mmdev.me.driver.presentation.utils.extensions.hideKeyboard
import com.mmdev.me.driver.presentation.utils.extensions.setDebounceOnClick
import com.mmdev.me.driver.presentation.utils.extensions.showSnack
import com.mmdev.me.driver.presentation.utils.extensions.visibleIf
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * Fullscreen dialog used to interact with sign in/out/up, password reset actions
 * Hosted by SettingsFragment
 */

class AuthBottomSheet: BaseBottomSheetFragment<AuthViewModel, BottomSheetAuthBinding> (
	layoutId = R.layout.bottom_sheet_auth
) {
	
	//get same scope as SettingsFragment
	override val mViewModel: AuthViewModel by viewModel()
	
	private val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")
	
	private var emailInputError = ""
	private var emailResetSent = ""
	private var emailResetNotSent = ""
	private var passwordInputError = ""
	private var passwordMismatchError = ""
	private var signInError = ""
	private var signUpError = ""
	
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
		//init error strings
		initStringRes()
		
		//observe sign in/out/up, resetPassword states
		observeAuthViewState()
		
		//observe inputs
		observeInputEmail()
		observeInputPassword()
		observeInputPasswordAreSameAsConfirm()
		
		
		binding.run {
			root.setOnTouchListener { rootView, _ ->
				rootView.performClick()
				rootView.hideKeyboard(rootView)
			}
			btnCancel.setDebounceOnClick(300) {
				//transition to start motion state and the next click dismisses dialog
				if (motionAuth.currentState == R.id.signInSet) dismiss()
				else motionAuth.transitionToStart().also { mViewModel.clearPasswordsInput() }
				hideKeyboard(rootView)
			}
			
			btnSignIn.setDebounceOnClick(2000) { mViewModel.signIn() }
			
			btnResetPassword.setDebounceOnClick(2000) {
				mViewModel.resetPassword()
				binding.root.showSnack(getString(R.string.fg_settings_auth_email_sent_success))
			}
			
			btnCreateAccount.setDebounceOnClick(2000) { mViewModel.signUp() }
		}
	
	}
	
	/** Handle dialog interaction emits from [mViewModel] */
	private fun observeAuthViewState() {
		mViewModel.viewState.observe(this, { renderState(it) })
	}
	
	override fun renderState(state: ViewState) {
		binding.viewLoading.visibleIf(otherwise = View.INVISIBLE) { state == AuthViewState.Loading }
		
		when (state) {
			
			// resetPassword states
			is Success.ResetPassword -> {
				binding.btnCancel.performClick()
				binding.root.rootView.showSnack(emailResetSent, Snackbar.LENGTH_LONG)
			}
			is Error.ResetPassword -> binding.root.rootView.showSnack(
				state.errorMsg ?: emailResetNotSent,
				Snackbar.LENGTH_LONG
			)
			
			is Success.SignIn -> dismiss()
			
			// sign in error
			is Error.SignIn -> binding.root.rootView.showSnack(
				state.errorMsg ?: signInError,
				Snackbar.LENGTH_LONG
			)
			// sign up error
			is Error.SignUp -> binding.root.rootView.showSnack(
				state.errorMsg ?: signUpError,
				Snackbar.LENGTH_LONG
			)
			
			is Success.SignUp -> dismiss()
		}
	}
	
	private fun initStringRes() {
		emailInputError = getString(R.string.fg_settings_auth_email_input_error)
		emailResetSent = getString(R.string.fg_settings_auth_email_sent_success)
		emailResetNotSent = getString(R.string.fg_settings_auth_email_sent_error_message)
		passwordInputError = getString(R.string.fg_settings_auth_password_input_error)
		passwordMismatchError = getString(R.string.fg_settings_auth_password_mismatch_error)
		signInError = getString(R.string.fg_settings_auth_sign_in_error_message)
		signUpError = getString(R.string.fg_settings_auth_sign_up_error_message)
	}
	
	/**
	 * Observed value primary triggered at "Sign In" & "Forgot password" states
	 */
	private fun observeInputEmail() {
		mViewModel.inputEmail.observe(this, {
			if (!it.isNullOrBlank() && !it.matches(emailRegex)) {
				binding.layoutInputEmail.error = emailInputError
			}
			else binding.layoutInputEmail.error = null
		})
	}
	
	/**
	 * Observed value primary triggered at "Sign In" state
	 */
	private fun observeInputPassword() {
		mViewModel.inputPassword.observe(this, {
			if (!it.isNullOrBlank() && it.length < 6) {
				binding.layoutInputPassword.error = passwordInputError
			}
			//be sure clear only error which we set in observer
			else if (binding.layoutInputPassword.error == passwordInputError)
				binding.layoutInputPassword.error = null
		})
	}
	
	/**
	 * Observed values primary triggered at "Sign Up" state
	 */
	private fun observeInputPasswordAreSameAsConfirm() {
		mViewModel.inputPasswordAreSameAsConfirm.observe(this, {
			//check if value is not null and password inputs equality is false
			if (it != null && it == false) {
				binding.layoutInputPassword.error = passwordMismatchError
				binding.layoutInputPasswordConfirm.error = passwordMismatchError
			}
			//else (value is null or passwords are same) reset error
			else {
				//be sure clear only error which we set in observer
				if (binding.layoutInputPassword.error == passwordMismatchError)
					binding.layoutInputPassword.error = null
				binding.layoutInputPasswordConfirm.error = null
			}
			
		})
	}
	
}