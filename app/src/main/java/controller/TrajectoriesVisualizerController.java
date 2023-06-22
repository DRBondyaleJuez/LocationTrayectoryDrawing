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
 * Provides an object in charged of temporal storage of the data regarding the trajectory files stored by the app
 * It serves as a controller of the TrajectoryVisualizerViewController
 */
public class TrajectoriesVisualizerController {

    private PersistenceManager persistenceManager;
    private File[] trajectoryFileList;
    private ArrayList<Trajectory> trajectoryList;

    /**
     * This is the constructor. Several attributes are initialized here. The file and trajectory lists
     * are filled calling and the specific methods persistenceManager are provided since they are
     * constructed with activity as attribute which is not needed here in the controller
     * @param persistenceManager PersistenceManager object built with the activity already
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
     * Retrieve all the names of the files in trajectoryFileList for the app i.e. that have a particular extension
     * <p>
     *     This list is normally past called by the viewController specifically the recyclerView to display this list for selection
     * </p>
     * @return Array list of strings of all the filenames that have this particular extension
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
     * Retrieve all the files of interest from the persistence.
     * <p>
     *     These files are sorted based on their filename alphabetically reverse using a specific comparator
     * </p>
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
     * Build the trajectoryList attribute by opening and extract the data from the files in trajectoryFileList
     * <p>
     *     This method uses another method called extractTrajectoryFromFile which uses the persistenceManager
     *     to extract file information and then build trajectory objects which
     *     are then added to trajectoryList in the order the files were in trajectoryFileList
     * </p>
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
     * Build a particular trajectory object based on the data extracted from a file
     * <p>
     *     This method uses uses the persistenceManager  to extract file information and then builds
     *     a trajectory object with this data to return
     * </p>
     * @param file File provided to extract data and build trajectory object
     * @return Trajectory object containing extracted data
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
     * Build and return a list of the Trajectory objects in trajectory list which are selected
     * <p>
     *     Selected means that their attribute isChecked is true.
     *      This method checks every trajectory in trajectoryList and return those that match the criteria
     * </p>
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
     * Delete the files which correspond to selected trajectories.
     * <p>
     *     This method operates by finding the trajectory objects with a isChecked true attribute and
     *     then knowing their position in the list and their filename proceeds to select their file
     *     from the trajectoryFileList attribute and use the delete method of File objects.
     *     The deletion is confirmed or not in the provided object of the nested class FileDeletionResponse
     * </p>
     * @return FileDeletionResponse object containing a boolean informing of complete deletion but also two
     * array lists one of the files deleted and another of the files unable to delete for possible purposes for now
     * to inform the result of the operation through a toast
     */
    public FileDeletionResponse deleteSelectedFiles() {

        ArrayList<String> deletedFiles = new ArrayList<>();
        ArrayList<String> unableToDeleteFiles = new ArrayList<>();

        for (int i = 0; i < trajectoryFileList.length; i++) {

            if(trajectoryList.get(i).isChecked()){
                String currentFilename = trajectoryFileList[i].getName();
                System.out.println(trajectoryFileList[i].getPath());
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
