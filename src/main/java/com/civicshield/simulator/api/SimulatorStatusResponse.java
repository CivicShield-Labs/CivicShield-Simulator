package com.civicshield.simulator.api;

public record SimulatorStatusResponse(
        boolean running,
        long tick,
        int vehicleCount,
        String incidentId
) {
}
