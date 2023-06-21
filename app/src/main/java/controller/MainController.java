package controller;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Date;

import controller.directionCalculator.DirectionCalculator;
import controller.directionCalculator.DirectionEnum;
import controller.directionCalculator.DirectionalVector;
import controller.locationController.LocationController;
import controller.locationController.LocationControllerObserver;
import persistence.PersistenceManager;
import viewController.MainControllerObservable;
import viewController.MainControllerObserver;

/**
 * Provides the class that acts as a controller for the MainViewController in charge of providing
 * logic, temporal storage of trajectory information, mediating between the persistence and the viewController.
 * It also communicates with two complementary classes to calculate directions and to retrieve directions from the
 * phones GPS system.
 * It also implements interface acting as an observable for the viewController and as a observer of the
 * LocationController
 */
public class MainController implements LocationControllerObserver, MainControllerObservable {

    //CONSTANTS
    private static final double DEFAULT_DOUBLE_STOP_OR_ABSENCE = -2000;
    private static final float DEFAULT_SPEED = -1;
    private static final int BUFFER_SIZE_FOR_MEAN = 1;
    private static final int NUMBER_OF_POINTS_PER_TRAJECTORY = 3;
    private static final int AMOUNT_OF_DATA_IN_DISPLAY = 10;

    //COMPLEMENTARY CONTROLLERS
    private final LocationController locationController;
    private DirectionCalculator directionCalculator;
    private final PersistenceManager persistenceManager;

    //LISTS OF DATA
    private final ArrayList<Double> latitudes;
    private ArrayList<Double> bufferLatitudes;
    private final ArrayList<Double> longitudes;
    private ArrayList<Double> bufferLongitudes;
    private ArrayList<double[]> directionsBuffer;
    private final ArrayList<Double> punctualDistances;
    private final ArrayList<Double> altitudes;
    private final ArrayList<Float> speeds;
    private ArrayList<DirectionEnum> directions;
    private double totalDistance;
    private double distanceFromOrigin;

    //OBSERVERS
    private ArrayList<MainControllerObserver> observers;

    /**
     * This is the constructor. Several attributes are initialized here. The locationController and the
     * persistenceManager are provided since they are constructed with activity as attribute which is not
     * needed here in the controller
     * @param locationController LocationController object built with the activity already
     * @param persistenceManager PersistenceManager object built with the activity already
     */
    public MainController(LocationController locationController , PersistenceManager persistenceManager) {
        this.locationController = locationController;
        locationController.updateGPS();

        this.persistenceManager = persistenceManager;

        latitudes = new ArrayList<>();
        bufferLatitudes = new ArrayList<>();
        longitudes = new ArrayList<>();
        bufferLongitudes = new ArrayList<>();
        punctualDistances = new ArrayList<>();
        altitudes = new ArrayList<>();
        speeds = new ArrayList<>();
        observers = new ArrayList<>();
        totalDistance = 0.0;
        distanceFromOrigin = 0.0;

        directionsBuffer = new ArrayList<>();
        directions = new ArrayList<>();

        locationController.addObservers(this);
    }


    public void updateLocation() {
        locationController.updateGPS();
    }

    /**
     * Start the continuous location exchange with the LocationController which will trigger the use
     * of other methods in this class called by the locationController as observable
     */
    public void startContinuousUpdateLocation() {
        locationController.startContinuousLocationUpdate();
    }

    /**
     * Stop the continuous location exchange with the LocationController and add a constant in the
     * list of location parameters retrieved indicating the lack of information
     */
    public void stopContinuousUpdateLocation() {
        locationController.stopContinuousLocationUpdate();
        latitudes.add(DEFAULT_DOUBLE_STOP_OR_ABSENCE);
        longitudes.add(DEFAULT_DOUBLE_STOP_OR_ABSENCE);
        punctualDistances.add(DEFAULT_DOUBLE_STOP_OR_ABSENCE);

    }

    /**
     * Save the location parameters in the attributes using the PersistenceManager.
     * <p>
     *     To do this a filename is obtained based on the current date and time.
     *     The data is converted to a String with a certain csv inspired format before storage
     * </p>
     * @return boolean confirming the success or failure of the operation
     */
    public boolean saveData(){
        if(latitudes.size() < 1 || directions.size() < 1) return false;

        //Creating the name of the file
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date date = new Date();
        String currentDateTime = dateFormat.format(date);
        String filename = currentDateTime;

        //Formatting the data for storage
        String data = "";
        for (int i = 0; i < latitudes.size(); i++) {
            data = data + latitudes.get(i) + ";" + longitudes.get(i) + "\n";
        }
        return persistenceManager.saveData(filename,data);
    }

    /**
     * Restart triggered by the restart of the MainViewController. This implieas reset conditions
     * similar to the beginning, clearing all containers and parameters
     */
    public void restart() {
        latitudes.clear();
        bufferLatitudes.clear();
        longitudes.clear();
        bufferLongitudes.clear();
        punctualDistances.clear();
        altitudes.clear();
        speeds.clear();
        totalDistance = 0.0;
        directions.clear();
        directionsBuffer.clear();
    }


    //Implementations of overridden methods of the MainControllerObservable and LocationControllerObserver interfaces
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

