/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.12.2020 13:44
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.sync.download

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mmdev.me.driver.core.MedriverApp
import com.mmdev.me.driver.core.utils.MyDispatchers
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.data.sync.download.IDataDownloader
import com.mmdev.me.driver.presentation.ui.MainActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 *
 */

@KoinApiExtension
class DownloadWorker(appContext: Context, workerParams: WorkerParameters):
		CoroutineWorker(appContext, workerParams), KoinComponent {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	private val downloader: IDataDownloader by inject()
	
	
	override suspend fun doWork(): Result = withContext(MyDispatchers.io()) {
		if (MedriverApp.isInternetWorking()) {
			logDebug(TAG, "Doing work...")
			val email = inputData.getString(MainActivity.USER_KEY)
			if (!email.isNullOrBlank()) {
				
				downloader.fetchNewFromServer(email).collect { result ->
					result.fold(
						success = { logInfo(TAG, "Successfully downloaded new data") },
						failure = { logError(TAG, "Error downloading new data: ${it.message}") }
					)
				}
				logInfo(TAG, "Downloading worker job result is SUCCESS")
				Result.success()
			}
			else {
				logInfo(TAG, "Downloading worker job result is FAILURE")
				Result.failure()
			}
			
		}
		else {
			logError(TAG, "Internet is not working...")
			//			if (runAttemptCount < 5) {
			//				logWtf(TAG, "retrying...")
			//				Result.retry()
			//
			//			} else {
			//
			//			}
			logError(TAG, "Downloading worker job result is FAILURE")
			Result.failure()
		}
		
	}
}
