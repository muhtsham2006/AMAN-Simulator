package atc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Airspace {
    // 1. Core state
    private final List<Aircraft> activeAircraft;
    private final List<AirspaceListener> listeners;
    private final ArrivalManager arrivalManager;
    private int currentSimTimeSeconds;

    // 2. Constructor
    public Airspace() {
        this.activeAircraft = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.arrivalManager = new ArrivalManager();
        this.currentSimTimeSeconds = 0;
    }

    // 3. Observer Pattern: Registration methods
    public void addListener(AirspaceListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(AirspaceListener listener) {
        listeners.remove(listener);
    }

    public void notifyListeners() {
        // Return an unmodifiable view of aircraft so observers cannot mutate our internal list directly
        List<Aircraft> unmodifiableList = Collections.unmodifiableList(this.activeAircraft);
        for (AirspaceListener listener : listeners) {
            listener.onAirspaceUpdated(unmodifiableList, this.currentSimTimeSeconds);
        }
    }

    // 4. Traffic management
    public void addAircraft(Aircraft aircraft) {
        if (aircraft != null && !activeAircraft.contains(aircraft)) {
            activeAircraft.add(aircraft);
            aircraft.recalculateETO(currentSimTimeSeconds);
            refreshSchedule();
        }
    }

    public void removeAircraft(Aircraft aircraft) {
        activeAircraft.remove(aircraft);
        refreshSchedule();
    }

    // 5. Scheduling update
    public void refreshSchedule() {
        this.arrivalManager.updateSchedule(activeAircraft, currentSimTimeSeconds);
        notifyListeners();
    }

    // 6. Getters and Setters
    public List<Aircraft> getActiveAircraft() {
        return Collections.unmodifiableList(activeAircraft);
    }

    public ArrivalManager getArrivalManager() {
        return arrivalManager;
    }

    public int getCurrentSimTimeSeconds() {
        return currentSimTimeSeconds;
    }

    public void setSimTime(int seconds) {
        this.currentSimTimeSeconds = seconds;
    }
}