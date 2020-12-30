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
import com.mmdev.me.driver.presentation.utils.extensions.toPx
import kotlin.math.max
import kotlin.math.min

/*
 * see https://dribbble.com/shots/5095383-Loader-Animation
 */

class LoadingView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
	
	private val ovalRectF = RectF()
	private val sweepPaint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.STROKE
		strokeCap = Paint.Cap.ROUND
	}
	//shadow "glowing" radius
	//strokeSize + 25%
	private var sweepPaintShadowRadius = 0f
	
	private var minStrokeSize = 2.toPx()
	private var maxStrokeSize = 8.toPx()
	
	/** force stroke to be in bounds min 2dp and max 8dp
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
			sweepPaintShadowRadius = field * 1.25f
			sweepPaint.setShadowLayer(sweepPaintShadowRadius,0f,0f, sweepColor)
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
			sweepPaint.setShadowLayer(sweepPaintShadowRadius,0f,0f, field)
		}
	
	private var animatorSet: AnimatorSet? = AnimatorSet()
	
	private var viewRotateAnimator: ValueAnimator? = null
	private fun viewRotateAnimator(): ValueAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
		duration = 1600
		interpolator = LinearInterpolator()
		repeatCount = INFINITE
		repeatMode = RESTART
		addUpdateListener {
			rotation = it.animatedValue as Float //causing memory leaks? keeping references to view
		}
	}
	
	private var angleAnimator: ValueAnimator? = null
	private fun angleAnimator(): ValueAnimator = ValueAnimator.ofFloat(5f, 105f).apply {
		duration = 800
		// god given custom interpolator
		interpolator = PathInterpolator(1f, 0f, 0f, 1f)
		repeatCount = INFINITE
		repeatMode = REVERSE
		addUpdateListener {
			
			sweepAngle1 = it.animatedValue as Float
			sweepAngle2 = it.animatedValue as Float
			sweepAngle3 = it.animatedValue as Float
			invalidate() //causing memory leaks?
		}
		
	}
	
	/** using for toggle animation
	 * true -> animatorSet.resume()
	 * false -> animatorSet.pause()
	 */
	private var isAnimating = true
		private set(value) {
			field = value
			if (field) animatorSet?.resume()
			else animatorSet?.pause()
		}
	
	
	init {
		attrs?.let { retrieveAttributes(attrs, defStyleAttr) }
	}
	
	private fun retrieveAttributes(attrs: AttributeSet, defStyleAttr: Int) {
		val ta = context.obtainStyledAttributes(
			attrs,
			R.styleable.LoadingView,
			defStyleAttr,
			R.style.LoadingView
		)
		
		sweepColor = ta.getColor(R.styleable.LoadingView_loadStrokeColor, Color.WHITE)
		
		strokeSize = ta.getDimensionPixelSize(R.styleable.LoadingView_loadStrokeWidth, 8)
		
		ta.recycle()
	}
	
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		//force square view
		val width = MeasureSpec.getSize(widthMeasureSpec)
		val height = MeasureSpec.getSize(heightMeasureSpec)
		
		//check what is lower to draw square
		//apply stroke width according to measured min size
		val minSize = min(width, height).also {
			strokeSize = max(minStrokeSize, min(maxStrokeSize, it / 24))
		}
		
		//calculate bounds to draw without cutting off
		ovalRectF.set(paddingLeft.toFloat() + strokeSize,
		              paddingTop.toFloat() + strokeSize,
		              (minSize - paddingRight).toFloat() - strokeSize,
		              (minSize - paddingBottom).toFloat() - strokeSize)
		
		
		setMeasuredDimension(minSize, minSize)
		
		// auto start animation
		// no need to toggle
		angleAnimator = angleAnimator()
		viewRotateAnimator = viewRotateAnimator()
		animatorSet!!.cancel()
		animatorSet!!.playTogether(angleAnimator, viewRotateAnimator)
		animatorSet!!.start()
	}
	
	override fun onDraw(canvas: Canvas) {
		canvas.drawArc(ovalRectF, 0f, sweepAngle1, false, sweepPaint)
		canvas.drawArc(ovalRectF, 120f, sweepAngle2, false, sweepPaint)
		canvas.drawArc(ovalRectF, 240f, sweepAngle3, false, sweepPaint)
		
	}
	
	fun toggleAnimation() {
		isAnimating = !isAnimating
	}
	
	override fun onDetachedFromWindow() {
		super.onDetachedFromWindow()
		animatorSet?.cancel()
		angleAnimator?.cancel()
		viewRotateAnimator?.cancel()
		animatorSet = null
		viewRotateAnimator = null
		angleAnimator = null
	}
}
	
