package atc.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FCFSSeparationStrategy implements SeparationStrategy {

    @Override
    public List<Aircraft> calculateSchedule(List<Aircraft> aircraftList, int currentSimTimeSeconds) {
        // 1. Create a shallow copy of the list to avoid mutating the original collection unexpectedly
        List<Aircraft> scheduledList = new ArrayList<>(aircraftList);

        // 2. Sort aircraft strictly by their Estimated Time Overhead (ETO) ascending
        scheduledList.sort(Comparator.comparingInt(Aircraft::getEstimatedTimeOverhead));

        // If the list is empty, return early
        if (scheduledList.isEmpty()) {
            return scheduledList;
        }

        // 3. Process the first aircraft: target landing time is at least its ETO
        Aircraft firstAircraft = scheduledList.get(0);
        int initialLandingTime = Math.max(firstAircraft.getEstimatedTimeOverhead(), currentSimTimeSeconds);
        firstAircraft.setTargetLandingTime(initialLandingTime);

        // 4. Sequentially process remaining aircraft and enforce wake turbulence separation
        for (int i = 1; i < scheduledList.size(); i++) {
            Aircraft leader = scheduledList.get(i - 1);
            Aircraft trailer = scheduledList.get(i);

            // Look up required safety gap (in seconds) between leader and trailer
            int requiredGap = leader.getWakeCategory().getRequiredSeparation(trailer.getWakeCategory());

            // The earliest this aircraft is legally allowed to touch down behind the leader
            int earliestAllowedTime = leader.getTargetLandingTime() + requiredGap;

            // Target time is the max of when it naturally arrives vs when it is legally allowed
            int assignedTargetTime = Math.max(trailer.getEstimatedTimeOverhead(), earliestAllowedTime);
            trailer.setTargetLandingTime(assignedTargetTime);
        }

        return scheduledList;
    }
}