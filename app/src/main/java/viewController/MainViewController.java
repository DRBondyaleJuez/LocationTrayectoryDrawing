package viewController;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.locationtrayectorydrawing.R;

import java.util.ArrayList;
import java.util.List;

import controller.directionCalculator.DirectionEnum;
import controller.locationController.LocationController;
import controller.MainController;
import persistence.PersistenceManager;

public class MainViewController implements MainControllerObserver {

    //Activity
    private final Activity activity;

    //ViewComponents
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private TextView directionsTextView;
    private final Button locationFinderButtonOnce;
    private final Button saveDataButton;
    private final Switch continuousLocationSwitch;
    private final ImageView trajectoryImageView;


    // View controller attributes
    private final MainController controller;
    private final TrajectoryViewController trajectoryViewController;

    public MainViewController(Activity activity) {
        this.activity = activity;

        //Initializing and setting View components associated to view elements
        latitudeTextView = activity.findViewById(R.id.latitudeTextView);
        longitudeTextView = activity.findViewById(R.id.longitudeTextView);
        directionsTextView = activity.findViewById(R.id.directionTextView);
        locationFinderButtonOnce = activity.findViewById(R.id.locationFinderButtonOnce);
        locationFinderButtonOnce.setOnClickListener(setOnClickLocationFinder());
        continuousLocationSwitch = activity.findViewById(R.id.continuousLocationSwitch);
        continuousLocationSwitch.setOnClickListener(setOnClickContinuousLocationFinder());
        saveDataButton = activity.findViewById(R.id.saveDataButton);
        saveDataButton.setOnClickListener(setOnClickSaveData());
        trajectoryImageView = activity.findViewById(R.id.trajectoryImageView);

        //Building the controller
        LocationController locationController = new LocationController(activity);
        PersistenceManager persistenceManager = new PersistenceManager(activity);
        controller = new MainController(locationController,persistenceManager);
        trajectoryViewController = new TrajectoryViewController();

        controller.addObservers(this);

    }

    private View.OnClickListener setOnClickSaveData() {

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.saveData();
            }
        };

    }

    private View.OnClickListener setOnClickContinuousLocationFinder() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(continuousLocationSwitch.isChecked()) {
                    Toast.makeText(activity, "Obtaining continuous location data.", Toast.LENGTH_LONG).show();

                    controller.startContinuousUpdateLocation();

                } else {
                    Toast.makeText(activity, "STOP continuous location data.", Toast.LENGTH_LONG).show();
                    controller.stopContinuousUpdateLocation();
                    setEmptyLocationInfo();
                }
            }
        };
    }

    private void setEmptyLocationInfo() {
        latitudeTextView.setText(" -- ");
        longitudeTextView.setText(" -- ");
    }


    private View.OnClickListener setOnClickLocationFinder() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "Obtaining location data once.", Toast.LENGTH_LONG).show();

                controller.updateLocation();
            }
        };
    }

    @Override
    public void setLocationParameters(String currentLatitudes, String currentLongitudes, String currentAltitudes, String currentSpeeds){
        latitudeTextView.setText(currentLatitudes);
        longitudeTextView.setText(currentLongitudes);
    }

    @Override
    public void setTrajectory(ArrayList<Double> latitudes, ArrayList<Double> longitudes){
        trajectoryImageView.setImageBitmap(trajectoryViewController.getTrajectory(latitudes,longitudes));
    }

    @Override
    public void setDirection(List<DirectionEnum> directionList) {
        StringBuilder directionsString = new StringBuilder();
        for (DirectionEnum directionEnum:directionList) {
            directionsString.append(directionEnum.getSymbol() + "  ||  ");
        }
        directionsTextView.setText(directionsString);
    }
}
