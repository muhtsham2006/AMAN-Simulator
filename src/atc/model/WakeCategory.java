package atc.model;

public enum WakeCategory {

    //(behind SUPER, behind HEAVY, behind MEDIUM, behind LIGHT) seperation in seconds
    SUPER(60, 90, 120, 150),
    HEAVY(60, 60, 90, 120),
    MEDIUM(60, 60, 60, 90),
    LIGHT(60, 60, 60, 60);

    //private immutable fields to hold these values
    private final int behindSuper;
    private final int behindHeavy;
    private final int behindMedium;
    private final int behindLight;
    
    //enum constructor (runs once for each constant above when initialized)
    private WakeCategory(int behindSuper, int behindHeavy, int behindMedium, int behindLight) {
        this.behindSuper = behindSuper;
        this.behindHeavy = behindHeavy;
        this.behindMedium = behindMedium;
        this.behindLight = behindLight;
    }
    
    //Method to look up required gap in seconds given a trailing aircraft
    public int getRequiredSeparation(WakeCategory trailingAircraft) {
        switch (trailingAircraft) {
            case SUPER: return this.behindSuper;
            case HEAVY: return this.behindHeavy;
            case MEDIUM: return this.behindMedium;
            case LIGHT: return this.behindLight;
            default:    return 60; //Minimum default runway rule
        }
    }

}
