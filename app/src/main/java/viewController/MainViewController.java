package viewController;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.locationtrayectorydrawing.R;

import java.text.DecimalFormat;
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
    private TextView punctualDistanceTextView;
    private TextView numberOfPointsTextView;
    private TextView totalDistanceTextView;
    private TextView distanceFromOriginTextView;
    private TextView directionsTextView;
    private final Button saveDataButton;
    private final Button goToVisualizerButton;
    private final Switch continuousLocationSwitch;
    private final ImageView trajectoryImageView;

    // View controller attributes
    private final MainController controller;
    private final TrajectoryDrawingViewController trajectoryDrawingViewController;

    public MainViewController(Activity activity) {
        this.activity = activity;

        //Initializing and setting View components associated to view elements
        latitudeTextView = activity.findViewById(R.id.latitudeTextView);
        longitudeTextView = activity.findViewById(R.id.longitudeTextView);
        punctualDistanceTextView = activity.findViewById(R.id.punctualDistanceTextView);
        numberOfPointsTextView = activity.findViewById(R.id.numberOfPointsTextView);
        totalDistanceTextView = activity.findViewById(R.id.totalDistanceTextView);
        distanceFromOriginTextView = activity.findViewById(R.id.distanceFromOriginTextView);
        directionsTextView = activity.findViewById(R.id.directionTextView);
        continuousLocationSwitch = activity.findViewById(R.id.continuousLocationSwitch);
        continuousLocationSwitch.setOnClickListener(setOnClickContinuousLocationFinder());
        saveDataButton = activity.findViewById(R.id.saveDataButton);
        saveDataButton.setOnClickListener(setOnClickSaveData());
        goToVisualizerButton = activity.findViewById(R.id.goToVisualizerButton);
        goToVisualizerButton.setOnClickListener(setOnClickChangeView());
        trajectoryImageView = activity.findViewById(R.id.trajectoryImageView);

        //Building the controller
        LocationController locationController = new LocationController(activity);
        PersistenceManager persistenceManager = new PersistenceManager(activity);
        controller = new MainController(locationController,persistenceManager);
        trajectoryDrawingViewController = new TrajectoryDrawingViewController();

        trajectoryImageView.setImageBitmap(trajectoryDrawingViewController.drawEmptyBlackSquare());

        controller.addObservers(this);

    }

    private View.OnClickListener setOnClickChangeView() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continuousLocationSwitch.setChecked(false);
                continuousLocationSwitchChange();
                activity.setContentView(R.layout.saved_trajectories_visualizer);
                new TrajectoriesVisualizerViewController(activity);
            }
        };
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

                continuousLocationSwitchChange();

            }
        };
    }

    private void continuousLocationSwitchChange() {
        if(continuousLocationSwitch.isChecked()) {
            Toast.makeText(activity, "Obtaining continuous location data.", Toast.LENGTH_LONG).show();

            controller.startContinuousUpdateLocation();

        } else {
            Toast.makeText(activity, "STOP continuous location data.", Toast.LENGTH_LONG).show();
            controller.stopContinuousUpdateLocation();
            setEmptyLocationInfo();
        }
    }

    private void setEmptyLocationInfo() {
        latitudeTextView.setText(" -- ");
        longitudeTextView.setText(" -- ");
        punctualDistanceTextView.setText(" -- ");
    }


    @Override
    public void setLocationParameters(String currentLatitudes, String currentLongitudes, String currentAltitudes, String currentSpeeds){
        latitudeTextView.setText(currentLatitudes);
        longitudeTextView.setText(currentLongitudes);
    }

    @Override
    public void setTrajectory(ArrayList<Double> latitudes, ArrayList<Double> longitudes){
        trajectoryImageView.setImageBitmap(trajectoryDrawingViewController.getTrajectoryBITMAP(latitudes,longitudes));
    }

    @Override
    public void setDirection(List<DirectionEnum> directionList) {
        StringBuilder directionsString = new StringBuilder();
        for (DirectionEnum directionEnum:directionList) {
            directionsString.append(directionEnum.getSymbol() + "  ||  ");
        }
        directionsTextView.setText(directionsString);
    }

    @Override
    public void setNumberOfPoints(int numberOfPoints) {
        numberOfPointsTextView.setText("Nº of Points: " + numberOfPoints);
    }

    @Override
    public void setDistances(double totalDistance, double distanceFromOrigin, String currentPunctualDistances) {

        DecimalFormat df = new DecimalFormat("#." + "00");
        String roundedTotalDistance = df.format(totalDistance);

        totalDistanceTextView.setText("Total covered (m):  " + roundedTotalDistance);

        String roundedDistanceFromOrigin = df.format(distanceFromOrigin);
        distanceFromOriginTextView.setText("From origin (m): " + roundedDistanceFromOrigin);

        punctualDistanceTextView.setText(currentPunctualDistances);
    }
}
