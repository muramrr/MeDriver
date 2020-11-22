/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 01:08
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.sync.download

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mmdev.me.driver.core.utils.MyDispatchers
import com.mmdev.me.driver.data.sync.download.maintenance.IMaintenanceDownloader
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Worker responsible to download only MaintenanceHistory
 */

@KoinApiExtension
class MaintenanceDownloadWorker(
	context: Context,
	params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {
	
	private val downloader: IMaintenanceDownloader by inject()
	
	override suspend fun doWork(): Result = withContext(MyDispatchers.io()) {
		val email = inputData.getString(FirebaseNotificationSync.EMAIL)
		val vin = inputData.getString(FirebaseNotificationSync.VIN)
		val id = inputData.getString(FirebaseNotificationSync.ID)
		
		if (!email.isNullOrBlank() && !vin.isNullOrBlank() && !id.isNullOrBlank()) {
			
			//no need to check connection, because we came here from Firebase Messaging handler
			//message cannot be even received without connection to firebase
			val downloadTask = async { downloader.downloadSingle(email, vin, id).collect {  } }
			
			downloadTask.await()
			Result.success()
		}
		else Result.failure()
		
	}
	
}