package controller.locationController;

/**
 * Provides an interface representing one of the components of an observer-observable design pattern. This component
 * represents an object that can observe this facilitates triggers and interactions caused by objects which implement
 * the observable equivalent interface.
 */
public interface LocationControllerObserver {

    /**
     * Receive the parameters obtained from the device's GPS, store them and submit them to the viewController
     * @param currentLatitude Double recently retrieve latitude
     * @param currentLongitude Double recently retrieve longitude
     * @param currentAltitude Double recently retrieve altitude
     * @param currentSpeed Float recently retrieve speed
     */
    void setLocationParameters(double currentLatitude, double currentLongitude, Double currentAltitude, Float currentSpeed);

}
