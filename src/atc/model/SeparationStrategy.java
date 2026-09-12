package atc.model;

import java.util.List;

public interface SeparationStrategy {
    /**
     * Calculates and assigns target landing times (TT) for a list of aircraft.
     * 
     * @param aircraftList The active incoming aircraft to sequence.
     * @param currentSimTimeSeconds The current simulation time in seconds.
     * @return The sequenced list of aircraft with updated targetLandingTimes.
     */
    List<Aircraft> calculateSchedule(List<Aircraft> aircraftList, int currentSimTimeSeconds);
}