            if(latitudes.size() >= 2) {
                calculateAndSetPunctualDistances();
            } else {
                punctualDistances.add(0.0);
            }
            sendLocationInformation();
        }
    }

    @Override
    public void calculateAndSetDirection(double currentLatitude, double currentLongitude){
        double[] newLocation = {currentLatitude,currentLongitude};
        directionsBuffer.add(newLocation);

        if(directionsBuffer.size() == NUMBER_OF_POINTS_PER_TRAJECTORY){

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
            System.out.println("============================ Current Direction: " + currentDirection);
            sendDirectionInformation();
        }
    }

    /**
     * Call for the calculation of distances when a new location is added to determine the distance between this new location and the previous one.
     * Also the addition of this new distance to the distances list attribute and calculating the distanceFromOrgin and the totalDistance
     */
    private void calculateAndSetPunctualDistances(){

            Double currentLatitude = latitudes.get(latitudes.size()-1);
            Double previousLatitude = latitudes.get(latitudes.size()-2);
            if(previousLatitude == DEFAULT_DOUBLE_STOP_OR_ABSENCE) previousLatitude = latitudes.get(latitudes.size()-3);
            Double currentLongitude = longitudes.get(longitudes.size()-1);
            Double previousLongitude = longitudes.get(longitudes.size()-2);
            if(previousLongitude == DEFAULT_DOUBLE_STOP_OR_ABSENCE) previousLongitude = longitudes.get(longitudes.size()-3);
            Double newDistance = calculateNewDistance(currentLatitude,currentLongitude,previousLatitude,previousLongitude);

            distanceFromOrigin = calculateNewDistance(currentLatitude,currentLongitude,latitudes.get(0),longitudes.get(0));
            punctualDistances.add(newDistance);
            totalDistance += newDistance;

            sendDistanceInformation();
    }

    /**
     * Calculate distance between two points on a sphere provided the latitude and longitude.
     * <p>
     *     This calculation is based on the Haversine formula assuming Earth's radius is 6371km
     * </p>
     * @param currentLatitude Double corresponding to the final latitude
     * @param currentLongitude Double corresponding to the final longitude
     * @param previousLatitude Double corresponding to the initial latitude
     * @param previousLongitude Double corresponding to the initial longitude
     * @return Double corresponding to the result of the Haversine in km
     */
    private Double calculateNewDistance(Double currentLatitude, Double currentLongitude, Double previousLatitude, Double previousLongitude) {

        //CALCULATING DISTANCE FOLLOWING TH HAVERSINE FORMULA --- https://mathworld.wolfram.com/Haversine.html

        double EARTH_RADIUS = 6371.0; // Earth's radius in kilometers

        double dLat = Math.toRadians(currentLatitude - previousLatitude);
        double dLon = Math.toRadians(currentLongitude - previousLongitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(previousLatitude)) * Math.cos(Math.toRadians(currentLatitude))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        double distance = EARTH_RADIUS * c * 1000;
            return distance;
    }

    /**
     * Translate the list of Doubles of the location parameters to line spaced String lists with the
     * number of data to be displayed by the view based on the constant of this class.
     * @param dataList ArrayList of Doubles to convert to String
     * @return String of the converted list of doubles
     */
    private String buildDoubleListString(ArrayList<Double> dataList){
        String dataListString = "";
        int numberOfDataLines = dataList.size();
        if(dataList.size() > 10) numberOfDataLines = AMOUNT_OF_DATA_IN_DISPLAY;
        for (int i = 0; i < numberOfDataLines; i++) {
            int currentIndex = dataList.size() - 1 - i;
            if(dataList.get(currentIndex) <= DEFAULT_DOUBLE_STOP_OR_ABSENCE){
                dataListString = dataListString.concat(" -- " + "\n");
            } else {

                DecimalFormat df = new DecimalFormat("0." + "000000");
                String roundedDouble = df.format(dataList.get(currentIndex));

                dataListString = dataListString.concat(roundedDouble + "\n");
            }
        }
        return dataListString;
    }

    /**
     * Same as the other build method but with floats for the speed data
     * @param dataList arraylist of floats to translate to String
     * @return String corresponding to the number of values displayed from the list of floats
     */
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

    /**
     * Method to encapsulate the action commanded to the observers regarding location parameters
     */
    private void sendLocationInformation(){

        //CALLING OBSERVERS
        for (MainControllerObserver observer: observers) {

            observer.setLocationParameters(buildDoubleListString(latitudes),buildDoubleListString(longitudes),buildDoubleListString(altitudes),buildFloatListString(speeds));
            observer.setTrajectory(latitudes,longitudes);

        }
    }

    /**
     * Method to encapsulate the action commanded to the observers regarding directions
     */
    private void sendDirectionInformation(){

        //CALLING OBSERVERS
        for (MainControllerObserver observer: observers) {

            if(directions.size() < AMOUNT_OF_DATA_IN_DISPLAY){
                //observer.setDirection(directions.subList(0,directions.size()-1));
                observer.setDirection(directions.subList(0,directions.size()));
            } else {
                observer.setDirection(directions.subList(directions.size()-AMOUNT_OF_DATA_IN_DISPLAY,directions.size()-1));
            }
        }
    }

    /**
     * Method to encapsulate the action commanded to the observers regarding distances
     */
    private void sendDistanceInformation(){
        //CALLING OBSERVERS
        for (MainControllerObserver observer: observers) {
            observer.setDistances(totalDistance,distanceFromOrigin,buildDoubleListString(punctualDistances));
        }
    }

}


