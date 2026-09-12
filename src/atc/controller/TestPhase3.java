package atc.controller;

import atc.model.Aircraft;
import atc.model.AircraftFactory;
import atc.model.Airspace;
import atc.model.AirspaceListener;
import java.util.List;

public class TestPhase3 {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- Starting Phase 3 Simulation Loop & Observer Test ---");

        // 1. Setup Model and Controller layers
        Airspace airspace = new Airspace();
        FlightController controller = new FlightController(airspace);
        SimulationClock clock = new SimulationClock(controller);

        // 2. Register an anonymous AirspaceListener (Observer)
        airspace.addListener(new AirspaceListener() {
            @Override
            public void onAirspaceUpdated(List<Aircraft> activeAircraft, int simTimeSeconds) {
                System.out.println("\n[SIM TICK] Time: " + simTimeSeconds + "s | Active Aircraft: " + activeAircraft.size());
                for (Aircraft a : activeAircraft) {
                    System.out.printf("  %s (%s) | Pos: (%.2f, %.2f) NM | Dist: %.2f NM | Fuel: %.2f min | State: %s\n",
                        a.getCallSign(), a.getModel(), a.getX(), a.getY(), 
                        a.getDistanceToRunway(), a.getFuelRemaining(), a.getState());
                }
            }
        });

        // 3. Spawn an inbound A350 heading directly South (180 deg) toward (0, 0)
        // Starting at (0, 20) NM
        Aircraft a350 = AircraftFactory.createAircraft("A350", "BAW123", 0.0, 20.0, 180.0);
        airspace.addAircraft(a350);

        // 4. Start clock at 5x simulation speed
        clock.setTimeMultiplier(5);
        System.out.println("Starting clock at 5x multiplier...");
        clock.start();

        // Let the simulation run for 3 real seconds (advancing ~15 simulation seconds)
        Thread.sleep(3200);

        // 5. Stop clock
        clock.stop();
        System.out.println("\nSimulation paused. Test completed successfully!");
        System.exit(0);
    }
}