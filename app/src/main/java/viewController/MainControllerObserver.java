package viewController;

import java.util.ArrayList;
import java.util.List;

import controller.directionCalculator.DirectionEnum;

/**
 * Provides an interface representing one of the components of an observer-observable design pattern. This component
 * represents an object that can observe this facilitates triggers and interactions caused by objects which implement
 * the observable equivalent interface.
 */
public interface MainControllerObserver {


    /**
     * Modify the location parameters of the observer
     * @param currentLatitudes String formatted to display a list of the 10 most recent Doubles of latitude measurements
     * @param currentLongitudes String formatted to display a list of the 10 most recent Doubles of longitude measurements
     * @param currentAltitudes String formatted to display a list of the 10 most recent Doubles of altitude measurements
     * @param currentSpeeds String formatted to display a list of the 10 most recent Doubles of speed measurements
     */
    void setLocationParameters(String currentLatitudes, String currentLongitudes, String currentAltitudes, String currentSpeeds);

    /**
     * Modify the list of latitude and longitude data in the observer attributes
     * @param latitudes Array list of doubles corresponding to the latitude data
     * @param longitudes Array list of doubles corresponding to the longitude data
     */
    void setTrajectory(ArrayList<Double> latitudes, ArrayList<Double> longitudes);

    /**
     * Modify the list of data associated to the direction of trajectory based on the location data
     * @param subList list of a particular direction enum associated to the trajectory taken
     */
    void setDirection(List<DirectionEnum> subList);

    /**
     * Modify the parameter displayed associated with the distance
     * @param totalDistance double corresponding to the sum of all distance so far
     * @param distanceFromOrigin double corresponding to the difference between the current location and the initial location
     * @param currentPunctualDistances The distance between successive points
     */
    void setDistances(double totalDistance, double distanceFromOrigin, String currentPunctualDistances);
}
