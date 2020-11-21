/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.11.2020 17:32
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.workers

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker.Result
import androidx.work.testing.TestListenableWorkerBuilder
import com.mmdev.me.driver.core.notifications.NotificationWorker
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Notification worker test (immediately brings notification)
 */

@RunWith(JUnit4::class)
class NotificationWorkerTest {
	private lateinit var context: Context
	
	@Before
	fun setup() {
		context = ApplicationProvider.getApplicationContext()
	}
	
	@Test
	fun testRefreshMainDataWork() {
		// Get the ListenableWorker
		val worker = TestListenableWorkerBuilder<NotificationWorker>(context).build()
		// Start the work synchronously
		val result = worker.startWork().get()
		assertEquals(result, Result.success())
	}
	
}