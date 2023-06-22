package viewController;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.locationtrayectorydrawing.R;

import java.util.ArrayList;

import controller.TrajectoriesVisualizerController;
import model.Trajectory;
import persistence.PersistenceManager;
import viewController.RecyclerView.RecyclerViewController;

/**
 * Provides the object in charged of managing the interactions between the view associated with
 * the multiple trajectories visualizer and the TrajectoriesVisualizerController and viceversa
 */
public class TrajectoriesVisualizerViewController {

    //Activity
    private final Activity activity;

    //View Components
    private final ImageButton backToTrackerButton;
    private final ImageButton deleteButton;
    private final ImageButton displayButton;
    private final CheckBox selectAllCheckBox;
    private final ImageView trajectoryVisualizerImageView;
    private RecyclerView trajectoryFilesRecyclerView;
    private final TextView noFileTextView;

    //ViewController Attributes
    private final TrajectoriesVisualizerController controller;
    private final TrajectoryDrawingViewController trajectoryDrawingViewController;
    private final RecyclerViewController recyclerViewController;

    /**
     * This is the constructor.
     * <p>
     *     Here the view components are initialized and assigned actions.
     *     The complementary ViewControllers, persistence and the controller associated with this view are also
     *     initialized here to retrieve the data that will be displayed in the recyclerView and draw it.
     *     .
     *     Some initial view properties are also modified accordingly.
     * </p>
     * @param activity Activity object associated with the app starting
     */
    public TrajectoriesVisualizerViewController(Activity activity) {
        this.activity = activity;

        //Initializing and setting View components associated to view elements
        trajectoryVisualizerImageView = activity.findViewById(R.id.trajectoryVisualizerImageView);

        deleteButton = activity.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(setOnClickDeleteButton());
        displayButton = activity.findViewById(R.id.displayButton);
        displayButton.setOnClickListener(setOnClickDisplayButton());
        backToTrackerButton = activity.findViewById(R.id.backToTrackerButton);
        backToTrackerButton.setOnClickListener(setOnClickChangeView());
        noFileTextView = activity.findViewById(R.id.noFileTextView);
        selectAllCheckBox = activity.findViewById(R.id.selectAllCheckBox);
        selectAllCheckBox.setOnCheckedChangeListener(setSelectedAllCheckChange());

        trajectoryFilesRecyclerView = activity.findViewById(R.id.trajectoryFilesRecyclerView);

        PersistenceManager persistenceManager = new PersistenceManager(activity);
        controller = new TrajectoriesVisualizerController(persistenceManager);
        trajectoryDrawingViewController = new TrajectoryDrawingViewController();
        recyclerViewController = new RecyclerViewController(trajectoryFilesRecyclerView,this);

        if(controller.getAllFilenames().size() > 0){
            noFileTextView.setVisibility(View.GONE);
            trajectoryFilesRecyclerView.setVisibility(View.VISIBLE);
            recyclerViewController.setRecyclerViewAdapter(activity,controller.getAllFilenames());
        } else {
            noFileTextView.setVisibility(View.VISIBLE);
            trajectoryFilesRecyclerView.setVisibility(View.GONE);
        }

        trajectoryVisualizerImageView.setImageBitmap(trajectoryDrawingViewController.drawEmptyBlackSquare());
    }

    /**
     * Retrieve the information regarding the boolean state of the checkBox that can check all checkboxes
     * @return boolean corresponding to the checked state of said checkbox
     */
    public boolean getSelectAllCheckBoxState(){
        return selectAllCheckBox.isChecked();
    }

    /**
     * Communicate to the controller that a certain trajectory file displayed in the recyclerView has been checked
     * @param position int the index of the file in the display and therefore index in the controller's storage
     */
    public void trajectoryFileChecked(int position) {
        controller.trajectoryFileChecked(position);
    }

    /**
     * Communicate to the controller that a certain trajectory file displayed in the recyclerView has been unchecked
     * @param position int the index of the file in the display and therefore index in the controller's storage
     */
    public void trajectoryFileUnChecked(int position) {
        controller.trajectoryFileUnchecked(position);
    }

    //Methods associated with events triggered by interactions with particular view elements

    /**
     * Set the response when the select all check box state changes either checked or unchecked.
     * communicating this to the controller and the recycler view to acted accordingly
     * @return CompoundButton.OnCheckedChangeListener necessary for the action
     */
    private CompoundButton.OnCheckedChangeListener setSelectedAllCheckChange() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                controller.applyToAllTrajectories(isChecked);
                recyclerViewController.setCheckBoxesOfViewHolders(isChecked);

            }
        };
    }

    /**
     * Set the response when the display button is clicked communicating this to the controller and
     * the recycler view to acted accordingly
     * @return View.OnClickListener necessary for the action
     */
    private View.OnClickListener setOnClickDisplayButton() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<Trajectory> selectedTrajectories = controller.getSelectedTrajectories();

                if(selectedTrajectories.isEmpty()){
                    trajectoryVisualizerImageView.setImageBitmap(trajectoryDrawingViewController.drawEmptyBlackSquare());
                } else {
                    trajectoryVisualizerImageView.setImageBitmap(trajectoryDrawingViewController.getSelectedTrajectoriesBITMAP(selectedTrajectories));
                }

            }
        };
    }

    /**
     * Set the response when the delete button is clicked communicating this to the controller and
     * the recycler view to acted accordingly
     * @return View.OnClickListener necessary for the action
     */
    private View.OnClickListener setOnClickDeleteButton() {

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrajectoriesVisualizerController.FileDeletionResponse deletionResponse = controller.deleteSelectedFiles();

                String message;

                if(deletionResponse.isCompleteDeletion()){
                    message = "Deleted selected files: \n ";
                    for (String filename: deletionResponse.getFilesDeleted()) {
                        message = message + filename + "\n";
                    }

                } else {
                    message = "Unable to delete files: \n ";
                    for (String filename: deletionResponse.getFilesUnableToDelete()) {
                        message = message + filename + "\n";
                    }

                }
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();

                //Redo recycler View
                if(controller.getAllFilenames().size() > 0){
                    noFileTextView.setVisibility(View.GONE);
                    trajectoryFilesRecyclerView.setVisibility(View.VISIBLE);
                    recyclerViewController.setRecyclerViewAdapter(activity,controller.getAllFilenames());
                } else {
                    noFileTextView.setVisibility(View.VISIBLE);
                    trajectoryFilesRecyclerView.setVisibility(View.GONE);
                }
                //Redraw black square
                trajectoryVisualizerImageView.setImageBitmap(trajectoryDrawingViewController.drawEmptyBlackSquare());
            }
        };

    }

    /**
     * Set the response when clicking the button to go to the tracker.
     * This changes the contentView by the activity
     * as well as initializing a MainViewController for this new view.
     * @return View.OnClickListener necessary for the action
     */
    private View.OnClickListener setOnClickChangeView() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.setContentView(R.layout.activity_main);
                new MainViewController(activity);
            }
        };
    }

}
