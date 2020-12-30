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

package com.mmdev.me.driver.presentation.ui.common.custom.components

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
import android.view.animation.Interpolator
import androidx.annotation.ColorInt
import androidx.core.animation.doOnEnd
import androidx.core.graphics.withTranslation
import com.mmdev.me.driver.R
import kotlin.math.cos
import kotlin.math.pow

/**
 *
 */

class Switcher @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

	companion object {
		private const val SWITCHER_ANIMATION_DURATION = 600L
		private const val COLOR_ANIMATION_DURATION = 300L
		private const val TRANSLATE_ANIMATION_DURATION = 200L
		private const val ON_CLICK_RADIUS_OFFSET = 2f
		private const val BOUNCE_ANIM_AMPLITUDE_IN = 0.3
		private const val BOUNCE_ANIM_AMPLITUDE_OUT = 0.2
		private const val BOUNCE_ANIM_FREQUENCY_IN = 16.5
		private const val BOUNCE_ANIM_FREQUENCY_OUT = 15.0

		private const val STATE = "switch_state"
		private const val KEY_CHECKED = "checked"
	}


	private var iconRadius = 0f
	private var iconClipRadius = 0f
	private var iconCollapsedWidth = 0f
	private var defHeight = 0
	private var defWidth = 0
	private var mChecked = false
	// apply disabled colors and move switch to unchecked state
	private var mEnabled = true
		private set(value) {
			field = value
			
			if (value) {
				// if enabled
				currentColor = if (mChecked) onColor else offColor
				iconPaint.color = iconColor
			}
			else {
				if (mChecked) setSwitchUnchecked()
				currentColor = disabledBackgroundColor
				iconPaint.color = disabledIconColor
			}
			mChecked = false
			invalidate()
		}


	@ColorInt
	private var onColor = 0
	@ColorInt
	private var offColor = 0
	@ColorInt
	private var iconColor = 0
	@ColorInt
	private var disabledBackgroundColor = 0
	@ColorInt
	private var disabledIconColor = 0

	private val switcherPaint = Paint(Paint.ANTI_ALIAS_FLAG)

	private val iconRect = RectF(0f, 0f, 0f, 0f)
	private val iconClipRect = RectF(0f, 0f, 0f, 0f)
	private val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG)

	private val iconClipPaint = Paint(Paint.ANTI_ALIAS_FLAG)

	private var animatorSet: AnimatorSet = AnimatorSet()

	private var shadowOffset = 0f

	@ColorInt
	private var currentColor = 0
		private set(value) {
			field = value
			switcherPaint.color = value
			iconClipPaint.color = value
		}

	private var switchElevation = 0f
	private var iconHeight = 0f
	
	/**
	 * current icon position
	 * 0f -> icon is looks like O and switch is off
	 * 1f -> icon is looks like 1 and switch is on
	 */
	private var iconProgress = 0f
		private set(value) {
			if (field != value) {
				field = value

				val iconOffset = linearInterpolation(0f, iconRadius - iconCollapsedWidth / 2, value)
				iconRect.left = width - switcherCornerRadius - iconCollapsedWidth / 2 - iconOffset
				iconRect.right = width - switcherCornerRadius + iconCollapsedWidth / 2 + iconOffset

				val clipOffset = linearInterpolation(0f, iconClipRadius, value)
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
		private set(value) {
			field = value
			switcherRect.left = value + shadowOffset
			switcherRect.top = value + shadowOffset / 2
			switcherRect.right = width.toFloat() - value - shadowOffset
			switcherRect.bottom = height.toFloat() - value - shadowOffset - shadowOffset / 2
			invalidate()
		}




	init {
		attrs?.let { retrieveAttributes(attrs, defStyleAttr) }
		setOnClickListener { toggle() }
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
		                              Color.parseColor("#48EA8B"))

		offColor = typedArray.getColor(R.styleable.Switcher_switcher_off_color,
		                               Color.parseColor("#d62828"))
		
		disabledBackgroundColor = typedArray.getColor(
			R.styleable.Switcher_switcher_disabled_background_color,
			Color.parseColor("#C6C6C6")
		)
		
		disabledIconColor = typedArray.getColor(
			R.styleable.Switcher_switcher_disabled_icon_color,
			Color.parseColor("#EBEBE4")
		)

		iconColor = typedArray.getColor(R.styleable.Switcher_switcher_icon_color, Color.WHITE)

		mChecked = typedArray.getBoolean(R.styleable.Switcher_android_checked, false)
		mEnabled = typedArray.getBoolean(R.styleable.Switcher_android_enabled, true)
		
		//set def values according to isChecked value
		iconProgress = if (mChecked) 0f else 1f
		currentColor = if (mEnabled) if (mChecked) onColor else offColor else disabledBackgroundColor

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
		iconCollapsedWidth = iconRadius - iconClipRadius / 2

		iconHeight = iconRadius * 2f

		iconRect.set(
				width - switcherCornerRadius - iconCollapsedWidth / 2,
				((height - iconHeight) / 2f) - shadowOffset / 2,
				width - switcherCornerRadius + iconCollapsedWidth / 2,
				(height - (height - iconHeight) / 2f) - shadowOffset / 2
		)

		if (!mChecked) {
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

	private fun animateBackgroundColor() : ValueAnimator {
		val toColor = if (mChecked) onColor else offColor

		iconClipPaint.color = toColor

		return ValueAnimator().apply {
			addUpdateListener { currentColor = it.animatedValue as Int }
			setIntValues(currentColor, toColor)
			setEvaluator(ArgbEvaluator())
			duration = COLOR_ANIMATION_DURATION
		}
	}

	private fun animateIconTranslation(): ValueAnimator {
		//initial pos will translate from left to right -> (left to right)
		var startPos = 0f
		var endPos = -(width - shadowOffset - switcherCornerRadius * 2)

		if (mChecked) {
			//if checked -> start from endPos (right to left)
			startPos = endPos
			endPos = -shadowOffset
		}

		return ValueAnimator.ofFloat(0f, 1f).apply {
			addUpdateListener {
				val value = it.animatedValue as Float
				iconTranslateX = linearInterpolation(startPos, endPos, value)
			}
			doOnEnd { onClickOffset = 0f }
			duration = TRANSLATE_ANIMATION_DURATION
		}
	}

	private fun animateIconChange() : ValueAnimator {
		var amplitude = BOUNCE_ANIM_AMPLITUDE_IN
		var frequency = BOUNCE_ANIM_FREQUENCY_IN

		var newProgress = 1f

		if (mChecked) {
			amplitude = BOUNCE_ANIM_AMPLITUDE_OUT
			frequency = BOUNCE_ANIM_FREQUENCY_OUT
			newProgress = 0f
		}

		return ValueAnimator.ofFloat(iconProgress, newProgress).apply {
			addUpdateListener { iconProgress = it.animatedValue as Float }
			interpolator = BounceInterpolator(amplitude, frequency)
			duration = SWITCHER_ANIMATION_DURATION
		}
	}
	
	/**
	 * Animate switch to [mChecked] state and invoke listener after animation ends
	 */
	private fun animateSwitchWithClickInvoked(invoke: Boolean = true) {
		animatorSet.cancel()
		animatorSet = AnimatorSet()

		onClickOffset = ON_CLICK_RADIUS_OFFSET


		val colorBackgroundAnimator = animateBackgroundColor()

		val translateIconAnimator = animateIconTranslation()

		val iconAnimator = animateIconChange()

		animatorSet.apply {
			//doOnStart { listener?.invoke(this@Switcher, isChecked) }
			playTogether(colorBackgroundAnimator, translateIconAnimator, iconAnimator)
			start()
			//guarantee that animation will play smoothly if Activity or Fragment recreated
			//invoke after animation successfully played

			doOnEnd {
				if (mEnabled && invoke) checkListener?.invoke(this@Switcher, mChecked)
			}
		}
	}
	
	private fun setSwitchChecked() {
		currentColor = onColor
		iconProgress = 0f
		iconTranslateX = -shadowOffset
		
	}
	
	private fun setSwitchUnchecked() {
		currentColor = offColor
		iconProgress = 1f
		iconTranslateX = -(width - shadowOffset - switcherCornerRadius * 2)
	}

	/**
	 * Changes the isChecked state of this switch.
	 *
	 * @param checked true to check the switch, false to uncheck it
	 */
	private fun setChecked(checked: Boolean) {
		if (mChecked != checked) {
			mChecked = checked
			if (width != 0) { animateSwitchWithClickInvoked() }
			else {
				// manually set values and manually invoke listener
				// this branch will almost certainly not be called
				animatorSet.cancel()
				
				if (checked) setSwitchChecked()
				else setSwitchUnchecked()
				
				if (mEnabled) checkListener?.invoke(this, mChecked)
			}
		}
	}

	private var checkListener: ((view: Switcher, isChecked: Boolean) -> Unit)? = null

	/**
	 * Register a callback to be invoked when the isChecked state of this switch
	 * changes.
	 *
	 * @param listener the callback to call on isChecked state change
	 */
	fun setOnCheckedChangeListener(listener: (view: Switcher, isChecked: Boolean) -> Unit) {
		checkListener = listener
	}
	
	fun removeOnCheckedChangeListener() {
		if (checkListener != null) checkListener = null
	}
	
	override fun setEnabled(isEnabled: Boolean) {
		super.setEnabled(isEnabled)
		if (mEnabled != isEnabled) {
			mEnabled = isEnabled
			//invalidate()
		}
	}

	fun isChecked(checked: Boolean = mChecked): Boolean {
		if (mChecked != checked) {
			mChecked = checked
			if (checked) setSwitchChecked()
			else setSwitchUnchecked()
		}
		return mChecked
	}

	private fun toggle() = setChecked(!mChecked)

	override fun onSaveInstanceState(): Parcelable {
		super.onSaveInstanceState()
		return Bundle().apply {
			putParcelable(STATE, super.onSaveInstanceState())
			putBoolean(KEY_CHECKED, mChecked)
		}
	}

	override fun onRestoreInstanceState(state: Parcelable?) {
		if (state is Bundle) {

			mChecked = state.getBoolean(KEY_CHECKED)
			restoreSwitchState(mChecked)

			super.onRestoreInstanceState(state.getParcelable(STATE))
		}
	}

	private fun restoreSwitchState(checked: Boolean) {
		if (!checked) {
			currentColor = offColor
			iconProgress = 1f
			iconTranslateX = -(width - shadowOffset - switcherCornerRadius * 2)
		}
		else {
			currentColor = onColor
			iconProgress = 0f
			iconTranslateX = -shadowOffset
		}

	}

	private fun linearInterpolation(a: Float, b: Float, x: Float): Float = a + (b - a) * x

	/*
	 * (read more on https://evgenii.com/blog/spring-button-animation-on-android/)
	 */
	private class BounceInterpolator(private val amplitude: Double, private val frequency: Double) :
			Interpolator {
		
		override fun getInterpolation(time: Float): Float =
			(-1 * Math.E.pow(-time / amplitude) * cos(frequency * time) + 1).toFloat()
		
	}
	
}