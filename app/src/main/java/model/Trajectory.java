package model;

import java.util.ArrayList;

public class Trajectory {

    private final ArrayList<String> latitudes;
    private final ArrayList<String> longitudes;
    private boolean isChecked;

    public Trajectory(ArrayList<String> latitudes, ArrayList<String> longitudes) {
        this.latitudes = latitudes;
        this.longitudes = longitudes;
        isChecked = false;
    }

    public ArrayList<String> getLatitudes() {
        return latitudes;
    }

    public ArrayList<String> getLongitudes() {
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
