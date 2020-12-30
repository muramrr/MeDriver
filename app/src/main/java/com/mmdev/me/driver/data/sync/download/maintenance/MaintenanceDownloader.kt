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

package com.mmdev.me.driver.data.sync.download.maintenance

import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.data.core.firebase.FIRESTORE_NO_DOCUMENT_EXCEPTION
import com.mmdev.me.driver.data.datasource.maintenance.local.IMaintenanceLocalDataSource
import com.mmdev.me.driver.data.datasource.maintenance.server.IMaintenanceServerDataSource
import com.mmdev.me.driver.data.repository.maintenance.mappers.MaintenanceMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import com.mmdev.me.driver.domain.core.SimpleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.transform

/**
 * [IMaintenanceDownloader] implementation
 */

class MaintenanceDownloader(
	private val local: IMaintenanceLocalDataSource,
	private val server: IMaintenanceServerDataSource,
	private val mappers: MaintenanceMappersFacade
): IMaintenanceDownloader {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	override suspend fun deleteSingle(email: String, id: String) = local.deleteMaintenanceHistoryEntry(id.toLong())
	
	override fun download(email: String, vin: String): Flow<SimpleResult<Unit>> =
		server.getAllMaintenanceHistory(email, vin).transform { result ->
			logDebug(TAG, "Downloading maintenance history...")
			result.fold(
				success = { emit(local.importReplacedSpareParts(mappers.listDtoToEntities(it))) },
				failure = {
					logError(TAG, "${it.message}")
					emit(ResultState.failure(it))
				}
			)
		}
	
	
	override fun downloadSingle(email: String, vin: String, id: String): Flow<SimpleResult<Unit>> =
		server.getMaintenanceHistoryById(email, vin, id).transform { resultServer ->
			resultServer.fold(
				success = { emit(local.importReplacedSpareParts(listOf(mappers.dtoToEntity(it)))) },
				failure = { error ->
					logError(TAG, "${error.message}")
					if (error.message == FIRESTORE_NO_DOCUMENT_EXCEPTION) {
						server.deleteFromJournal(email, id).collect { emit(it) }
					}
					else emit(ResultState.failure(error))
				}
			)
		}
	
	
	override suspend fun clear(): SimpleResult<Unit> = local.clearAll()
	
}