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