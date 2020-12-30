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

package com.mmdev.me.driver.presentation.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import com.mmdev.me.driver.BR
import com.mmdev.me.driver.presentation.core.ViewState
import com.mmdev.me.driver.presentation.utils.extensions.showSnack

/**
 * Base class for [DialogFragment] used in application
 * Uses similar behavior as [BaseFragment]
 */

abstract class BaseDialogFragment<VM: BaseViewModel, Binding: ViewDataBinding>(
	@LayoutRes private val layoutId: Int
): DialogFragment() {
	
	protected val TAG = "mylogs_" + javaClass.simpleName
	
	protected abstract val mViewModel: VM?
	
	private var _binding: Binding? = null
	
	protected val binding: Binding
		get() = _binding ?: throw IllegalStateException(
			"Trying to access the binding outside of the view lifecycle."
		)
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		return DataBindingUtil.inflate<Binding>(inflater, layoutId, container, false)
			.apply {
				lifecycleOwner = viewLifecycleOwner
				setVariable(BR.viewModel, mViewModel)
				_binding = this
			}.root
	}
	
	abstract fun setupViews()
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setupViews()
	}
	
	open fun renderState(state: ViewState) {}
	
	protected fun showInnerSnack(@StringRes messageRes: Int, length: Int = Snackbar.LENGTH_SHORT) =
		binding.root.rootView.showSnack(messageRes, length)
	protected fun showInnerSnack(message: String, length: Int = Snackbar.LENGTH_SHORT) =
		binding.root.rootView.showSnack(message, length)
	
	override fun onDestroyView() {
		binding.unbind()
		_binding = null
		super.onDestroyView()
	}
}