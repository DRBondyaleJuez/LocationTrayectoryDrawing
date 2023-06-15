package persistence;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class PersistenceManager {

    private static String STORAGE_PATH = "";
    private Activity mainActivity;

    public PersistenceManager(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public boolean saveData(String filename, String data){

        System.out.println("FILENAME: " + filename);
        System.out.println("DATA: " + data);

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = mainActivity.openFileOutput(filename, Context.MODE_PRIVATE);
            fileOutputStream.write(data.getBytes());
            Toast.makeText(mainActivity, "Saved to " + mainActivity.getFilesDir() + "/" + filename,Toast.LENGTH_LONG).show();
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
}
