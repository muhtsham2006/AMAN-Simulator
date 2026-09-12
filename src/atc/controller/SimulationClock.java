package atc.controller;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimulationClock {
    private final FlightController flightController;
    private ScheduledExecutorService scheduler;
    private boolean isRunning;
    private int timeMultiplier; // 1 = real-time (1s sim = 1s real), 2 = 2x speed, etc.

    public SimulationClock(FlightController flightController) {
        this.flightController = flightController;
        this.isRunning = false;
        this.timeMultiplier = 1;
    }

    /**
     * Starts the simulation clock ticking once per second.
     */
    public synchronized void start() {
        if (isRunning) {
            return;
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();
        isRunning = true;

        // Schedule a fixed-rate tick every 1000 milliseconds
        scheduler.scheduleAtFixedRate(() -> {
            try {
                // Advance airspace by (1 second * timeMultiplier)
                flightController.updateAirspace(1 * timeMultiplier);
            } catch (Exception e) {
                System.err.println("Error during simulation tick: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * Pauses the simulation clock.
     */
    public synchronized void pause() {
        if (!isRunning) {
            return;
        }

        isRunning = false;
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    /**
     * Shuts down the thread pool completely.
     */
    public synchronized void stop() {
        pause();
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    public synchronized void setTimeMultiplier(int multiplier) {
        if (multiplier > 0) {
            this.timeMultiplier = multiplier;
        }
    }

    public synchronized int getTimeMultiplier() {
        return timeMultiplier;
    }
}