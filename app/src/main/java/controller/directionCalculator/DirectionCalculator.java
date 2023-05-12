package controller.directionCalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DirectionCalculator {

    private static final int NUMBER_OF_POINTS_PER_TRAJECTORY = 3;
    private static final double LATITUDE_SUFFICIENT_CHANGE = 0.0005;
    private static final double LONGITUDE_SUFFICIENT_CHANGE = 0.0005;
    private double[][] initialTrajectory;
    private double[][] previousTrajectory;
    private ArrayList<DirectionEnum> directions;

    public DirectionCalculator(double[][] initialTrajectory) {
        this.initialTrajectory = initialTrajectory;
        previousTrajectory = initialTrajectory;
        directions = new ArrayList<>();
        directions.add(DirectionEnum.FRONT);
        if(checkIfStop(initialTrajectory)){
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

    public void restart(double[][] newInitialTrajectory){
        initialTrajectory = newInitialTrajectory;
        directions.clear();
        directions.add(DirectionEnum.FRONT);
    }

    public DirectionEnum calculateDirection(double[][] currentTrajectory){

        if(checkIfStop(currentTrajectory)) return DirectionEnum.STOP;

        if(Arrays.deepEquals(initialTrajectory, previousTrajectory)) return DirectionEnum.FRONT;

        if(checkIfStop(initialTrajectory)){
            restart(currentTrajectory);
        } else {
            if(isSameAsPrevious(currentTrajectory)) return DirectionEnum.STOP;
        }

        return DirectionEnum.FRONT;
    }

    private boolean isSameAsPrevious(double[][] currentTrajectory) {
        double[] previousAverage = averageTrajectory(previousTrajectory);
        double[] currentAverage = averageTrajectory(currentTrajectory);
        if(Math.abs(previousAverage[0] - currentAverage[0]) > LATITUDE_SUFFICIENT_CHANGE || Math.abs(previousAverage[1] - currentAverage[1]) > LONGITUDE_SUFFICIENT_CHANGE){
            return false;
        } else {
            return true;
        }
    }

    private boolean checkIfStop(double[][] trajectory){

        if(Math.abs(trajectory[0][0] - trajectory[1][0]) > LATITUDE_SUFFICIENT_CHANGE || Math.abs(trajectory[0][0] - trajectory[2][0]) > LATITUDE_SUFFICIENT_CHANGE || Math.abs(trajectory[1][0] - trajectory[2][0]) > LATITUDE_SUFFICIENT_CHANGE){
            return false;
        }
        if(Math.abs(trajectory[0][1] - trajectory[1][1]) > LONGITUDE_SUFFICIENT_CHANGE || Math.abs(trajectory[0][1] - trajectory[2][1]) > LONGITUDE_SUFFICIENT_CHANGE|| Math.abs(trajectory[1][1] - trajectory[2][1]) > LONGITUDE_SUFFICIENT_CHANGE){
            return false;
        }

        return true;
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
