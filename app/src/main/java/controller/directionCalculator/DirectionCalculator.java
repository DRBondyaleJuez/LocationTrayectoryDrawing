package controller.directionCalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides a complementary class to the controller to aid in the determination of direction based on
 * the vectors provided during tracking
 */
public class DirectionCalculator {

    private static final int NUMBER_OF_POINTS_PER_TRAJECTORY = 3;
    private static final double LATITUDE_SUFFICIENT_CHANGE = 0.0005;
    private static final double LONGITUDE_SUFFICIENT_CHANGE = 0.0005;
    private static final double VECTOR_MODULE_SUFFICIENT_CHANGE = 0.0005;
    private DirectionalVector initialTrajectory;
    private DirectionalVector previousTrajectory;
    private ArrayList<DirectionEnum> directions;

    /**
     * This is the constructor.
     * Initializes certain attributes. Stores the initial vector and verifies if it is stop or
     * advancing in a certain direction.
     * @param initialTrajectoryVector DirectionalVector of the initial trajectory of the system
     */
    public DirectionCalculator(DirectionalVector initialTrajectoryVector) {
        this.initialTrajectory = initialTrajectoryVector;
        previousTrajectory = initialTrajectoryVector;
        directions = new ArrayList<>();
        directions.add(DirectionEnum.FRONT);
        if(checkIfStop(initialTrajectoryVector)){
            directions.remove(0);
            directions.add(0,DirectionEnum.STOP);
        }
    }

    public List<DirectionEnum> getTwoLastDirections() {
        return directions.subList(directions.size()-2,directions.size()-1);
    }

    /**
     * Get the last stored direction
     * @return The element in the penultimate position in the directions array list attribute
     */
    public DirectionEnum getLastDirection() {
        return directions.get(directions.size()-1);
    }


    /**
     * Determines the direction of a given trajectoryVector based on the values of this vector and its
     * comparison with the initial vector.
     * @param currentTrajectoryVector DirectionalVector for which the direction is going to be calculated
     * @return DirectionEnum which can correspond to said enum for STOP, FRONT, BACK, LEFT, RIGHT
     */
    public DirectionEnum calculateDirection(DirectionalVector currentTrajectoryVector){

        if(checkIfStop(currentTrajectoryVector)) return DirectionEnum.STOP;

        if(initialTrajectory.equals(previousTrajectory)) return DirectionEnum.FRONT;

        if(checkIfStop(initialTrajectory)) restart(currentTrajectoryVector);

        return determineDirectionBasedOnAngle(currentTrajectoryVector);
    }

    /**
     * Determine the direction of a vector based on the resulting angle with the initial vector.
     * <p>
     *     FRONT - if angle < PI/4
     *     BACK - if angle > 3*PI/4
     *     LEFT or RIGHT if PI/4 < angle < 3*PI/4:
     *          LEFT if ZComponent < 0
     *          RIGHT if ZComponent > 0
     * </p>
     * @param currentTrajectoryVector DirectionalVector to calculate the angle for
     * @return DirectionEnum STOP,FRONT,BACK,LEFT,RIGHT depending on the result of the calculation
     */
    private DirectionEnum determineDirectionBasedOnAngle(DirectionalVector currentTrajectoryVector) {

        double angleBetweenInitialAndCurrentTrajectoryVector = initialTrajectory.calculateAngle(currentTrajectoryVector);

        if(angleBetweenInitialAndCurrentTrajectoryVector < Math.PI/4) return DirectionEnum.FRONT;

        if(angleBetweenInitialAndCurrentTrajectoryVector > Math.PI*3/4) return DirectionEnum.BACK;

        double crossProductZComponent = initialTrajectory.calculateCrossProductZComponent(currentTrajectoryVector);

        if(crossProductZComponent < 0) return DirectionEnum.RIGHT;

        if(crossProductZComponent > 0) return DirectionEnum.LEFT;

        return DirectionEnum.STOP;
    }

    /**
     * Verify if a vector corresponds to an stop condition based on its module
     * <p>
     *     The module of the vector is compared to constant which is an attribute of this class
     * </p>
     * @param trajectoryVector DirectionalVector to determine if it has stopped
     * @return boolean confirm or denying if the vector has an insufficient module to confirm is moving
     */
    private boolean checkIfStop(DirectionalVector trajectoryVector){
        return trajectoryVector.getModule() <= VECTOR_MODULE_SUFFICIENT_CHANGE;
    }

    /**
     * Restablishing the vector of the initial trajectory. This methos is called if the previous initial trajectory
     * was a STOP.
     * @param newInitialTrajectoryVector DirectionalVector that will substitute the previous initial trajectory
     */
    private void restart(DirectionalVector newInitialTrajectoryVector){
        initialTrajectory = newInitialTrajectoryVector;
        directions.clear();
        directions.add(DirectionEnum.FRONT);
    }

    private double[] averageTrajectory(double[][] trajectory){
        double[] average = new double[2];
        for (double[] location:trajectory) {
            average[0] = average[0] + location[0]/NUMBER_OF_POINTS_PER_TRAJECTORY;
            average[1] = average[1] + location[1]/NUMBER_OF_POINTS_PER_TRAJECTORY;
        }
        return average;
    }
}
