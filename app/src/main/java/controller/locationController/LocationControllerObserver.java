package controller.locationController;

public interface LocationControllerObserver {
    void setLocationParameters(double currentLatitude, double currentLongitude, Double currentAltitude, Float currentSpeed);
    void calculateAndSetDirection(double currentLatitude, double currentLongitude);
}
