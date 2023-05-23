package controller;

import java.util.ArrayList;

import controller.directionCalculator.DirectionCalculator;
import controller.directionCalculator.DirectionEnum;
import controller.directionCalculator.DirectionalVector;
import controller.locationController.LocationController;
import controller.locationController.LocationControllerObserver;
import viewController.MainControllerObservable;
import viewController.MainControllerObserver;

public class MainController implements LocationControllerObserver, MainControllerObservable {
    private static final double DEFAULT_DOUBLE_STOP_OR_ABSENCE = -2000;
    private static final float DEFAULT_SPEED = -1;
    private static final int BUFFER_SIZE_FOR_MEAN = 1;
    private static final int NUMBER_OF_POINTS_PER_TRAJECTORY = 3;
    private final LocationController locationController;
    private final ArrayList<Double> latitudes;
    private ArrayList<Double> bufferLatitudes;
    private final ArrayList<Double> longitudes;
    private ArrayList<Double> bufferLongitudes;
    private ArrayList<double[]> directionsBuffer;
    private final ArrayList<Double> altitudes;
    private final ArrayList<Float> speeds;
    private static final int AMOUNT_OF_DATA_IN_DISPLAY = 10;
    private DirectionCalculator directionCalculator;
    private ArrayList<DirectionEnum> directions;

    private ArrayList<MainControllerObserver> observers;

    public MainController(LocationController locationController) {
        latitudes = new ArrayList<>();
        bufferLatitudes = new ArrayList<>();
        longitudes = new ArrayList<>();
        bufferLongitudes = new ArrayList<>();
        altitudes = new ArrayList<>();
        speeds = new ArrayList<>();
        observers = new ArrayList<>();
        this.locationController = locationController;
        locationController.addObservers(this);
        locationController.updateGPS();

        directionsBuffer = new ArrayList<>();
        directions = new ArrayList<>();
    }



    public void updateLocation() {
        locationController.updateGPS();
    }

    public void startContinuousUpdateLocation() {
        locationController.startContinuousLocationUpdate();
    }

    public void stopContinuousUpdateLocation() {
        locationController.stopContinuousLocationUpdate();
        latitudes.add(DEFAULT_DOUBLE_STOP_OR_ABSENCE);
        longitudes.add(DEFAULT_DOUBLE_STOP_OR_ABSENCE);

    }

    private String buildDoubleListString(ArrayList<Double> dataList){
        String dataListString = "";
        int numberOfDataLines = dataList.size();
        if(dataList.size() > 10) numberOfDataLines = AMOUNT_OF_DATA_IN_DISPLAY;
        for (int i = 0; i < numberOfDataLines; i++) {
            int currentIndex = dataList.size() - 1 - i;
            if(dataList.get(currentIndex) <= DEFAULT_DOUBLE_STOP_OR_ABSENCE){
                dataListString = dataListString.concat(" -- " + "\n");
            } else {
                dataListString = dataListString.concat(dataList.get(currentIndex).toString() + "\n");
            }
        }
        return dataListString;
    }

    private String buildFloatListString(ArrayList<Float> dataList){
        String dataListString = "";
        int numberOfDataLines = dataList.size();
        if(dataList.size() > AMOUNT_OF_DATA_IN_DISPLAY) numberOfDataLines = AMOUNT_OF_DATA_IN_DISPLAY;
        for (int i = 0; i < numberOfDataLines; i++) {
            int currentIndex = dataList.size() - 1 - i;
            if(dataList.get(currentIndex) < 0){
                dataListString = dataListString.concat(" -- " + "\n");
            } else {
                dataListString = dataListString.concat(dataList.get(currentIndex).toString() + "\n");
            }
        }
        return dataListString;
    }

    @Override
    public void addObservers(MainControllerObserver observer) {
        observers.add(observer);
    }

    @Override
    public void setLocationParameters(double currentLatitude, double currentLongitude, Double currentAltitude, Float currentSpeed){
        bufferLatitudes.add(currentLatitude);
        bufferLongitudes.add(currentLongitude);
        if(currentAltitude == null){
            currentAltitude= DEFAULT_DOUBLE_STOP_OR_ABSENCE;
        }
        altitudes.add(currentAltitude);
        if(currentSpeed == null){
            currentSpeed = DEFAULT_SPEED;
        }
        speeds.add(currentSpeed);

        //LOCATION OPERATIONS
        if(bufferLatitudes.size() == BUFFER_SIZE_FOR_MEAN && bufferLongitudes.size() == BUFFER_SIZE_FOR_MEAN){

            double latSum = 0;
            for (double lat:bufferLatitudes) {
                latSum += lat;
            }
            latitudes.add(latSum/BUFFER_SIZE_FOR_MEAN);
            bufferLatitudes.clear();

            double lonSum = 0;
            for (double lon:bufferLongitudes) {
                lonSum += lon;
            }
            longitudes.add(lonSum/BUFFER_SIZE_FOR_MEAN);
            bufferLongitudes.clear();

            sendLocationInformation();
        }
    }

    @Override
    public void calculateAndSetDirection(double currentLatitude, double currentLongitude){
        double[] newLocation = {currentLatitude,currentLongitude};
        directionsBuffer.add(newLocation);

        if(directionsBuffer.size() > NUMBER_OF_POINTS_PER_TRAJECTORY){

            double[][] trajectory = new double[NUMBER_OF_POINTS_PER_TRAJECTORY][2];
            for (int i = 0; i < NUMBER_OF_POINTS_PER_TRAJECTORY; i++) {
                trajectory[i] = directionsBuffer.get(i);
            }
            DirectionalVector currentDirectionalVector = new DirectionalVector(trajectory);

            DirectionEnum currentDirection;
            if(directionCalculator == null){
                directionCalculator = new DirectionCalculator(currentDirectionalVector);
                currentDirection = directionCalculator.getLastDirection();
            } else {
                currentDirection = directionCalculator.calculateDirection(currentDirectionalVector);
            }
            directions.add(currentDirection);
            directionsBuffer.clear();
            System.out.println(currentDirection);
            sendDirectionInformation();
        }
    }

    private void sendLocationInformation(){

        //CALLING OBSERVERS
        for (MainControllerObserver observer: observers) {

            observer.setLocationParameters(buildDoubleListString(latitudes),buildDoubleListString(longitudes),buildDoubleListString(altitudes),buildFloatListString(speeds));
            observer.setTrajectory(latitudes,longitudes);

        }
    }

    private void sendDirectionInformation(){

        //CALLING OBSERVERS
        for (MainControllerObserver observer: observers) {

            if(directions.size() < AMOUNT_OF_DATA_IN_DISPLAY){
                observer.setDirection(directions.subList(0,directions.size()-1));
            } else {
                observer.setDirection(directions.subList(directions.size()-AMOUNT_OF_DATA_IN_DISPLAY,directions.size()-1));
            }
        }
    }
}


