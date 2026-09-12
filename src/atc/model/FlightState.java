package atc.model;

public enum FlightState {
    EN_ROUTE, //flying inbound along standard arrival route toward the sector
    HOLDING, //flying a 4 min loop at a waypoint to burn off delay
    APPROACH, //cleared for final descent toward runway threshold
    LANDED //touched down and exited active scheduling

    //above are the 4 fixed lifecycle states an aircraft can occupy during simulation
    
}
