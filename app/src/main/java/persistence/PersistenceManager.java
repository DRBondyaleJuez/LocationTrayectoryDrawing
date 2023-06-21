package persistence;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Provides an intermediary between the controllers and file system of the phone
 * to retrieve and store files that contain trajectory data
 */
public class PersistenceManager {

    private Activity mainActivity;
    private static String STORAGE_PATH = "/trajectory_files/";
    private static String EXTENSION = ".trajectory";


    /**
     * This is the constructor.
     * @param mainActivity Activity type object that corresponds to the original
     *                     activity that started the app. This allows to use certain
     *                     methods and access the directory where paths are stored
     */
    public PersistenceManager(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }


    /**
     * Store the trajectory data (latitude and longitude values) in the directory the activity
     * has access to and displays a message if the process is completed.
     * @param filename String of the name of the file without the extension which is added after
     * @param data String of the latitudes and longitudes in a particular csv inspired format
     * @return boolean corresponding to the success of the process
     */
    public boolean saveData(String filename, String data){

        System.out.println("FILENAME: " + filename);
        System.out.println("DATA: " + data);

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = mainActivity.openFileOutput(filename + EXTENSION, Context.MODE_PRIVATE);
            fileOutputStream.write(data.getBytes());
            Toast.makeText(mainActivity, "Saved to " + mainActivity.getFilesDir() + filename + EXTENSION,Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(fileOutputStream != null){
                try {
                    fileOutputStream.close();
                    return true;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }


    /**
     * Open a file a extract the String data contained
     * @param filename String of the name of the file that is going to be opened
     * @return String the content of the file
     */
    public String loadData(String filename){

        FileInputStream fileInputStream = null;
        String fileContent;

        try {
            fileInputStream = mainActivity.openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String currentText;
            while ((currentText = bufferedReader.readLine()) != null){
                stringBuilder.append(currentText).append("\n");
            }

            fileContent = stringBuilder.toString();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fileContent;
    }


    /**
     * Retrieve all the files in the directory in the form of a File object
     * @return Array of Files contained in the directory
     */
    public File[] getAllFiles() {
        File directory = mainActivity.getFilesDir();
        File[] filesInDirectory = directory.listFiles();
        ArrayList<File> filteredFileList = new ArrayList<>();
        for (File currentFile:filesInDirectory) {
            if(currentFile.getName().contains(EXTENSION)){
                filteredFileList.add(currentFile);
            }
        }

        filesInDirectory = filteredFileList.toArray(new File[filteredFileList.size()]);

        return filesInDirectory;
    }

    /**
     * Get the String value in the EXTENSION attribute of this class
     * @return String assigned to the EXTENSION attribute
     */
    public String getEXTENSION(){
        return EXTENSION;
    }
}
