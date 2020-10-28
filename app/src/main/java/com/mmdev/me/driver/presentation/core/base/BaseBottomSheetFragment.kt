/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 28.10.2020 15:57
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mmdev.me.driver.BR
import com.mmdev.me.driver.presentation.core.ViewState

/**
 * Base class for [BottomSheetDialogFragment] used in application
 * Uses similar behavior as [BaseFragment]
 */

abstract class BaseBottomSheetFragment<VM: BaseViewModel, Binding: ViewDataBinding>(
	@LayoutRes private val layoutId: Int
): BottomSheetDialogFragment() {
	
	protected val TAG = "mylogs_" + javaClass.simpleName
	
	protected abstract val mViewModel: VM?
	
	private var _binding: Binding? = null
	
	protected val binding: Binding
		get() = _binding ?: throw IllegalStateException(
			"Trying to access the binding outside of the view lifecycle."
		)
	
	
	//automatically dismiss to prevent half expanded state
	protected val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
		
		override fun onStateChanged(bottomSheet: View, newState: Int) {
			if (newState !in arrayOf(
						BottomSheetBehavior.STATE_EXPANDED,
						BottomSheetBehavior.STATE_DRAGGING,
						BottomSheetBehavior.STATE_SETTLING))
				dismiss()
		}
		// Do something for slide offset
		override fun onSlide(bottomSheet: View, slideOffset: Float) {}
	}
	
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
	
	override fun onDestroyView() {
		binding.unbind()
		_binding = null
		super.onDestroyView()
	}
}