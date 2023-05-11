package viewController;

import java.util.ArrayList;

public interface MainControllerObserver {

    void setLocationParameters(String currentLatitudes, String currentLongitudes, String currentAltitudes, String currentSpeeds);

    void setTrajectory(ArrayList<Double> latitudes, ArrayList<Double> longitudes);

}
