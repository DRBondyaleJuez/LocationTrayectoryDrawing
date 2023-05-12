package controller.locationController;

public interface LocationControllerObserver {
    public void setLocationParameters(double currentLatitude, double currentLongitude, Double currentAltitude, Float currentSpeed);
}
