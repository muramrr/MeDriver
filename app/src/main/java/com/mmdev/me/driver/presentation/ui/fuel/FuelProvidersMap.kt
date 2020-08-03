package com.mmdev.me.driver.presentation.ui.fuel

import com.mmdev.me.driver.R

/**
 * This is the documentation block about the class
 */

object FuelProvidersMap {


	internal val fuelProvidersSlug = listOf(
			"amic",
			"anp",
			"brsm-nafta",
			"glusco",
			"klo",
			"okko",
			"socar",
			"shell",
			"ukrnafta",
			"wog"
	)

	internal val fuelProvidersIcons = listOf(
			R.drawable.gas_station_amic,
			R.drawable.gas_station_anp,
			R.drawable.gas_station_brsm,
			R.drawable.gas_station_glusco,
			R.drawable.gas_station_klo,
			R.drawable.gas_station_okko,
			R.drawable.gas_station_socar,
			R.drawable.gas_station_shell,
			R.drawable.gas_station_ukrnafta,
			R.drawable.gas_station_wog
	)

	internal val fuelProvidersMap = fuelProvidersSlug.zip(fuelProvidersIcons).toMap()

}