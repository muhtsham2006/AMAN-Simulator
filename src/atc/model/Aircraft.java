package atc.model;

public class Aircraft {
    // 1. Immutable identity fields
    private final String callSign;
    private final String model;
    private final WakeCategory wakeCategory;

    // 2. Mutable telemetry & state fields
    private double x;              // Position X in Nautical Miles (relative to runway at 0,0)
    private double y;              // Position Y in Nautical Miles
    private double altitude;       // Altitude in feet
    private double speed;          // Ground speed in knots
    private double heading;        // Heading in degrees (0 - 359)
    private double fuelRemaining;  // Fuel left in minutes of flight
    private FlightState state;     // Current operational state

    // 3. AMAN Scheduling timestamps (measured in simulation seconds)
    private int estimatedTimeOverhead;
    private int targetLandingTime;

    // 4. Constructor
    public Aircraft(String callSign, String model, WakeCategory wakeCategory, 
                    double x, double y, double altitude, double speed, double heading, double fuelRemaining) {
        this.callSign = callSign;
        this.model = model;
        this.wakeCategory = wakeCategory;
        this.x = x;
        this.y = y;
        this.altitude = altitude;
        this.speed = speed;
        this.heading = heading;
        this.fuelRemaining = fuelRemaining;
        this.state = FlightState.EN_ROUTE;
        this.estimatedTimeOverhead = 0;
        this.targetLandingTime = 0;
    }

    // 5. Distance and ETO calculations
    public double getDistanceToRunway() {
        // Distance formula from (x, y) to runway origin (0, 0)
        return Math.sqrt(x * x + y * y); //standard pythagorean distance
    }

    public void recalculateETO(int currentSimTimeSeconds) {
        double distanceNM = getDistanceToRunway();
        // Time = Distance / Speed. Speed is in NM/hour, so convert to seconds (* 3600)
        if (speed > 0) {
            int flightTimeSeconds = (int) ((distanceNM / speed) * 3600);
            this.estimatedTimeOverhead = currentSimTimeSeconds + flightTimeSeconds;
        }
    }

    // 6. Getters and Setters
    public String getCallSign() { return callSign; }
    public String getModel() { return model; }
    public WakeCategory getWakeCategory() { return wakeCategory; }
    
    public double getX() { return x; }
    public void setPosition(double x, double y) { this.x = x; this.y = y; }
    
    public double getY() { return y; }
    public double getAltitude() { return altitude; }
    public void setAltitude(double altitude) { this.altitude = altitude; }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }

    public double getHeading() { return heading; }
    public void setHeading(double heading) { this.heading = heading; }

    public double getFuelRemaining() { return fuelRemaining; }
    public void consumeFuel(double minutes) { this.fuelRemaining -= minutes; }

    public FlightState getState() { return state; }
    public void setState(FlightState state) { this.state = state; }

    public int getEstimatedTimeOverhead() { return estimatedTimeOverhead; }
    public int getTargetLandingTime() { return targetLandingTime; }
    public void setTargetLandingTime(int targetLandingTime) { this.targetLandingTime = targetLandingTime; }
}