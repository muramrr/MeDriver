/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 28.07.20 21:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.custom.components

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.graphics.withTranslation
import com.mmdev.me.driver.R
import com.mmdev.me.driver.presentation.ui.custom.components.switcher.BounceInterpolator
import com.mmdev.me.driver.presentation.utils.lerp

/**
 *
 */

class Switcher @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                         defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

	companion object {
		private const val SWITCHER_ANIMATION_DURATION = 800L
		private const val COLOR_ANIMATION_DURATION = 300L
		private const val TRANSLATE_ANIMATION_DURATION = 200L
		private const val ON_CLICK_RADIUS_OFFSET = 2f
		private const val BOUNCE_ANIM_AMPLITUDE_IN = 0.2
		private const val BOUNCE_ANIM_AMPLITUDE_OUT = 0.15
		private const val BOUNCE_ANIM_FREQUENCY_IN = 14.5
		private const val BOUNCE_ANIM_FREQUENCY_OUT = 12.0

		private const val STATE = "switch_state"
		private const val KEY_CHECKED = "checked"
	}


	private var iconRadius = 0f
	private var iconClipRadius = 0f
	private var iconCollapsedWidth = 0f
	private var defHeight = 0
	private var defWidth = 0
	private var isChecked = true


	@ColorInt
	private var onColor = 0
	@ColorInt
	private var offColor = 0
	@ColorInt
	private var iconColor = 0

	private val switcherPaint = Paint(Paint.ANTI_ALIAS_FLAG)

	private val iconRect = RectF(0f, 0f, 0f, 0f)
	private val iconClipRect = RectF(0f, 0f, 0f, 0f)
	private val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG)

	private val iconClipPaint = Paint(Paint.ANTI_ALIAS_FLAG)

	private var animatorSet: AnimatorSet? = AnimatorSet()

	private var shadowOffset = 0f

	@ColorInt
	private var currentColor = 0
		set(value) {
			field = value
			switcherPaint.color = value
			iconClipPaint.color = value
		}

	private var switchElevation = 0f
	private var iconHeight = 0f

	private var iconProgress = 0f
		set(value) {
			if (field != value) {
				field = value

				val iconOffset = lerp(0f, iconRadius - iconCollapsedWidth / 2, value)
				iconRect.left = width - switcherCornerRadius - iconCollapsedWidth / 2 - iconOffset
				iconRect.right = width - switcherCornerRadius + iconCollapsedWidth / 2 + iconOffset

				val clipOffset = lerp(0f, iconClipRadius, value)
				iconClipRect.set(
						iconRect.centerX() - clipOffset,
						iconRect.centerY() - clipOffset,
						iconRect.centerX() + clipOffset,
						iconRect.centerY() + clipOffset
				)
				postInvalidateOnAnimation()
			}
		}

	private val switcherRect = RectF(0f, 0f, 0f, 0f)
	private var switcherCornerRadius = 0f

	private var iconTranslateX = 0f

	private var onClickOffset = 0f
		set(value) {
			field = value
			switcherRect.left = value + shadowOffset
			switcherRect.top = value + shadowOffset / 2
			switcherRect.right = width.toFloat() - value - shadowOffset
			switcherRect.bottom = height.toFloat() - value - shadowOffset - shadowOffset / 2
			invalidate()
		}




	init {
		attrs?.let { retrieveAttributes(attrs, defStyleAttr) }
		setOnClickListener { setChecked(!isChecked) }
	}

	private fun retrieveAttributes(attrs: AttributeSet, defStyleAttr: Int) {
		val typedArray = context.obtainStyledAttributes(
				attrs,
				R.styleable.Switcher,
				defStyleAttr,
				R.style.Switcher
		)

		switchElevation = typedArray.getDimension(R.styleable.Switcher_elevation, 0f)

		onColor = typedArray.getColor(R.styleable.Switcher_switcher_on_color,
		                              Color.parseColor("#48ea8b"))

		offColor = typedArray.getColor(R.styleable.Switcher_switcher_off_color,
		                               Color.parseColor("#ff4651"))

		iconColor = typedArray.getColor(R.styleable.Switcher_switcher_icon_color, Color.WHITE)

		isChecked = typedArray.getBoolean(R.styleable.Switcher_android_checked, true)

		if (!isChecked) iconProgress = 1f

		currentColor = if (isChecked) onColor else offColor

		iconPaint.color = iconColor

		defHeight = typedArray.getDimensionPixelOffset(R.styleable.Switcher_switcher_height, 0)
		defWidth = typedArray.getDimensionPixelOffset(R.styleable.Switcher_switcher_width, 0)

		typedArray.recycle()

	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		val widthMode = MeasureSpec.getMode(widthMeasureSpec)
		var width = MeasureSpec.getSize(widthMeasureSpec)
		val heightMode = MeasureSpec.getMode(heightMeasureSpec)
		var height = MeasureSpec.getSize(heightMeasureSpec)

		if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY) {
			width = defWidth
			height = defHeight
		}

		setMeasuredDimension(width, height)
	}

	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

		shadowOffset = switchElevation
		iconTranslateX = -shadowOffset
		switcherRect.left = shadowOffset
		switcherRect.top = shadowOffset / 2
		switcherRect.right = width.toFloat() - shadowOffset
		switcherRect.bottom = height.toFloat() - shadowOffset - shadowOffset / 2

		switcherCornerRadius = (height - shadowOffset * 2) / 2f

		iconRadius = switcherCornerRadius * 0.6f
		iconClipRadius = iconRadius / 2.25f
		iconCollapsedWidth = iconRadius - iconClipRadius

		iconHeight = iconRadius * 2f

		iconRect.set(
				width - switcherCornerRadius - iconCollapsedWidth / 2,
				((height - iconHeight) / 2f) - shadowOffset / 2,
				width - switcherCornerRadius + iconCollapsedWidth / 2,
				(height - (height - iconHeight) / 2f) - shadowOffset / 2
		)

		if (!isChecked) {
			iconRect.left =
				width - switcherCornerRadius - iconCollapsedWidth / 2 - (iconRadius - iconCollapsedWidth / 2)
			iconRect.right =
				width - switcherCornerRadius + iconCollapsedWidth / 2 + (iconRadius - iconCollapsedWidth / 2)

			iconClipRect.set(
					iconRect.centerX() - iconClipRadius,
					iconRect.centerY() - iconClipRadius,
					iconRect.centerX() + iconClipRadius,
					iconRect.centerY() + iconClipRadius
			)

			iconTranslateX = -(width - shadowOffset - switcherCornerRadius * 2)
		}

	}

	override fun onDraw(canvas: Canvas?) {
		// switcher
		canvas?.drawRoundRect(switcherRect, switcherCornerRadius, switcherCornerRadius, switcherPaint)

		// icon
		canvas?.withTranslation(x = iconTranslateX) {
			drawRoundRect(iconRect, switcherCornerRadius, switcherCornerRadius, iconPaint)
			/* don't draw clip path if icon is collapsed (to prevent drawing small circle
			on rounded rect when switch is isChecked)*/
			if (iconClipRect.width() > iconCollapsedWidth)
				drawRoundRect(iconClipRect, iconRadius, iconRadius, iconClipPaint)
		}
	}

	private fun animateSwitch() {
		animatorSet?.cancel()
		animatorSet = AnimatorSet()

		onClickOffset = ON_CLICK_RADIUS_OFFSET

		var amplitude = BOUNCE_ANIM_AMPLITUDE_IN
		var frequency = BOUNCE_ANIM_FREQUENCY_IN
		var iconTranslateA = 0f
		var iconTranslateB = -(width - shadowOffset - switcherCornerRadius * 2)
		var newProgress = 1f

		if (isChecked) {
			amplitude = BOUNCE_ANIM_AMPLITUDE_OUT
			frequency = BOUNCE_ANIM_FREQUENCY_OUT
			iconTranslateA = iconTranslateB
			iconTranslateB = -shadowOffset
			newProgress = 0f
		}

		val switcherAnimator = ValueAnimator.ofFloat(iconProgress, newProgress).apply {
			addUpdateListener { iconProgress = it.animatedValue as Float }
			interpolator = BounceInterpolator(amplitude, frequency)
			duration = SWITCHER_ANIMATION_DURATION
		}

		val translateAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
			addUpdateListener {
				val value = it.animatedValue as Float
				iconTranslateX = lerp(iconTranslateA, iconTranslateB, value)
			}
			doOnEnd { onClickOffset = 0f }
			duration = TRANSLATE_ANIMATION_DURATION
		}

		val toColor = if (isChecked) onColor else offColor

		iconClipPaint.color = toColor

		val colorAnimator = ValueAnimator().apply {
			addUpdateListener { currentColor = it.animatedValue as Int }
			setIntValues(currentColor, toColor)
			setEvaluator(ArgbEvaluator())
			duration = COLOR_ANIMATION_DURATION
		}

		animatorSet?.apply {
			doOnStart { listener?.invoke(isChecked) }
			playTogether(switcherAnimator, translateAnimator, colorAnimator)
			start()
		}
	}

	private var listener: ((isChecked: Boolean) -> Unit)? = null

	/**
	 * Register a callback to be invoked when the isChecked state of this switch
	 * changes.
	 *
	 * @param listener the callback to call on isChecked state change
	 */
	fun setOnCheckedChangeListener(listener: (isChecked: Boolean) -> Unit) {
		this.listener = listener
	}

	/**
	 * <p>Changes the isChecked state of this switch.</p>
	 *
	 * @param checked true to check the switch, false to uncheck it
	 * @param withAnimation use animation
	 */
	private fun setChecked(checked: Boolean, withAnimation: Boolean = true) {
		if (this.isChecked != checked) {
			this.isChecked = checked
			if (withAnimation && width != 0) {
				animateSwitch()
			} else {
				animatorSet?.cancel()
				if (!checked) {
					currentColor = offColor
					iconProgress = 1f
					iconTranslateX = -(width - shadowOffset - switcherCornerRadius * 2)
				} else {
					currentColor = onColor
					iconProgress = 0f
					iconTranslateX = -shadowOffset
				}
				listener?.invoke(isChecked)
			}
		}
	}

	override fun onSaveInstanceState(): Parcelable {
		super.onSaveInstanceState()
		return Bundle().apply {
			putBoolean(KEY_CHECKED, isChecked)
			putParcelable(STATE, super.onSaveInstanceState())
		}
	}

	override fun onRestoreInstanceState(state: Parcelable?) {
		if (state is Bundle) {
			super.onRestoreInstanceState(state.getParcelable(STATE))
			isChecked = state.getBoolean(KEY_CHECKED)
			if (!isChecked) forceUncheck()
		}
	}

	private fun forceUncheck() {
		currentColor = offColor
		iconProgress = 1f
		iconTranslateX = -(width - shadowOffset - switcherCornerRadius * 2)
	}

}