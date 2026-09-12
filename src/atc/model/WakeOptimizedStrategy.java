package atc.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WakeOptimizedStrategy implements SeparationStrategy {

    @Override
    public List<Aircraft> calculateSchedule(List<Aircraft> aircraftList, int currentSimTimeSeconds) {
        List<Aircraft> scheduledList = new ArrayList<>(aircraftList);

        if (scheduledList.isEmpty()) {
            return scheduledList;
        }

        // 1. Sort primarily by time windows (e.g., 300s buckets), 
        //    and secondarily by Wake Category ordinal (SUPER=0, HEAVY=1, MEDIUM=2, LIGHT=3)
        //    to minimize alternating wake turbulence penalties.
        scheduledList.sort(
            Comparator.comparingInt((Aircraft a) -> a.getEstimatedTimeOverhead() / 300)
                      .thenComparingInt(a -> a.getWakeCategory().ordinal())
                      .thenComparingInt(Aircraft::getEstimatedTimeOverhead)
        );

        // 2. Schedule the first aircraft
        Aircraft firstAircraft = scheduledList.get(0);
        int initialLandingTime = Math.max(firstAircraft.getEstimatedTimeOverhead(), currentSimTimeSeconds);
        firstAircraft.setTargetLandingTime(initialLandingTime);

        // 3. Sequentially calculate target landing times with safety buffers
        for (int i = 1; i < scheduledList.size(); i++) {
            Aircraft leader = scheduledList.get(i - 1);
            Aircraft trailer = scheduledList.get(i);

            int requiredGap = leader.getWakeCategory().getRequiredSeparation(trailer.getWakeCategory());
            int earliestAllowedTime = leader.getTargetLandingTime() + requiredGap;

            int assignedTargetTime = Math.max(trailer.getEstimatedTimeOverhead(), earliestAllowedTime);
            trailer.setTargetLandingTime(assignedTargetTime);
        }

        return scheduledList;
    }
}