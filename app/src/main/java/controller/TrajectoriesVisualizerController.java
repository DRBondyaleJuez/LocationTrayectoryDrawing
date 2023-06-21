package controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import model.Trajectory;
import persistence.PersistenceManager;

/**
 *
 */
public class TrajectoriesVisualizerController {

    PersistenceManager persistenceManager;
    File[] trajectoryFileList;
    ArrayList<Trajectory> trajectoryList;

    /**
     * This is the constructor.
     * @param persistenceManager
     */
    public TrajectoriesVisualizerController(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
        trajectoryFileList = collectTrajectoryFiles();
        trajectoryList = new ArrayList<>();
        if(trajectoryFileList.length > 0) {
            buildTrajectoryList();
        }
    }

    /**
     * @return
     */
    public ArrayList<String> getAllFilenames(){
        ArrayList<String> filenameList = new ArrayList<>();
        for (File currentFile:trajectoryFileList) {
            if(currentFile.getName().contains(persistenceManager.getEXTENSION())) {
                filenameList.add(currentFile.getName());
            }
        }
        return filenameList;
    }

    /**
     * @return
     */
    private File[] collectTrajectoryFiles() {

        File[] files = persistenceManager.getAllFiles();

        Comparator<File> comparator = new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                return file1.getName().compareTo(file2.getName())*-1; //Multiplied by -1 to sort it alphabetically inverse
            }
        };

        Arrays.sort(files, comparator);

        return files;
    }

    /**
     *
     */
    private void buildTrajectoryList() {

        trajectoryList.clear();

        for (File currentFile:trajectoryFileList) {
            if(currentFile.getName().contains(persistenceManager.getEXTENSION())) {
                Trajectory currentTrajectory = extractTrajectoryFromFile(currentFile);
                trajectoryList.add(currentTrajectory);
            }
        }

    }

    /**
     * @param file
     * @return
     */
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

    /**
     * Communicate to the corresponding trajectory in trajectoryList that it is now checked based on the recyclerView has been checked
     * @param position int the index of the file in the display and therefore index in trajectoryList
     */
    public void trajectoryFileChecked(int position) {
        trajectoryList.get(position).setCheckedStatus(true);
    }

    /**
     * Communicate to the corresponding trajectory in trajectoryList that it is now unchecked based on the recyclerView has been checked
     * @param position int the index of the file in the display and therefore index in trajectoryList
     */
    public void trajectoryFileUnchecked(int position) {
        trajectoryList.get(position).setCheckedStatus(false);
    }

    /**
     * Communicate to the all trajectory obejects in trajectoryList that their checked status is equal to the one provided
     * @param currentCheckStatus boolean corresponding to the status of checked or unchecked of all trajectory objects
     */
    public void applyToAllTrajectories(boolean currentCheckStatus) {
        for (Trajectory currentTrajectory : trajectoryList) {
            currentTrajectory.setCheckedStatus(currentCheckStatus);
        }
    }

    /**
     * @return
     */
    public ArrayList<Trajectory> getSelectedTrajectories() {

        ArrayList<Trajectory> selectedTrajectories = new ArrayList<>();

        for (Trajectory currentTrajectory : trajectoryList) {
            if(currentTrajectory.isChecked()){
                selectedTrajectories.add(currentTrajectory);
            }
        }
        return selectedTrajectories;
    }

    /**
     * @return
     */
    public FileDeletionResponse deleteSelectedFiles() {

        ArrayList<String> deletedFiles = new ArrayList<>();
        ArrayList<String> unableToDeleteFiles = new ArrayList<>();

        for (int i = 0; i < trajectoryFileList.length; i++) {
            if(trajectoryList.get(i).isChecked()){
                String currentFilename = trajectoryFileList[i].getName();
                System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< EVALUATING FILE FOR DELETION: " + currentFilename);
                if(trajectoryFileList[i].delete()){
                    deletedFiles.add(currentFilename);
                } else {
                    unableToDeleteFiles.add(currentFilename);
                }
            }
        }

        //Deletion triggering rebuilding the corresponding collections in this class:
        trajectoryFileList = collectTrajectoryFiles();
        trajectoryList = new ArrayList<>();
        if(trajectoryFileList.length > 0) {
            buildTrajectoryList();
        }

        boolean completeDeletion = unableToDeleteFiles.isEmpty();

        return new FileDeletionResponse(completeDeletion,deletedFiles,unableToDeleteFiles);
    }

    //
    /**
     * Nested class to handle the encapsulation of the response for the viewController when there is an attempt at deleting files
     */
    public class FileDeletionResponse{
        boolean completeDeletion;
        ArrayList<String> filesDeleted;
        ArrayList<String> filesUnableToDelete;

        public FileDeletionResponse(boolean completeDeletion, ArrayList<String> filesDeleted, ArrayList<String> filesUnableToDelete) {
            this.completeDeletion = completeDeletion;
            this.filesDeleted = filesDeleted;
            this.filesUnableToDelete = filesUnableToDelete;
        }

        public boolean isCompleteDeletion() {
            return completeDeletion;
        }

        public ArrayList<String> getFilesDeleted() {
            return filesDeleted;
        }

        public ArrayList<String> getFilesUnableToDelete() {
            return filesUnableToDelete;
        }
    }
}
