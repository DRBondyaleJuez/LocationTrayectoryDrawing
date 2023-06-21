package model;

import java.util.ArrayList;

/**
 * Provides an object that encapsulates the information necessary to draw the trajectory
 */
public class Trajectory {

    private final String filename;
    private final ArrayList<Double> latitudes;
    private final ArrayList<Double> longitudes;
    private boolean isChecked;

    /**
     * This is the constructor.
     * @param filename String the name of the file containing the data
     * @param latitudes ArrayList of Doubles the latitude values stored
     * @param longitudes ArrayList of Doubles the longitude values stored
     */
    public Trajectory(String filename,ArrayList<Double> latitudes, ArrayList<Double> longitudes) {
        this.filename = filename;
        this.latitudes = latitudes;
        this.longitudes = longitudes;
        isChecked = false;
    }

    // GETTERS
    public ArrayList<Double> getLatitudes() {
        return latitudes;
    }

    public ArrayList<Double> getLongitudes() {
        return longitudes;
    }

    public boolean isChecked() {
        return isChecked;
    }


    /**
     * Set the value of the isChecked attribute
     * @param status boolean corresponding to the checked status of this file in the view
     */
    public void setCheckedStatus(boolean status){
        isChecked = status;
    }
}
