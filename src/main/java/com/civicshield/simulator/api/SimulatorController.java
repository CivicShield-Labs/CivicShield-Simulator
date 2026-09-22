package com.civicshield.simulator.api;

import com.civicshield.simulator.domain.SimulatedVehicle;
import com.civicshield.simulator.engine.SimulationEngine;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/simulator")
public class SimulatorController {

    private final SimulationEngine simulationEngine;

    public SimulatorController(SimulationEngine simulationEngine) {
        this.simulationEngine = simulationEngine;
    }

    @GetMapping("/status")
    public SimulatorStatusResponse status() {
        return new SimulatorStatusResponse(
                simulationEngine.isRunning(),
                simulationEngine.getTick(),
                simulationEngine.getVehicles().size(),
                simulationEngine.getIncidentId()
        );
    }

    @GetMapping("/vehicles")
    public List<SimulatedVehicle> vehicles() {
        return simulationEngine.getVehicles();
    }

    @PostMapping("/start")
    public SimulatorStatusResponse start() {
        simulationEngine.start();
        return status();
    }

    @PostMapping("/stop")
    public SimulatorStatusResponse stop() {
        simulationEngine.stop();
        return status();
    }

    @PostMapping("/reset")
    public SimulatorStatusResponse reset() {
        simulationEngine.reset();
        return status();
    }

    @PostMapping("/vehicles/{vehicleId}/slowdown")
    public SimulatedVehicle slowdown(@PathVariable String vehicleId) {
        try {
            simulationEngine.requestSimulatedSlowdown(vehicleId);

            return simulationEngine.getVehicles()
                    .stream()
                    .filter(v -> v.getVehicleId().equals(vehicleId))
                    .findFirst()
                    .orElseThrow();
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    ex.getMessage()
            );
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    ex.getMessage()
            );
        }
    }
}
