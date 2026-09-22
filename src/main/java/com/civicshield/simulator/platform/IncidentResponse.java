package com.civicshield.simulator.platform;

public record IncidentResponse(
        String id,
        String agencyId,
        String incidentType,
        String status,
        String createdAt
) {
}
