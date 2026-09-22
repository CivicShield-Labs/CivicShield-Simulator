package com.civicshield.simulator.engine;

import com.civicshield.simulator.domain.SimulatedVehicle;
import com.civicshield.simulator.domain.VehicleBehavior;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulatedVehicleTest {

    @Test
    void vehicleMovesBasedOnSpeed() {
        SimulatedVehicle vehicle = new SimulatedVehicle(
                "SIM-TEST",
                "TEST001",
                "SIMVINTEST00000001",
                "Test",
                "Vehicle",
                0,
                60,
                VehicleBehavior.NORMAL
        );

        vehicle.tick(60);

        assertEquals(1.0, vehicle.getPositionMiles(), 0.001);
    }

    @Test
    void slowdownCannotProduceNegativeSpeed() {
        SimulatedVehicle vehicle = new SimulatedVehicle(
                "SIM-TEST",
                "TEST001",
                "SIMVINTEST00000001",
                "Test",
                "Vehicle",
                0,
                10,
                VehicleBehavior.FLEEING
        );

        vehicle.reduceSpeed(50);

        assertEquals(0, vehicle.getSpeedMph());
        assertEquals(VehicleBehavior.STOPPED, vehicle.getBehavior());
    }
}
