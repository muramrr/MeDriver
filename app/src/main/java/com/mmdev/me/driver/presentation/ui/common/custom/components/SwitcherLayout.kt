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

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.mmdev.me.driver.R
import com.mmdev.me.driver.presentation.utils.extensions.visible

/**
 *
 */

class SwitcherLayout(context: Context, attrs: AttributeSet?): LinearLayout(context, attrs) {
	
	private val tvTitle: TextView
	private val tvSubtitle: TextView
	private val switcher: Switcher
	
	fun setTitle(title: String) {
		tvTitle.text = title
	}
	
	fun setSubtitle(subtitle: String) {
		if (subtitle.isNotBlank() && tvSubtitle.visibility != View.GONE) {
			tvSubtitle.text = subtitle
		}
	}
	
	fun setChecked(checked: Boolean) {
		switcher.isChecked(checked)
	}
	
	fun setSwitcherListener(listener: (view: Switcher, isChecked: Boolean) -> Unit) {
		switcher.setOnCheckedChangeListener(listener)
	}
	
	override fun setEnabled(enabled: Boolean) {
		super.setEnabled(enabled)
		switcher.isEnabled = enabled
	}
	
	init {
		val a: TypedArray = context.obtainStyledAttributes(
			attrs, R.styleable.SwitcherLayout, 0, 0
		)
		val titleText = a.getString(R.styleable.SwitcherLayout_title)
		val subtitleText = a.getString(R.styleable.SwitcherLayout_subtitle)
		val checked = a.getBoolean(R.styleable.SwitcherLayout_checked, false)
		
		a.recycle()
		val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
		inflater.inflate(R.layout.layout_switcher, this, true)
		tvTitle = findViewById(R.id.tvSwitcherTitle)
		tvSubtitle = findViewById(R.id.tvSwitcherSubtitle)
		switcher = findViewById(R.id.switcher)
		
		tvTitle.text = titleText
		
		if (!subtitleText.isNullOrBlank()) {
			tvSubtitle.visible(0)
			tvSubtitle.text = subtitleText
		}
		
		switcher.isChecked(checked)
	}
}