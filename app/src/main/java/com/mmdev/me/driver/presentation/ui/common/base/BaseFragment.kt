/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 29.07.20 21:11
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

/**
 * This is the documentation block about the class
 */


abstract class BaseFragment<VM: ViewModel, Binding: ViewDataBinding>(
	val isViewModelActivityHosted: Boolean = false,
	private val layoutId: Int
) : Fragment() {

	protected lateinit var navController: NavController
	protected val TAG = "mylogs_" + javaClass.simpleName

	//protected val sharedViewModel: SharedViewModel by viewModels()


	protected abstract val viewModel: VM

	protected lateinit var binding: Binding
		private set


	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		return DataBindingUtil.inflate<Binding>(inflater, layoutId, container, false)
			.apply {
				lifecycleOwner = viewLifecycleOwner
				//setVariable(BR.viewModel, viewModel)
				binding = this
			}.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setupViews()
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		navController = findNavController()
		//(associatedViewModel as BaseViewModel).showErrorDialog(this, context)
	}

	abstract fun setupViews()

//	protected inline fun <reified VM : ViewModel> getViewModel(): VM =
//		if (isViewModelActivityHosted) {
//			activity?.run {
//				ViewModelProvider(this, factory)[VM::class.java]
//			} ?: throw Exception("Invalid Activity")
//		}
//		else ViewModelProvider(this, factory)[VM::class.java]

	//get actual class from parameterized <T>
	//CAUTION: REFLECTION USED
	//use at own risk
//	private fun getTClass(): Class<T> =
//		(javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>



}