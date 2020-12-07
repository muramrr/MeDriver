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

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Handle all firebase cloud function messages to sync database with server
 * //todo: make available only for [SubscriptionType.PRO] users
 */

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseNotificationSync: FirebaseMessagingService(), LifecycleObserver {
	
	companion object {
		private const val TYPE_VEHICLE = "VEHICLE"
		private const val TYPE_MAINTENANCE = "MAINTENANCE"
		private const val TYPE_FUEL_HISTORY = "FUEL_HISTORY"
		
		const val EMAIL = "EMAIL"
		const val VIN = "VIN"
		const val ID = "ID"
	}
	
	private var isAppInForeground = false
	
	override fun onCreate() {
		super.onCreate()
		ProcessLifecycleOwner.get().lifecycle.addObserver(this)
	}
	
	override fun onDestroy() {
		super.onDestroy()
		ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
	}
	
	
	@OnLifecycleEvent(Lifecycle.Event.ON_START)
	private fun onForegroundStart() {
		isAppInForeground = true
	}
	
	@OnLifecycleEvent(Lifecycle.Event.ON_STOP)
	private fun onForegroundStop() {
		isAppInForeground = false
	}
	
	override fun onMessageReceived(remoteMessage: RemoteMessage) {
		val data = workDataOf(
			EMAIL to remoteMessage.data[EMAIL],
			VIN to remoteMessage.data[VIN],
			ID to remoteMessage.data[ID]
		)
		
		val constraints = Constraints.Builder()
			.setRequiresBatteryNotLow(true)
			.setRequiredNetworkType(NetworkType.CONNECTED) //just in case
			.build()
		
		
		when(remoteMessage.data["TYPE"]) {
			TYPE_VEHICLE -> enqueueVehicleDownload(data, constraints)
			TYPE_MAINTENANCE -> enqueueMaintenanceDownload(data, constraints)
			TYPE_FUEL_HISTORY -> enqueueFuelHistoryDownload(data, constraints)
		}
		
	}
	
	private fun enqueueMaintenanceDownload(data: Data, constraints: Constraints) {
		val downloadRequest = OneTimeWorkRequestBuilder<MaintenanceDownloadWorker>()
			.setConstraints(constraints)
			.setInputData(data)
			.build()
		
		WorkManager.getInstance(applicationContext)
			.enqueue(downloadRequest)
	}
	
	private fun enqueueFuelHistoryDownload(data: Data, constraints: Constraints) {
		val downloadRequest = OneTimeWorkRequestBuilder<FuelHistoryDownloadWorker>()
			.setConstraints(constraints)
			.setInputData(data)
			.build()
		
		WorkManager.getInstance(applicationContext)
			.enqueue(downloadRequest)
	}
	
	private fun enqueueVehicleDownload(data: Data, constraints: Constraints) {
		val downloadRequest = OneTimeWorkRequestBuilder<VehicleDownloadWorker>()
			.setConstraints(constraints)
			.setInputData(data)
			.build()
		
		WorkManager.getInstance(applicationContext)
			.enqueue(downloadRequest)
	}
	
}