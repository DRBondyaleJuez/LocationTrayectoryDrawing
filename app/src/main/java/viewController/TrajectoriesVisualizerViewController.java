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

    public boolean getSelectAllCheckBoxState(){
        return selectAllCheckBox.isChecked();
    }

    private CompoundButton.OnCheckedChangeListener setSelectedAllCheckChange() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                controller.applyToAllTrajectories(isChecked);
                recyclerViewController.setCheckBoxesOfViewHolders(isChecked);

            }
        };
    }

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

    private View.OnClickListener setOnClickChangeView() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.setContentView(R.layout.activity_main);
                new MainViewController(activity);
            }
        };
    }

    public void trajectoryFileChecked(int position) {
        controller.trajectoryFileChecked(position);
    }

    public void trajectoryFileUnChecked(int position) {
        controller.trajectoryFileUnchecked(position);
    }
}
