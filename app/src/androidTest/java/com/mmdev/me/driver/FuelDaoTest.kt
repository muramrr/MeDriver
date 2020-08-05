package com.mmdev.me.driver

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mmdev.me.driver.data.datasource.local.fuel.dao.FuelDao
import com.mmdev.me.driver.modules.roomTestModule
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.test.KoinTest
import org.koin.test.inject

/**
 * KoinTest help inject Koin components from actual runtime
 */

@RunWith(AndroidJUnit4::class)
class FuelDaoTest : KoinTest {

	/*
	 * Inject needed components from Koin
	 */
	private val fuelDao: FuelDao by inject()

	//another approach without KoinTest
//	private lateinit var fuelDatabase: fuelRoomDatabase //the db instance
//	private lateinit var fuelDao: fuelDao //the dao
//
//	@Before
//	fun setUp() {
//		val context = ApplicationProvider.getApplicationContext<Context>()
//		fuelDatabase = Room.inMemoryDatabaseBuilder(context, fuelRoomDatabase::class.java)
//			.build()
//
//		fuelDao = fuelDatabase.habitDao()
//	}

	/**
	 * Override default Koin configuration to use Room in-memory database
	 */
	@Before
	fun before() {
		loadKoinModules(roomTestModule)
	}

	@Test
	fun testInsertPlace() = runBlocking {

		// Create Fuel place entity
		val fuelEntity = FuelEntity()

		// Insert entity

		fuelDao.insertFuelModel(FuelEntity)
		// Request one entity per id
		val requestedEntities = fuelDao.getAllFuelModels()

		// compare result
		assertEquals(listOf(FuelEntity), requestedEntities)
		assertTrue(fuelDao.getAllFuelModels().isEmpty())
	}

	@Test
	fun testInsertEvent() = runBlocking {

		// Create Fuel event entity
		val fuelEntity = FuelEntity()

		// Insert entity

		fuelDao.insertFuelModel(FuelEntity)
		// Request one entity per id
		val requestedEntities = fuelDao.getAllFuelModels()

		// compare result
		assertEquals(listOf(FuelEntity), requestedEntities)
		assertTrue(fuelDao.getAllFuelModels().isEmpty())
	}

	@Test
	fun testDeleteFuel() = runBlocking {

		// Create casual entity
		val fuelEntity = FuelEntity()

		// Save entities
		fuelDao.insertFuelModel(FuelEntity)

		// compare result
		assertEquals(listOf(FuelEntity), fuelDao.getAllFuelModels())
		assertTrue(fuelDao.getAllFuelModels().isEmpty())

		fuelDao.deleteAll()


		// compare result
		assertEquals(emptyList<FuelEntity>(), fuelDao.getAllFuelModels())
	}

	/**
	 * Close resources
	 */
	@After
	fun after() {
		//fuelDatabase.close()
		//stopKoin()
	}
}