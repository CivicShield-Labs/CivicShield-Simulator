package com.civicshield.simulator.engine;

import com.civicshield.simulator.domain.SimulatedVehicle;
import com.civicshield.simulator.domain.VehicleBehavior;
import com.civicshield.simulator.platform.PlatformClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SimulationEngine {

    private final PlatformClient platformClient;

    private final List<SimulatedVehicle> vehicles = new ArrayList<>();
    private final AtomicLong tick = new AtomicLong();

    private volatile boolean running;
    private volatile String incidentId;

    public SimulationEngine(PlatformClient platformClient) {
        this.platformClient = platformClient;
        reset();
    }

    public synchronized void start() {
        if (running) {
            return;
        }

        if (vehicles.isEmpty()) {
            reset();
        }

        incidentId = platformClient.createSimulationIncident();
        running = true;

        platformClient.addEvent(
                incidentId,
                "SIMULATION_STARTED",
                "Traffic simulation started with " + vehicles.size() + " vehicles."
        );
    }

    public synchronized void stop() {
        if (!running) {
            return;
        }

        running = false;

        platformClient.addEvent(
                incidentId,
                "SIMULATION_STOPPED",
                "Simulation stopped at tick " + tick.get() + "."
        );
    }

    public synchronized void reset() {
        running = false;
        tick.set(0);
        incidentId = null;

        vehicles.clear();

        vehicles.add(vehicle(
                "SIM-001",
                "CSL001",
                "SIMVIN00000000001",
                "Toyota",
                "Camry",
                0.2,
                63,
                VehicleBehavior.NORMAL
        ));

        vehicles.add(vehicle(
                "SIM-002",
                "CSL002",
                "SIMVIN00000000002",
                "Honda",
                "Accord",
                0.6,
                66,
                VehicleBehavior.NORMAL
        ));

        vehicles.add(vehicle(
                "SIM-003",
                "CSL003",
                "SIMVIN00000000003",
                "Ford",
                "Explorer",
                1.1,
                82,
                VehicleBehavior.SPEEDING
        ));

        vehicles.add(vehicle(
                "SIM-004",
                "CSL004",
                "SIMVIN00000000004",
                "Chevrolet",
                "Tahoe",
                1.7,
                101,
                VehicleBehavior.FLEEING
        ));

        vehicles.add(vehicle(
                "SIM-005",
                "CSL005",
                "SIMVIN00000000005",
                "BMW",
                "X3",
                2.0,
                65,
                VehicleBehavior.NORMAL
        ));
    }

    private SimulatedVehicle vehicle(
            String id,
            String plate,
            String vin,
            String make,
            String model,
            double position,
            double speed,
            VehicleBehavior behavior
    ) {
        return new SimulatedVehicle(
                id,
                plate,
                vin,
                make,
                model,
                position,
                speed,
                behavior
        );
    }

    @Scheduled(fixedRate = 1000)
    public synchronized void simulationTick() {
        if (!running) {
            return;
        }

        long currentTick = tick.incrementAndGet();

        for (SimulatedVehicle vehicle : vehicles) {

            if (vehicle.getBehavior() == VehicleBehavior.FLEEING) {
                if (currentTick % 3 == 0 && vehicle.getSpeedMph() < 120) {
                    vehicle.increaseSpeed(1.5);
                }
            }

            vehicle.tick(1.0);
        }

        if (currentTick % 10 == 0) {
            SimulatedVehicle fleeing = vehicles.stream()
                    .filter(v -> v.getBehavior() == VehicleBehavior.FLEEING)
                    .findFirst()
                    .orElse(null);

            if (fleeing != null) {
                platformClient.addEvent(
                        incidentId,
                        "FLEEING_VEHICLE_TELEMETRY",
                        "vehicle=" + fleeing.getVehicleId()
                                + ", plate=" + fleeing.getPlate()
                                + ", speedMph=" + Math.round(fleeing.getSpeedMph())
                                + ", positionMiles="
                                + String.format("%.2f", fleeing.getPositionMiles())
                );
            }
        }
    }

    public synchronized void requestSimulatedSlowdown(String vehicleId) {
        SimulatedVehicle vehicle = findVehicle(vehicleId);

        if (vehicle.getBehavior() != VehicleBehavior.FLEEING) {
            throw new IllegalStateException(
                    "Controlled slowdown simulation is only enabled for the designated fleeing test vehicle."
            );
        }

        platformClient.addEvent(
                incidentId,
                "SIMULATED_SLOWDOWN_AUTHORIZED",
                "Human-authorized simulated slowdown requested for " + vehicleId + "."
        );

        vehicle.reduceSpeed(20);

        if (vehicle.getSpeedMph() <= 15) {
            vehicle.setSpeedMph(0);
            vehicle.setBehavior(VehicleBehavior.STOPPED);

            platformClient.addEvent(
                    incidentId,
                    "SIMULATED_VEHICLE_STOPPED",
                    "Vehicle " + vehicleId + " safely stopped in simulation."
            );
        }
    }

    public synchronized List<SimulatedVehicle> getVehicles() {
        return new ArrayList<>(vehicles);
    }

    public long getTick() {
        return tick.get();
    }

    public boolean isRunning() {
        return running;
    }

    public String getIncidentId() {
        return incidentId;
    }

    private SimulatedVehicle findVehicle(String vehicleId) {
        return vehicles.stream()
                .filter(v -> v.getVehicleId().equals(vehicleId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Vehicle not found: " + vehicleId
                ));
    }
}
