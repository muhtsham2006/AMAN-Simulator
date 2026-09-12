package atc.model;

import java.util.Collections;
import java.util.List;

public class ArrivalManager {
    // 1. The Strategy reference (polymorphism via the interface)
    private SeparationStrategy strategy;

    // 2. Constructor defaults to standard FCFS strategy
    public ArrivalManager() {
        this.strategy = new FCFSSeparationStrategy();
    }

    // Constructor to inject a specific strategy upon creation
    public ArrivalManager(SeparationStrategy initialStrategy) {
        this.strategy = initialStrategy;
    }

    // 3. Setter allowing dynamic algorithm switching at runtime
    public void setStrategy(SeparationStrategy strategy) {
        this.strategy = strategy;
    }

    public SeparationStrategy getStrategy() {
        return strategy;
    }

    // 4. Delegation method to execute scheduling calculations
    public List<Aircraft> updateSchedule(List<Aircraft> activeAircraft, int currentSimTimeSeconds) {
        if (activeAircraft == null || activeAircraft.isEmpty()) {
            return Collections.emptyList();
        }
        return this.strategy.calculateSchedule(activeAircraft, currentSimTimeSeconds);
    }
}