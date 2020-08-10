package com.mmdev.me.driver.presentation.core.base

import androidx.activity.addCallback
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.navigation.NavDirections
import com.mmdev.me.driver.presentation.utils.popBackStackAllInstances

/**
 * This is the documentation block about the class
 */

abstract class BaseFlowFragment<VM: BaseViewModel, Binding: ViewDataBinding>(
	@LayoutRes layoutId: Int
) :
	BaseFragment<VM, Binding>(layoutId) {

	/**
	 * [isNavigated] used to check if it's trying to navigate in same tab we don't need to addCallback
	 */
	private var isNavigated = false

	protected fun navigateWithAction(action: NavDirections) {
		isNavigated = true
		navController.navigate(action)
	}

	protected fun navigate(resId: Int) {
		isNavigated = true
		navController.navigate(resId)
	}


	override fun onDestroyView() {
		super.onDestroyView()
		if (!isNavigated)
			requireActivity().onBackPressedDispatcher.addCallback(this) {
				if (navController.currentBackStackEntry?.destination?.id != null) {
					navController.popBackStackAllInstances(
						navController.currentBackStackEntry?.destination?.id!!,
						true
					)
				}
				else navController.popBackStack()
			}
	}
}