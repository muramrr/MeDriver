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

package com.mmdev.me.driver.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mmdev.me.driver.R
import com.mmdev.me.driver.core.utils.MyDispatchers
import com.mmdev.me.driver.core.utils.extensions.currentLocalDateTime
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.domain.fuel.history.data.DistanceBound
import com.mmdev.me.driver.domain.maintenance.data.components.PlannedParts
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.domain.vehicle.IVehicleRepository
import com.mmdev.me.driver.domain.vehicle.data.PendingReplacement
import com.mmdev.me.driver.presentation.ui.MainActivity
import com.mmdev.me.driver.presentation.ui.maintenance.VehicleSystemNodeConstants
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.datetime.minus
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * Calculating and showing reminders about consumable parts replacement
 *
 * [TIME_LIMIT] defines the time threshold beyond which the notification will be shown
 * [DISTANCE_LIMIT] defines the distance threshold beyond which the notification will be shown
 */

@KoinApiExtension
class NotificationWorker(
	private val context: Context,
	params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	private val TIME_LIMIT = 2
	private val DISTANCE_LIMIT = DistanceBound(kilometers = 2000, miles = null)
	
	
	
	override suspend fun doWork(): Result = coroutineScope {
		logDebug(TAG, "Worker Started!")
		
		val repository: IVehicleRepository = get()
		
		withContext(MyDispatchers.default()) {
			repository.getAllSavedVehicles().fold(
				success = { vehicles ->
					if (!vehicles.isNullOrEmpty()) {
						logDebug(TAG, "Vehicles is not null, calculating thresholds...")
						vehicles.forEach { vehicle ->
							repository.getPendingReplacements(vehicle).fold(
								success = { calculateThresholds(it) },
								failure = {
									logError(TAG, "${it.message}")
									return@withContext Result.failure()
								}
							)
						}
						Result.success()
					}
					else {
						logError(TAG, "Null vehicles list")
						Result.failure()
					}
				},
				failure = {
					logError(TAG, "No saved vehicles")
					Result.failure()
				}
			)
		}
	}
	
	private fun calculateThresholds(replacements: Map<SparePart, PendingReplacement?>) {
		replacements.forEach { (key, value) ->
			value?.let {
				if ((it.distanceRemain.kilometers <= DISTANCE_LIMIT.kilometers) ||
				    (it.finalDate.minus(currentLocalDateTime().date)).months <= TIME_LIMIT) {
					showNotification(
						VehicleSystemNodeConstants.plannedComponents[PlannedParts.valueOf(key.getSparePartName()).ordinal]
					)
				}
			}
		}
	}
	
	private fun showNotification(@StringRes componentRes: Int) {
		
		val component = context.getString(componentRes)
		val title = context.getString(R.string.notification_consumable_replace_title)
		val bodyText = context.getString(R.string.notification_consumable_replace_text, component)
		
		val pendingIntent = NavDeepLinkBuilder(context)
			.setComponentName(MainActivity::class.java)
			.setGraph(R.navigation.navigation_main)
			.setDestination(R.id.garageFragment)
			.createPendingIntent()
		
		val channelId = context.getString(R.string.notification_channel_consumable_replace_id)
		val channelName = context.getString(R.string.notification_channel_consumable_replace_name)
		val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
		
		//note: is it bad to show app icon in notification?
		val drawable = ResourcesCompat.getDrawable(
			context.resources, R.mipmap.ic_launcher, context.theme
		)
		val bitmap = drawable!!.toBitmap(300, 300, Bitmap.Config.ARGB_8888)
		
		val notificationBuilder = NotificationCompat.Builder(context, channelId)
			.setAutoCancel(true)
			.setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
			.setContentIntent(pendingIntent)
			.setContentTitle(title)
			.setContentText(bodyText)
			.setContentInfo(component)
			.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
			.setLargeIcon(bitmap)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.setSound(defaultSoundUri)
			.setSmallIcon(R.drawable.ic_notification_warning)
			.setStyle(NotificationCompat.BigTextStyle().bigText(bodyText))
		
		val notificationManager =
			context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		
		// Since android Oreo notification channel is needed.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				channelId, channelName, NotificationManager.IMPORTANCE_HIGH
			)
			channel.description = context.getString(R.string.notification_channel_consumable_replace_description)
			notificationManager.createNotificationChannel(channel)
		}
		
		val notificationId = System.currentTimeMillis().toInt()
		notificationManager.notify(notificationId, notificationBuilder.build())
	}
}