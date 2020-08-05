/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.08.20 16:10
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.common.custom.components

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.animation.ValueAnimator.*
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.PathInterpolator
import com.mmdev.me.driver.R
import com.mmdev.me.driver.presentation.utils.toPx
import kotlin.math.min

/*
 * see [https://dribbble.com/shots/5095383-Loader-Animation]
 */

class LoadingView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
	
	private val ovalRectF = RectF()
	private val sweepPaint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.STROKE
		strokeCap = Paint.Cap.ROUND
	}
	
	private var minStrokeSize = 4.toPx()
	private var maxStrokeSize = 8.toPx()
	
	/** force stroke to be in bounds min 4dp and max 8dp
	 * also auto apply [sweepPaint] strokeWidth
	 * @see minStrokeSize
	 * @see maxStrokeSize
	 */
	private var strokeSize = 0
		private set(value) {
			field = when {
				value > maxStrokeSize -> maxStrokeSize
				value < minStrokeSize -> minStrokeSize
				else -> value
			}
			sweepPaint.strokeWidth = field.toFloat()
		}
	
	private var sweepAngle1 = 5f
	private var sweepAngle2 = 5f
	private var sweepAngle3 = 5f
	
	//auto apply paint color while changing this
	//auto apply "glowing" shadow with same color
	private var sweepColor: Int = Color.WHITE
		private set(value) {
			field = value
			sweepPaint.color = field
			sweepPaint.setShadowLayer(10f,0f,0f, field)
		}
	
	private val animatorSet = AnimatorSet()
	
	/** using for toggle animation
	 * true -> animatorSet.resume()
	 * false -> animatorSet.pause()
	 */
	private var isAnimating = true
		private set(value) {
			field = value
			if (field) animatorSet.resume()
			else animatorSet.pause()
		}
	
	
	init {
		attrs?.let {
			val ta = context.obtainStyledAttributes(
					it,
					R.styleable.LoadingView,
					defStyleAttr,
					R.style.LoadingView
			)
			
			sweepColor = ta.getColor(R.styleable.LoadingView_loadStrokeColor, Color.WHITE)
			
			strokeSize = ta.getDimensionPixelSize(R.styleable.LoadingView_loadStrokeWidth, 8)
			
			ta.recycle()
		}
	
	}
	
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		//force square view
		val width = MeasureSpec.getSize(widthMeasureSpec)
		val height = MeasureSpec.getSize(heightMeasureSpec)
		
		//check what is lower to draw square
		val minSize = min(width, height)
		
		//calculate bounds to draw without cutting off
		ovalRectF.set(paddingLeft.toFloat() + strokeSize,
		              paddingTop.toFloat() + strokeSize,
		              (minSize - paddingRight).toFloat() - strokeSize,
		              (minSize - paddingBottom).toFloat() - strokeSize)
		
		
		setMeasuredDimension(minSize, minSize)
		
		// auto start animation
		// no need to toggle
		animatorSet.cancel()
		animatorSet.playTogether(angleAnimator(), viewRotateAnimator())
		animatorSet.start()
	}
	
	override fun onDraw(canvas: Canvas) {
		canvas.drawArc(ovalRectF, 0f, sweepAngle1, false, sweepPaint)
		canvas.drawArc(ovalRectF, 120f, sweepAngle2, false, sweepPaint)
		canvas.drawArc(ovalRectF, 240f, sweepAngle3, false, sweepPaint)
		
	}
	
	fun toggleAnimation() {
		isAnimating = !isAnimating
	}
	
	private fun angleAnimator() = ValueAnimator.ofFloat(5f, 105f).apply {
		duration = 800
		// god given custom interpolator
		interpolator = PathInterpolator(1f, 0f, 0f, 1f)
		repeatCount = INFINITE
		repeatMode = REVERSE
		addUpdateListener {
			
			sweepAngle1 = it.animatedValue as Float
			sweepAngle2 = it.animatedValue as Float
			sweepAngle3 = it.animatedValue as Float
			invalidate()
		
		}
		
	}
	
	private fun viewRotateAnimator() = ValueAnimator.ofFloat(0f, 360f).apply {
		duration = 1600
		interpolator = LinearInterpolator()
		repeatCount = INFINITE
		repeatMode = RESTART
		addUpdateListener { rotation = it.animatedValue as Float }
	}
}