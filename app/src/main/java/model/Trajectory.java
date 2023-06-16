package model;

import java.util.ArrayList;

public class Trajectory {

    String filename;
    private final ArrayList<Double> latitudes;
    private final ArrayList<Double> longitudes;
    private boolean isChecked;

    public Trajectory(String filename,ArrayList<Double> latitudes, ArrayList<Double> longitudes) {
        this.filename = filename;
        this.latitudes = latitudes;
        this.longitudes = longitudes;
        isChecked = false;
    }

    public ArrayList<Double> getLatitudes() {
        return latitudes;
    }

    public ArrayList<Double> getLongitudes() {
        return longitudes;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void changeIsCheckedStatus(){
        boolean newIsCheckedStatus = !isChecked;
        isChecked = newIsCheckedStatus;
    }
}
