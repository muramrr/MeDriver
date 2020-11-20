/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.11.2020 01:41
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.sync.download

import android.annotation.SuppressLint
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mmdev.me.driver.core.utils.log.logDebug

/**
 * Handle all firebase cloud function messages to sync database with server
 * //todo: make available only for [SubscriptionType.PRO] users
 */

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseNotificationSync: FirebaseMessagingService(), LifecycleObserver {
	
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
		if(isAppInForeground) {
			// do foreground stuff on your activities
			logDebug(javaClass, "app is in foreground, unhandled notification: ${remoteMessage.data["CONTENT"]}")
		} else {
			when(remoteMessage.data["TYPE"]) {
				"NEW_VEHICLE" -> handleVehicleAdded(remoteMessage)
				"NEW_MAINTENANCE" -> handleMaintenanceAdded(remoteMessage)
				"NEW_FUEL_HISTORY" -> handleFuelHistoryAdded(remoteMessage)
			}
		}
	}
	
	private fun handleVehicleAdded(remoteMessage: RemoteMessage) {
		
		val data = bundleOf(
			"USER_ID" to remoteMessage.data["USER_ID"],
			"VIN" to remoteMessage.data["VEHICLE_VIN"],
		)
		
	}
	
	
	private fun handleMaintenanceAdded(remoteMessage: RemoteMessage) {
		
		val data = bundleOf(
			"USER_ID" to remoteMessage.data["USER_ID"],
			"VIN" to remoteMessage.data["VEHICLE_VIN"],
			"TABLE" to "maintenance",
			"ENTRY_ID" to remoteMessage.data["ENTRY_ID"]
		)
		
	}
	
	private fun handleFuelHistoryAdded(remoteMessage: RemoteMessage) {
		
		val data = bundleOf(
			"USER_ID" to remoteMessage.data["USER_ID"],
			"VIN" to remoteMessage.data["VEHICLE_VIN"],
			"TABLE" to "fuel_history",
			"ENTRY_ID" to remoteMessage.data["ENTRY_ID"]
		)
		
	}
	
	
}