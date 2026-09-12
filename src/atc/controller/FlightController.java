package atc.controller;

import atc.model.Aircraft;
import atc.model.Airspace;
import atc.model.FlightState;
import java.util.ArrayList;
import java.util.List;

public class FlightController {
    private final Airspace airspace;

    public FlightController(Airspace airspace) {
        this.airspace = airspace;
    }

    /**
     * Advances simulation physics and ATC tactical logic by dtSeconds.
     */
    public void updateAirspace(int dtSeconds) {
        int currentSimTime = airspace.getCurrentSimTimeSeconds() + dtSeconds;
        airspace.setSimTime(currentSimTime);

        List<Aircraft> landedAircraft = new ArrayList<>();

        for (Aircraft aircraft : airspace.getActiveAircraft()) {
            // 1. Consume fuel (dtSeconds converted to minutes)
            aircraft.consumeFuel(dtSeconds / 60.0);

            // 2. Tactical AMAN control based on scheduled delay
            applyTacticalSeparation(aircraft);

            // 3. Update spatial physics
            updatePosition(aircraft, dtSeconds);

            // 4. Update ETO based on new position
            aircraft.recalculateETO(currentSimTime);

            // 5. Evaluate state transitions and landing detection
            double distance = aircraft.getDistanceToRunway();
            if (distance <= 1.0) {
                aircraft.setState(FlightState.LANDED);
                landedAircraft.add(aircraft);
            } else if (distance <= 12.0 && aircraft.getState() != FlightState.LANDED) {
                aircraft.setState(FlightState.APPROACH);
                // Turn inbound toward the runway origin (0, 0)
                double inboundHeading = Math.toDegrees(Math.atan2(-aircraft.getX(), -aircraft.getY()));
                if (inboundHeading < 0) inboundHeading += 360;
                aircraft.setHeading(inboundHeading);
            }
        }

        // 6. Remove safely landed aircraft and broadcast state update
        for (Aircraft landed : landedAircraft) {
            airspace.removeAircraft(landed);
        }

        airspace.refreshSchedule();
    }

    private void applyTacticalSeparation(Aircraft aircraft) {
        if (aircraft.getState() == FlightState.LANDED) {
            return;
        }

        int delay = aircraft.getTargetLandingTime() - aircraft.getEstimatedTimeOverhead();

        if (delay > 180 && aircraft.getState() == FlightState.EN_ROUTE) {
            // Significant delay: Enter holding pattern to absorb delay
            aircraft.setState(FlightState.HOLDING);
        } else if (delay <= 60 && aircraft.getState() == FlightState.HOLDING) {
            // Delay absorbed: Exit holding and resume inbound track
            aircraft.setState(FlightState.EN_ROUTE);
        } else if (delay > 0 && delay <= 180 && aircraft.getState() == FlightState.EN_ROUTE) {
            // Moderate delay: Reduce speed toward minimum clean speed (210 kts)
            if (aircraft.getSpeed() > 210.0) {
                aircraft.setSpeed(Math.max(210.0, aircraft.getSpeed() - 5.0));
            }
        }
    }

    private void updatePosition(Aircraft aircraft, int dtSeconds) {
        if (aircraft.getState() == FlightState.HOLDING) {
            // Fly a standard rate-one holding turn (3 degrees per second)
            double newHeading = (aircraft.getHeading() + (3.0 * dtSeconds)) % 360;
            aircraft.setHeading(newHeading);
        }

        // Convert heading (degrees clockwise from North) to standard cartesian radians
        double headingRad = Math.toRadians(aircraft.getHeading());

        // Speed in knots = Nautical Miles per hour -> convert to NM per second
        double speedNMS = aircraft.getSpeed() / 3600.0;
        double distanceTravelled = speedNMS * dtSeconds;

        // Aviation heading convention: 000 is +Y (North), 090 is +X (East)
        double dx = distanceTravelled * Math.sin(headingRad);
        double dy = distanceTravelled * Math.cos(headingRad);

        aircraft.setPosition(aircraft.getX() + dx, aircraft.getY() + dy);
    }
}