package viewController;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

/**
 * Provides an object in charged of communicating between the view asociated with the display of continous
 * trajectory tracking and the MainController and vice-versa. It implements the MainControllerObserver
 * interface following an observer-observable design pattern facilitating the continuous communication needed
 * during tracking to be able to display continuously.
 */
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
    private final ImageButton saveDataButton;
    private final ImageButton goToVisualizerButton;
    private final ImageButton restartButton;
    private final Switch continuousLocationSwitch;
    private final ImageView trajectoryImageView;

    // View controller attributes
    private final MainController controller;
    private final TrajectoryDrawingViewController trajectoryDrawingViewController;

    /**
     * This is the constructor.
     * <p>
     *     Here the view components are initialized and assigned actions.
     *     The complementary ViewControllers, persistence and the controller associated with this view are also
     *     initialized here since is the first contact from the MainActivity when the code is run.
     *     An initial black drawing is set in the trajectory imageView.
     * </p>
     * @param activity Activity object associated with the app starting
     */
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
        restartButton = activity.findViewById(R.id.restartButton);
        restartButton.setOnClickListener(setOnClickRestart());
        trajectoryImageView = activity.findViewById(R.id.trajectoryImageView);

        //Building the controller
        LocationController locationController = new LocationController(activity);
        PersistenceManager persistenceManager = new PersistenceManager(activity);
        controller = new MainController(locationController,persistenceManager);
        trajectoryDrawingViewController = new TrajectoryDrawingViewController();

        trajectoryImageView.setImageBitmap(trajectoryDrawingViewController.drawEmptyBlackSquare());

        controller.addObservers(this);
    }

    //Overridden implementation of the methods associated to the interface and the Observer-Observable design pattern
    @Override
    public void setLocationParameters(String currentLatitudes, String currentLongitudes, String currentAltitudes, String currentSpeeds){
        latitudeTextView.setText(currentLatitudes);
        longitudeTextView.setText(currentLongitudes);
    }

    @Override
    public void setTrajectory(ArrayList<Double> latitudes, ArrayList<Double> longitudes){
        trajectoryImageView.setImageBitmap(trajectoryDrawingViewController.getTrajectoryBITMAP(latitudes,longitudes));
        setNumberOfPoints(latitudes.size());
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
    public void setDistances(double totalDistance, double distanceFromOrigin, String currentPunctualDistances) {

        DecimalFormat df = new DecimalFormat("0." + "00");
        String roundedTotalDistance = df.format(totalDistance);

        totalDistanceTextView.setText("Total covered (m):  " + roundedTotalDistance);

        String roundedDistanceFromOrigin = df.format(distanceFromOrigin);
        distanceFromOriginTextView.setText("From origin (m): " + roundedDistanceFromOrigin);

        punctualDistanceTextView.setText(currentPunctualDistances);
    }


    //Methods associated with events triggered by interactions with particular view elements

    /**
     * Set response to clicking the restart button which basically resets all the components and
     * communicates this to the controller to clear data stored
     * @return
     */
    private View.OnClickListener setOnClickRestart() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                continuousLocationSwitch.setChecked(false);
                continuousLocationSwitchChange();

                trajectoryImageView.setImageBitmap(trajectoryDrawingViewController.drawEmptyBlackSquare());

                controller.restart();
                directionsTextView.setText("directions");
                latitudeTextView.setText("latitudeValues");
                longitudeTextView.setText("longitudeValues");
                punctualDistanceTextView.setText("distanceValues");
                distanceFromOriginTextView.setText("From origin (m): 0.0");
                totalDistanceTextView.setText(" Total covered (m): 0.0");
                numberOfPointsTextView.setText("Nº of Points: \n 0");
            }
        };
    }

    /**
     * Set the response when clicking the button to go to the visualizer.
     * This stops the previous process of tracking if active and changes the contentView by the activity
     * as well as initializing a TrajectoriesVisualizerViewController for this new view.
     * @return View.OnClickListener necessary for the action
     */
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

    /**
     * Set response triggered when the save button is clicked which indicates the controller to save the data
     * @return View.OnClickListener necessary for the action
     */
    private View.OnClickListener setOnClickSaveData() {

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.saveData();
            }
        };

    }

    /**
     * Set response triggered when the continuous location finder switch is changed
     * @return View.OnClickListener necessary for the action
     */
    private View.OnClickListener setOnClickContinuousLocationFinder() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                continuousLocationSwitchChange();

            }
        };
    }

    /**
     * Communicated the continuous location finding starting or ending to the controller with a corresponding
     * display in the form of a toast in the app
     */
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

    /**
     * Change the display when the continuous tracking is stopped
     */
    private void setEmptyLocationInfo() {
        latitudeTextView.setText(" -- ");
        longitudeTextView.setText(" -- ");
        punctualDistanceTextView.setText(" -- ");
    }

    /**
     * Change the display of the number of points during tracking
     */
    private void setNumberOfPoints(int numberOfPoints) {
        numberOfPointsTextView.setText("Nº of Points: \n" + numberOfPoints);
    }

}
