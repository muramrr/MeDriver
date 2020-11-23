/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 23.11.2020 19:47
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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