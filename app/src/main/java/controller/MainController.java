package controller;

import android.app.Activity;
import java.util.ArrayList;

import viewController.MainControllerObservable;
import viewController.MainViewController;
import viewController.MainControllerObserver;

public class MainController implements LocationControllerObserver, MainControllerObservable {
    private static final double DEFAULT_ALTITUDE = -2000;
    private static final float DEFAULT_SPEED = -1;
    private static final int BUFFER_SIZE_FOR_MEAN = 1git ;
    private final LocationController locationController;
    private final ArrayList<Double> latitudes;
    private ArrayList<Double> bufferLatitudes;
    private final ArrayList<Double> longitudes;
    private ArrayList<Double> bufferLongitudes;
    private final ArrayList<Double> altitudes;
    private final ArrayList<Float> speeds;
    int amountOfDataInDisplay = 10;

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
    }



    public void updateLocation() {
        locationController.updateGPS();
    }

    public void startContinuousUpdateLocation() {
        locationController.startContinuousLocationUpdate();
    }

    public void stopContinuousUpdateLocation() {
        locationController.stopContinuousLocationUpdate();
    }


    private String buildDoubleListString(ArrayList<Double> dataList){
        String dataListString = "";
        int numberOfDataLines = dataList.size();
        if(dataList.size() > 10) numberOfDataLines = amountOfDataInDisplay;
        for (int i = 0; i < numberOfDataLines; i++) {
            int currentIndex = dataList.size() - 1 - i;
            if(dataList.get(currentIndex) < -100){
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
        if(dataList.size() > 10) numberOfDataLines = amountOfDataInDisplay;
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
            currentAltitude= DEFAULT_ALTITUDE;
        }
        altitudes.add(currentAltitude);
        if(currentSpeed == null){
            currentSpeed = DEFAULT_SPEED;
        }
        speeds.add(currentSpeed);


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

    private void sendLocationInformation(){
        //CALLING OBSERVERS
        for (MainControllerObserver observer: observers) {

            observer.setLocationParameters(buildDoubleListString(latitudes),buildDoubleListString(longitudes),buildDoubleListString(altitudes),buildFloatListString(speeds));
            observer.setTrajectory(latitudes,longitudes);

        }
    }
}


