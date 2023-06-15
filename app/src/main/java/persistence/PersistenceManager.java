package persistence;

import android.app.Activity;

public class PersistenceManager {

    private static String STORAGE_PATH = "";
    private Activity mainActivity;

    public PersistenceManager(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public boolean saveData(String filename, String data){

        System.out.println("DATA SAVED");
        return true;
    }

    public String getData(){
        return "";
    }
}
