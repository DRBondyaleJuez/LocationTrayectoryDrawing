package controller;

import java.io.File;
import java.util.ArrayList;

import model.Trajectory;
import persistence.PersistenceManager;
import viewController.TrajectoriesVisualizerViewController;

public class TrajectoriesVisualizerController {

    PersistenceManager persistenceManager;
    File[] trajectoryFileList;
    ArrayList<Trajectory> trajectoryList;

    public TrajectoriesVisualizerController(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
        trajectoryFileList = collectTrajectoryFiles();
        trajectoryList = new ArrayList<>();
        buildTrajectoryList();
    }

    public ArrayList<String> getAllFilenames(){
        ArrayList<String> filenameList = new ArrayList<>();
        for (File currentFile:trajectoryFileList) {
            filenameList.add(currentFile.getName());
        }
        return filenameList;
    }

    private File[] collectTrajectoryFiles() {
        return persistenceManager.getAllFiles();
    }
    private void buildTrajectoryList() {

        trajectoryList.clear();

        for (File file:trajectoryFileList) {
            Trajectory currentTrajectory = extractTrajectoryFromFile(file);
            trajectoryList.add(currentTrajectory);
        }

    }

    private Trajectory extractTrajectoryFromFile(File file) {

        ArrayList<Double> extractedLatitudes = new ArrayList<>();
        ArrayList<Double> extractedLongitudes = new ArrayList<>();
        String dataInFile = persistenceManager.loadData(file.getName());
        String[] latitudeLongitudeString = dataInFile.split("\\r?\\n"); //Separate each line into an array
        for (String latitudeLongitude:latitudeLongitudeString) {
            String[] latitudeAndLongitudeSeparate = latitudeLongitude.split(";");
            Double currentExtractedLatitude = Double.parseDouble(latitudeAndLongitudeSeparate[0]);
            Double currentExtractedLongitude = Double.parseDouble(latitudeAndLongitudeSeparate[1]);
            extractedLatitudes.add(currentExtractedLatitude);
            extractedLongitudes.add(currentExtractedLongitude);
        }
        String currentFilename = file.getName();

        return new Trajectory(currentFilename,extractedLatitudes,extractedLongitudes);
    }
}
