/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 17.11.2020 16:41
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.ui.maintenance

import com.mmdev.me.driver.R
import com.mmdev.me.driver.domain.maintenance.data.components.base.VehicleSystemNodeType

/**
 *
 */

object VehicleSystemNodeConstants {
	
	val engineComponents = intArrayOf(
		R.string.maintenance_engine_mount,
		R.string.maintenance_engine_silencer_corrugation,
		R.string.maintenance_engine_timing_chain_kit,
		R.string.maintenance_engine_cylinder_unit_heads,
		R.string.maintenance_engine_cylinder_unit_heads_gasket,
		R.string.maintenance_engine_cylinder_head_bolts,
		R.string.maintenance_engine_cylinder,
		R.string.maintenance_engine_cylinder_shell,
		R.string.maintenance_engine_cylinder_unit_liner,
		R.string.maintenance_engine_cylinder_unit,
		R.string.maintenance_engine_belt_generator,
		R.string.maintenance_engine_belt_air_conditioner,
		R.string.maintenance_engine_turbo,
		R.string.maintenance_engine_intercooler,
		R.string.maintenance_engine_compressor,
		R.string.maintenance_engine_belt_compressor,
		R.string.maintenance_engine_piston_rings,
		R.string.maintenance_engine_piston,
		R.string.maintenance_engine_pump_fuel,
		R.string.maintenance_engine_fuel_burner,
		R.string.maintenance_engine_belt_pump_fuel,
		R.string.maintenance_engine_pipe_fuel,
		R.string.maintenance_engine_radiator,
		R.string.maintenance_engine_pump_water,
		R.string.maintenance_engine_damper,
		R.string.maintenance_engine_valve,
		R.string.maintenance_engine_roller,
		R.string.maintenance_engine_connecting_rod,
		R.string.maintenance_engine_camshaft,
		R.string.maintenance_engine_crankshaft,
		R.string.maintenance_other_component
	)
	
	val transmissionComponents = intArrayOf(
		R.string.maintenance_transmission_speed_sensor,
		R.string.maintenance_transmission_clutch_kit,
		R.string.maintenance_transmission_clutch_bearing,
		R.string.maintenance_transmission_clutch_flywheel,
		R.string.maintenance_transmission_homokinetic_joint,
		R.string.maintenance_transmission_homokinetic_joint_duster,
		R.string.maintenance_transmission_cross_steering,
		R.string.maintenance_transmission_cross_gear,
		R.string.maintenance_transmission_semiaxis,
		R.string.maintenance_other_component
	)
	
	val electricsComponents = intArrayOf(
		R.string.maintenance_electrics_battery,
		R.string.maintenance_electrics_generator,
		R.string.maintenance_electrics_ignition_coil,
		R.string.maintenance_electrics_starter,
		R.string.maintenance_electrics_lambda_probe,
		R.string.maintenance_electrics_high_voltage_wire,
		R.string.maintenance_other_component
	)
	
	val suspensionComponents = intArrayOf(
		R.string.maintenance_suspension_shock_absorbers_front,
		R.string.maintenance_suspension_shock_absorbers_back,
		R.string.maintenance_suspension_shock_absorber_support,
		R.string.maintenance_suspension_spring,
		R.string.maintenance_suspension_silentblock,
		R.string.maintenance_suspension_spherical_bearing,
		R.string.maintenance_suspension_wheel_hub,
		R.string.maintenance_suspension_wheel_hub_bearing,
		R.string.maintenance_suspension_sleeve,
		R.string.maintenance_suspension_arm,
		R.string.maintenance_suspension_rack_stabilizer,
		R.string.maintenance_other_component
	)
	
	val brakesComponents = intArrayOf(
		R.string.maintenance_brakes_pads_discs_drums_front,
		R.string.maintenance_brakes_pads_discs_drums_back,
		R.string.maintenance_brakes_caliper,
		R.string.maintenance_brakes_pipe,
		R.string.maintenance_brakes_cylinder,
		R.string.maintenance_brakes_sensor_abs,
		R.string.maintenance_brakes_sensor_deterioration,
		R.string.maintenance_brakes_parking_cable,
		R.string.maintenance_other_component
	)
	
	val bodyComponents = intArrayOf(
		R.string.maintenance_body_wiper_front,
		R.string.maintenance_body_wiper_back,
		R.string.maintenance_body_windshield_front,
		R.string.maintenance_body_windshield_back,
		R.string.maintenance_body_hood,
		R.string.maintenance_body_hood_damper,
		R.string.maintenance_body_trunk_damper,
		R.string.maintenance_body_headlight_left,
		R.string.maintenance_body_headlight_right,
		R.string.maintenance_body_taillight_left,
		R.string.maintenance_body_taillight_right,
		R.string.maintenance_body_foglight_left,
		R.string.maintenance_body_foglight_right,
		R.string.maintenance_body_radiator_grill,
		R.string.maintenance_body_front_bumper_grill,
		R.string.maintenance_body_front_bumper,
		R.string.maintenance_body_back_bumper,
		R.string.maintenance_body_front_left_fender,
		R.string.maintenance_body_front_right_fender,
		R.string.maintenance_body_back_left_fender,
		R.string.maintenance_body_back_right_fender,
		R.string.maintenance_body_sill_left,
		R.string.maintenance_body_sill_right,
		R.string.maintenance_body_washer_reservoir,
		R.string.maintenance_other_component
	)
	
	val otherComponents = IntArray(0)
	
	val plannedComponents = intArrayOf(
		R.string.maintenance_planned_insurance,
		R.string.maintenance_planned_air_filter,
		R.string.maintenance_planned_engine_oil_filter,
		R.string.maintenance_planned_fuel_filter,
		R.string.maintenance_planned_cabin_filter,
		R.string.maintenance_planned_brakes_fluid,
		R.string.maintenance_planned_grm_kit,
		R.string.maintenance_planned_plugs,
		R.string.maintenance_planned_antifreeze,
		R.string.maintenance_planned_transmission_oil,
		R.string.maintenance_planned_transmission_filter,
	)
	
	
	
	val childrenMap: Map<VehicleSystemNodeType, IntArray> = mapOf(
		VehicleSystemNodeType.ENGINE to engineComponents,
		VehicleSystemNodeType.TRANSMISSION to transmissionComponents,
		VehicleSystemNodeType.ELECTRICS to electricsComponents,
		VehicleSystemNodeType.SUSPENSION to suspensionComponents,
		VehicleSystemNodeType.BRAKES to brakesComponents,
		VehicleSystemNodeType.BODY to bodyComponents,
		VehicleSystemNodeType.OTHER to otherComponents,
		VehicleSystemNodeType.PLANNED to plannedComponents
	)
	
	
}