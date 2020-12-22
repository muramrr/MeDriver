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

package com.mmdev.me.driver.core.sync

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
				logError(TAG, "Downloading worker job result is FAILURE, email is not valid")
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
