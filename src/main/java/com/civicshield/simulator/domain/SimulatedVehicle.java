package com.civicshield.simulator.domain;

public class SimulatedVehicle {

    private final String vehicleId;
    private final String plate;
    private final String vin;
    private final String make;
    private final String model;

    private double positionMiles;
    private double speedMph;
    private VehicleBehavior behavior;

    public SimulatedVehicle(
            String vehicleId,
            String plate,
            String vin,
            String make,
            String model,
            double positionMiles,
            double speedMph,
            VehicleBehavior behavior
    ) {
        this.vehicleId = vehicleId;
        this.plate = plate;
        this.vin = vin;
        this.make = make;
        this.model = model;
        this.positionMiles = positionMiles;
        this.speedMph = speedMph;
        this.behavior = behavior;
    }

    public void tick(double seconds) {
        positionMiles += speedMph * (seconds / 3600.0);
    }

    public void reduceSpeed(double amount) {
        speedMph = Math.max(0, speedMph - amount);

        if (speedMph == 0) {
            behavior = VehicleBehavior.STOPPED;
        }
    }

    public void increaseSpeed(double amount) {
        speedMph = Math.min(140, speedMph + amount);
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getPlate() {
        return plate;
    }

    public String getVin() {
        return vin;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public double getPositionMiles() {
        return positionMiles;
    }

    public double getSpeedMph() {
        return speedMph;
    }

    public VehicleBehavior getBehavior() {
        return behavior;
    }

    public void setBehavior(VehicleBehavior behavior) {
        this.behavior = behavior;
    }

    public void setSpeedMph(double speedMph) {
        this.speedMph = Math.max(0, speedMph);
    }
}
