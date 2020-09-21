/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 21.09.2020 17:40
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.maintenance

import com.mmdev.me.driver.data.core.base.BaseRepository
import com.mmdev.me.driver.data.datasource.maintenance.local.IMaintenanceLocalDataSource
import com.mmdev.me.driver.data.datasource.maintenance.remote.IMaintenanceRemoteDataSource
import com.mmdev.me.driver.data.repository.maintenance.mappers.MaintenanceMappersFacade
import com.mmdev.me.driver.domain.maintenance.IMaintenanceRepository

/**
 * [IMaintenanceRepository] implementation
 */

class MaintenanceRepositoryImpl(
	private val localDataSource: IMaintenanceLocalDataSource,
	private val remoteDataSource: IMaintenanceRemoteDataSource,
	private val mappers: MaintenanceMappersFacade
): IMaintenanceRepository, BaseRepository()