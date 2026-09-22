package com.civicshield.simulator.platform;

public record CreateAuditEventRequest(
        String action,
        String actor,
        String details
) {
}
