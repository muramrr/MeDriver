/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.07.20 20:47
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.common.custom.components

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.res.ResourcesCompat
import com.mmdev.me.driver.R
import kotlin.math.min

/**
 *
 */

class CircularProgressBar @JvmOverloads constructor(context: Context,
                                                    attrs: AttributeSet? = null,
                                                    defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

	companion object {
		private const val STROKE_THICKNESS_FRACTION = 0.075f
		private const val TEXT_SIZE_FRACTION = 0.25f
		private const val MAX_PROGRESS = 100f
		private const val ANIM_DURATION_MS = 800L
	}

	private var strokeThickness: Float = 0f

	private var progressTextSize: Float = 0f

	// pre-allocate and reuse in onDraw()
	private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

	private val circleBounds = RectF()

	private val progressTextBounds = Rect()

	private var progress = 0f

	private val progressValueAnimator = ValueAnimator.ofFloat(0f, progress).apply {
		duration = ANIM_DURATION_MS
		interpolator = AccelerateDecelerateInterpolator()
		addUpdateListener {
			progress = animatedValue as Float
			invalidate()
		}
	}

	private var backgroundColor: Int? = null

	private var foregroundColor: Int? = null

	private var textColor: Int? = null

	init {
		attrs?.let {
			val typedArray = context.obtainStyledAttributes(
					it,
					R.styleable.CircularProgressBar,
					defStyleAttr,
					R.style.CircularProgress)

			backgroundColor = typedArray.getColor(
					R.styleable.CircularProgressBar_barBackgroundColor,
					Color.parseColor("#e1e7e9"))

			foregroundColor = typedArray.getColor(
					R.styleable.CircularProgressBar_barForegroundColor,
					Color.BLUE)

			textColor = typedArray.getColor(
					R.styleable.CircularProgressBar_android_textColor,
					Color.BLACK)

			typedArray.recycle()
		}
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		drawBackground(canvas)
		drawForeground(canvas)
		drawProgressText(canvas)
	}

	/**
	 * Triggered when you set width and height explicitly or when the view gets resized inside another container
	 */
	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
		super.onSizeChanged(w, h, oldw, oldh)

		val diameter = min(w, h)
		strokeThickness = diameter * STROKE_THICKNESS_FRACTION

		val centerX = w / 2
		val centerY = h / 2
		val squareSide = diameter - strokeThickness
		val halfOfStrokeWidth = squareSide / 2

		circleBounds.apply {
			left = centerX - halfOfStrokeWidth
			top = centerY - halfOfStrokeWidth
			right = centerX + halfOfStrokeWidth
			bottom = centerY + halfOfStrokeWidth
		}

		progressTextSize = diameter * TEXT_SIZE_FRACTION
	}

	fun updateProgress(progress: Float) {
		if (progressValueAnimator.isRunning) {
			progressValueAnimator.cancel()
		}
		progressValueAnimator.setFloatValues(this.progress, progress)
		progressValueAnimator.start()
	}

	private fun drawBackground(canvas: Canvas) {
		paint.apply {
			backgroundColor?.let { color = it }
			style = Paint.Style.STROKE
			strokeWidth = strokeThickness
		}
		canvas.drawOval(circleBounds, paint)
	}

	private fun drawForeground(canvas: Canvas) {
		paint.apply {
			foregroundColor?.let { color = it }
			style = Paint.Style.STROKE
			strokeCap = Paint.Cap.ROUND
		}
		val sweepAngle = progress / MAX_PROGRESS * 360
		canvas.drawArc(circleBounds, -90f, sweepAngle, false, paint)
	}

	private fun drawProgressText(canvas: Canvas) {
		val text = "${progress.toInt()}%"

		paint.apply {
			textColor?.let { color = it }
			style = Paint.Style.FILL
			textSize = progressTextSize
			textAlign = Paint.Align.CENTER
			typeface = ResourcesCompat.getFont(context, R.font.m_plus_rounded1c_medium)
			getTextBounds(text, 0, text.length, progressTextBounds)
		}
		canvas.drawText(
				text,
				circleBounds.centerX(),
				circleBounds.centerY() + progressTextBounds.height() / 2,
				paint
		)
	}
}