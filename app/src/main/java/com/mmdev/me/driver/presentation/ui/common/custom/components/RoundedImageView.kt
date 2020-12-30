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

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Region
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView.ScaleType.CENTER_CROP
import androidx.appcompat.widget.AppCompatImageView
import com.mmdev.me.driver.R
import kotlin.math.min


/**
 * Simple class to draw rounded ImageView
 * It's not allocating any bitmaps or drawable
 * @see [onDraw] method: we provide rounded path and clip canvas to it bounds
 */

class RoundedImageView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet?,
	defStyle: Int = 0
): AppCompatImageView(context, attrs, defStyle) {

	private var radius = 0f
	private var isCircle = false // flag that describes is view should be full circle
		set(value) {
			field = value
			// if set to True we calculate radius
			if (field) radius = min(width, height) / 2f
		}

	private val viewRect = RectF() // general view rect
	private val roundPath = Path() //path which used to crop image
	
	private val croppedRect = RectF() // rect after crop operation with roundPath
	private val croppedRegion = Region() // region after with croppedRect
	
	private val clickableRect = Rect() // rect which used to be a clickable bounds
	private val clickableRegion = Region() // region which handles clicks
	

	init {
		attrs?.let {

			val ta = context.obtainStyledAttributes(
					it,
					R.styleable.RoundedImageView,
					defStyle,
					0)
			radius = ta.getDimensionPixelSize(R.styleable.RoundedImageView_imgCornerRadius, 0).toFloat()
			
			ta.recycle()
		}
	}
	
	override fun getScaleType(): ScaleType = super.getScaleType() ?: CENTER_CROP
	
	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
		super.onSizeChanged(w, h, oldw, oldh)
		// if radius not specified force to be a circle
		if (radius == 0f) isCircle = true
		
		viewRect.set(calculateBounds())
	}
	
	
	private fun calculateBounds(): RectF {
		val availableWidth = width - paddingLeft - paddingRight
		val availableHeight = height - paddingTop - paddingBottom
		val sideLength = min(availableWidth, availableHeight)
		val left = paddingLeft + (availableWidth - sideLength) / 2f
		val top = paddingTop + (availableHeight - sideLength) / 2f
		return RectF(left, top, left + sideLength, top + sideLength)
	}

	override fun onDraw(canvas: Canvas) {
		roundPath.reset()
		
		if (isCircle) {
			roundPath.reset()
			roundPath.addCircle(width / 2f, height / 2f, radius, Path.Direction.CW)
			//canvas.drawCircle(width / 2f, height / 2f, radius, paintBitmap)
		}
		else {
			viewRect.set(0f, 0f, width.toFloat(), height.toFloat())
			
			roundPath.reset()
			roundPath.addRoundRect(viewRect, radius, radius, Path.Direction.CW)
			
			//canvas.drawPath(roundPath, paintBitmap)
		}
		
		// clip ImageView
		canvas.clipPath(roundPath)
		
		//calculate clickable region
		calculateClickableBounds()
		
		super.onDraw(canvas)
	}
	
	//calculate clickable region
	private fun calculateClickableBounds() {
	
		roundPath.computeBounds(croppedRect, true)
		clickableRect.set(croppedRect.left.toInt(), croppedRect.top.toInt(),
		                  croppedRect.right.toInt(), croppedRect.bottom.toInt())
		croppedRegion.set(clickableRect)
		clickableRegion.setPath(roundPath, croppedRegion)
	
	}

	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event: MotionEvent): Boolean =
		inTouchableArea(event.x, event.y) && super.onTouchEvent(event)

	private fun inTouchableArea(x: Float, y: Float): Boolean {
		//logWtf(message = "${clickableRegion.bounds} $x , $y")
		return if (clickableRegion.isEmpty) true
		else clickableRegion.contains(x.toInt(), y.toInt())
	}
	
	fun setCornerRadius(radius: Float) {
		this.radius = radius
		invalidate()
	}

}