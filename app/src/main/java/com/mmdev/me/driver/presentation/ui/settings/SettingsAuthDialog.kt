/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.09.2020 04:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.FragmentSettingsAuthBinding
import com.mmdev.me.driver.presentation.utils.hideKeyboard
import com.mmdev.me.driver.presentation.utils.setDebounceOnClick
import com.mmdev.me.driver.presentation.utils.showSnack
import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Fullscreen dialog used to interact with sign in/out/up, password reset actions
 * Hosted by SettingsFragment
 */

class SettingsAuthDialog: DialogFragment() {
	
	private val TAG = javaClass.simpleName
	
	private lateinit var binding: FragmentSettingsAuthBinding
	
	
	//get same scope as SettingsFragment
	private val mViewModel: SettingsViewModel by lazy { requireParentFragment().getViewModel() }
	
	private val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")
	
	private var emailInputError = ""
	private var emailResetSent = ""
	private var emailResetNotSent = ""
	private var passwordInputError = ""
	private var passwordMismatchError = ""
	private var signInError = ""
	private var signUpError = ""
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(STYLE_NORMAL, R.style.My_Dialog_FullScreen)
	}
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View =
		FragmentSettingsAuthBinding.inflate(inflater, container, false)
			.apply {
				binding = this
				lifecycleOwner = viewLifecycleOwner
				viewModel = mViewModel
				executePendingBindings()
			}.root
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) { setupViews() }
	
	//override default fade in/out appearance animation
	override fun onStart() {
		super.onStart()
		dialog?.let {
			it.window?.setWindowAnimations(R.style.AppTheme_AnimSlideFromBottomToBottom)
		}
	}
	
	private fun setupViews() {
		//init error strings
		initStringRes()
		
		//observe inputs
		observeInputEmail()
		observeInputPassword()
		observeInputPasswordAreSameAsConfirm()
		
		//observe sign in/out/up, resetPassword states
		observeAuthViewState()
		
		binding.run {
			root.setOnTouchListener { rootView, _ ->
				rootView.performClick()
				rootView.hideKeyboard(rootView)
			}
			btnCancel.setDebounceOnClick(300) {
				//transition to start motion state and the next click dismisses dialog
				if (motionContainer.currentState == R.id.signInSet) dismiss()
				else motionContainer.transitionToStart().also { mViewModel.clearPasswordsInput() }
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
	
	
	/** Handle dialog interaction emits from [mViewModel] */
	private fun observeAuthViewState() {
		mViewModel.authViewState.observe(this, {
			renderAuthViewState(it)
		})
	}
	private fun renderAuthViewState(state: AuthViewState) {
		when (state) {
			
			// resetPassword states
			is AuthViewState.Success.ResetPassword -> {
				binding.btnCancel.performClick()
				binding.root.showSnack(emailResetSent, Snackbar.LENGTH_LONG)
			}
			is AuthViewState.Error.ResetPassword -> binding.root.showSnack(
				state.errorMsg ?: emailResetNotSent,
				Snackbar.LENGTH_LONG
			)
			
			// sign in error
			is AuthViewState.Error.SignIn -> binding.root.showSnack(
				state.errorMsg ?: signInError,
				Snackbar.LENGTH_LONG
			)
			// sign up error
			is AuthViewState.Error.SignUp -> binding.root.showSnack(
				state.errorMsg ?: signUpError,
				Snackbar.LENGTH_LONG
			)
		}
	}
	
	
}