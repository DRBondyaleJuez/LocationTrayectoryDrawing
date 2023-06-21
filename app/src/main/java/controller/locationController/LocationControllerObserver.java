package controller.locationController;

/**
 * Provides an interface representing one of the components of an observer-observable design pattern. This component
 * represents an object that can observe this facilitates triggers and interactions caused by objects which implement
 * the observable equivalent interface.
 */
public interface LocationControllerObserver {

    /**
     * @param currentLatitude
     * @param currentLongitude
     * @param currentAltitude
     * @param currentSpeed
     */
    void setLocationParameters(double currentLatitude, double currentLongitude, Double currentAltitude, Float currentSpeed);

    /**
     * @param currentLatitude
     * @param currentLongitude
     */
    void calculateAndSetDirection(double currentLatitude, double currentLongitude);
}
