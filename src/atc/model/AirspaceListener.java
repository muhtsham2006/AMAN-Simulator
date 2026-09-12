package atc.model;

import java.util.List;

public interface AirspaceListener {
    /**
     * Fired every time the simulation ticks and aircraft positions/states update.
     * 
     * @param activeAircraft The current list of airborne aircraft.
     * @param simTimeSeconds The current simulation time in elapsed seconds.
     */
    void onAirspaceUpdated(List<Aircraft> activeAircraft, int simTimeSeconds);
}