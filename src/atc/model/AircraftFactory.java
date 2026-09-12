package atc.model;

public class AircraftFactory {

    public static Aircraft createAircraft(String modelType, String callSign, double x, double y, double heading) {
        // Standard initial altitude (feet) and fuel reserve (minutes) for sector entries
        double entryAltitude = 10000.0;
        double defaultFuelMinutes = 60.0;

        switch (modelType.toUpperCase()) {
            case "A380":
                return new Aircraft(
                    callSign,
                    "Airbus A380-800",
                    WakeCategory.SUPER,
                    x, y,
                    entryAltitude,
                    280.0, // Typical terminal entry speed in knots
                    heading,
                    defaultFuelMinutes
                );

            case "A350":
                return new Aircraft(
                    callSign,
                    "Airbus A350-1000",
                    WakeCategory.HEAVY,
                    x, y,
                    entryAltitude,
                    290.0,
                    heading,
                    defaultFuelMinutes
                );

            case "B777":
                return new Aircraft(
                    callSign,
                    "Boeing 777-300ER",
                    WakeCategory.HEAVY,
                    x, y,
                    entryAltitude,
                    290.0,
                    heading,
                    defaultFuelMinutes
                );

            case "A320":
                return new Aircraft(
                    callSign,
                    "Airbus A320-200",
                    WakeCategory.MEDIUM,
                    x, y,
                    entryAltitude,
                    250.0,
                    heading,
                    defaultFuelMinutes
                );

            case "C172":
                return new Aircraft(
                    callSign,
                    "Cessna 172 Skyhawk",
                    WakeCategory.LIGHT,
                    x, y,
                    3000.0, // Lower altitude for light general aviation
                    110.0,  // Slower speed
                    heading,
                    defaultFuelMinutes
                );

            default:
                // Fallback default aircraft configuration
                return new Aircraft(
                    callSign,
                    "Generic Medium Transport",
                    WakeCategory.MEDIUM,
                    x, y,
                    entryAltitude,
                    250.0,
                    heading,
                    defaultFuelMinutes
                );
        }
    }
}