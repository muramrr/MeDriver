/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.12.2020 22:46
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
