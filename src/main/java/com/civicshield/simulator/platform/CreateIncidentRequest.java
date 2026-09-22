package com.civicshield.simulator.platform;

public record CreateIncidentRequest(
        String agencyId,
        String incidentType
) {
}
