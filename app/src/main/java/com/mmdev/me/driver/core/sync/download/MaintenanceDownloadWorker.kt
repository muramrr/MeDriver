/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.11.2020 01:42
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.sync.download

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent

/**
 *
 */

@KoinApiExtension
class MaintenanceDownloadWorker(
	private val context: Context,
	params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {
	
	
	override suspend fun doWork(): Result = coroutineScope {
		Result.success()
	}
	
}