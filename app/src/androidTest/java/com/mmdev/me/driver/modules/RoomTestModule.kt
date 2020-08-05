package com.mmdev.me.driver.modules

import androidx.room.Room
import com.mmdev.me.driver.data.datasource.local.database.MeDriveRoomDatabase
import org.koin.dsl.module

/**
 * In-Memory Room Database definition
 */
val roomTestModule = module (override = true) {
	single {
		// In-Memory database config
		Room.inMemoryDatabaseBuilder(get(), MeDriveRoomDatabase::class.java)
			.allowMainThreadQueries()
			.build()
	}
}