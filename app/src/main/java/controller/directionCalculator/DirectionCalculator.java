package controller.directionCalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DirectionCalculator {

    private static final int NUMBER_OF_POINTS_PER_TRAJECTORY = 3;
    private static final double LATITUDE_SUFFICIENT_CHANGE = 0.0005;
    private static final double LONGITUDE_SUFFICIENT_CHANGE = 0.0005;
    private static final double VECTOR_MODULE_SUFFICIENT_CHANGE = 0.0005;
    private DirectionalVector initialTrajectory;
    private DirectionalVector previousTrajectory;
    private ArrayList<DirectionEnum> directions;

    public DirectionCalculator(DirectionalVector initialTrajectoryVector) {
        this.initialTrajectory = initialTrajectoryVector;
        previousTrajectory = initialTrajectoryVector;
        directions = new ArrayList<>();
        directions.add(DirectionEnum.FRONT);
        if(checkIfStop(initialTrajectoryVector)){
            directions.add(0,DirectionEnum.STOP);
        }
    }

    public ArrayList<DirectionEnum> getDirections() {
        return directions;
    }

    public List<DirectionEnum> getTwoLastDirections() {
        return directions.subList(directions.size()-2,directions.size()-1);
    }

    public DirectionEnum getLastDirection() {
        return directions.get(directions.size()-1);
    }


    public DirectionEnum calculateDirection(DirectionalVector currentTrajectoryVector){

        if(checkIfStop(currentTrajectoryVector)) return DirectionEnum.STOP;

        if(initialTrajectory.equals(previousTrajectory)) return DirectionEnum.FRONT;

        if(checkIfStop(initialTrajectory)) restart(currentTrajectoryVector);

        return determineDirectionBasedOnAngle(currentTrajectoryVector);
    }

    private DirectionEnum determineDirectionBasedOnAngle(DirectionalVector currentTrajectoryVector) {

        double angleBetweenInitialAndCurrentTrajectoryVector = initialTrajectory.calculateAngle(currentTrajectoryVector);

        if(angleBetweenInitialAndCurrentTrajectoryVector < Math.PI/4) return DirectionEnum.FRONT;

        if(angleBetweenInitialAndCurrentTrajectoryVector > Math.PI*3/4) return DirectionEnum.BACK;

        double crossProductZComponent = initialTrajectory.calculateCrossProductZComponent(currentTrajectoryVector);

        if(crossProductZComponent < 0) return DirectionEnum.RIGHT;

        if(crossProductZComponent > 0) return DirectionEnum.LEFT;

        return DirectionEnum.STOP;
    }


    private boolean checkIfStop(DirectionalVector trajectoryVector){
        return trajectoryVector.getModule() <= VECTOR_MODULE_SUFFICIENT_CHANGE;
    }

    private double[] averageTrajectory(double[][] trajectory){
        double[] average = new double[2];
        for (double[] location:trajectory) {
            average[0] = average[0] + location[0]/NUMBER_OF_POINTS_PER_TRAJECTORY;
            average[1] = average[1] + location[1]/NUMBER_OF_POINTS_PER_TRAJECTORY;
        }
        return average;
    }

    private void restart(DirectionalVector newInitialTrajectoryVector){
        initialTrajectory = newInitialTrajectoryVector;
        directions.clear();
        directions.add(DirectionEnum.FRONT);
    }


}
