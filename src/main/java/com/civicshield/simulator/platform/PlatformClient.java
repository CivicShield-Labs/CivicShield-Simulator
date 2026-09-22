package com.civicshield.simulator.platform;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PlatformClient {

    private final RestClient restClient;

    public PlatformClient(
            @Value("${civicshield.platform.base-url}") String baseUrl,
            @Value("${civicshield.platform.username}") String username,
            @Value("${civicshield.platform.password}") String password
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .build();
    }

    public String createSimulationIncident() {
        try {
            IncidentResponse response = restClient.post()
                    .uri("/api/incidents")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new CreateIncidentRequest(
                            "civicshield-simulator",
                            "TRAFFIC_SAFETY_SIMULATION"
                    ))
                    .retrieve()
                    .body(IncidentResponse.class);

            return response != null ? response.id() : null;
        } catch (Exception ex) {
            System.err.println("Platform unavailable: " + ex.getMessage());
            return null;
        }
    }

    public void addEvent(
            String incidentId,
            String action,
            String details
    ) {
        if (incidentId == null || incidentId.isBlank()) {
            return;
        }

        try {
            restClient.post()
                    .uri("/api/incidents/{id}/events", incidentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new CreateAuditEventRequest(
                            action,
                            "civicshield-simulator",
                            details
                    ))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ex) {
            System.err.println("Could not send event to platform: " + ex.getMessage());
        }
    }
}
