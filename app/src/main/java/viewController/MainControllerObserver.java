package viewController;

import java.util.ArrayList;
import java.util.List;

import controller.directionCalculator.DirectionEnum;

public interface MainControllerObserver {

    void setLocationParameters(String currentLatitudes, String currentLongitudes, String currentAltitudes, String currentSpeeds);

    void setTrajectory(ArrayList<Double> latitudes, ArrayList<Double> longitudes);

    void setDirection(List<DirectionEnum> subList);

    void setNumberOfPoints(int numberOfPoints);

    void setDistances(double totalDistance, String currentPunctualDistances);
}
