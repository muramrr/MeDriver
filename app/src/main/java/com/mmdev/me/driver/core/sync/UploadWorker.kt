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
import com.mmdev.me.driver.core.utils.log.logWtf
import com.mmdev.me.driver.data.sync.upload.fuel.IFuelHistoryUploader
import com.mmdev.me.driver.data.sync.upload.maintenance.IMaintenanceUploader
import com.mmdev.me.driver.data.sync.upload.vehicle.IVehicleUploader
import com.mmdev.me.driver.presentation.ui.MainActivity
import com.mmdev.me.driver.presentation.ui.SharedViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Worker responsible for manage local cached operations
 * If any cached operation exists -> execute upload to server
 * Controls from [MainActivity]
 *
 * Main requirements to being executed successfully:
 * 1. Network is available
 * @see [com.mmdev.me.driver.core.utils.ConnectionManager], [MedriverApp.isNetworkAvailable]
 *
 * 2. User is not null (authenticated)
 * @see [MedriverApp.currentUser]
 *
 * 3. User has valid subscription
 * @see [UserDataInfo]
 *
 * 4. Internet is available
 * @see [MedriverApp.isInternetWorking]
 */


@KoinApiExtension
class UploadWorker(appContext: Context, workerParams: WorkerParameters):
		CoroutineWorker(appContext, workerParams), KoinComponent {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	private val fuelHistoryUploader: IFuelHistoryUploader by inject()
	private val maintenanceUploader: IMaintenanceUploader by inject()
	private val vehicleUploader: IVehicleUploader by inject()

	
	override suspend fun doWork(): Result = withContext(MyDispatchers.io()) {
		if (MedriverApp.isInternetWorking()) {
			logWtf(TAG, "Doing work...")
			val email = inputData.getString(MainActivity.USER_KEY)
			if (!email.isNullOrBlank()) {
				val syncOperations = listOf(
					async { fuelHistoryUploader.fetch(email).collect {  } },
					async { maintenanceUploader.fetch(email).collect {  } },
					async { vehicleUploader.fetch(email).collect {  } }
				)
				syncOperations.awaitAll()
				SharedViewModel.uploadWorkerExecuted = true
				Result.success()
			}
			else {
				SharedViewModel.uploadWorkerExecuted = false
				Result.failure()
			}
			
		}
		else {
			logWtf(TAG, "Internet is not working...")
//			if (runAttemptCount < 5) {
//				logWtf(TAG, "retrying...")
//				Result.retry()
//
//			} else {
//
//			}
			SharedViewModel.uploadWorkerExecuted = false
			Result.failure()
		}
		
		
	}
}
