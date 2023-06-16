package viewController;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.locationtrayectorydrawing.R;

import controller.TrajectoriesVisualizerController;
import persistence.PersistenceManager;
import viewController.RecyclerView.RecyclerViewController;

public class TrajectoriesVisualizerViewController {

    //Activity
    private final Activity activity;

    //View Components
    private final Button deleteButton;
    private final Button displayButton;
    private final CheckBox selectAllCheckBox;
    private final ImageView trajectoryVisualizerImageView;

    //ViewController Attributes
    private final TrajectoriesVisualizerController controller;
    private final TrajectoryDrawingViewController trajectoryViewController;
    private final RecyclerViewController recyclerViewController;

    public TrajectoriesVisualizerViewController(Activity activity) {
        this.activity = activity;

        //Initializing and setting View components associated to view elements
        trajectoryVisualizerImageView = activity.findViewById(R.id.trajectoryVisualizerImageView);

        deleteButton = activity.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(setOnClickDeleteButton());
        displayButton = activity.findViewById(R.id.displayButton);
        displayButton.setOnClickListener(setOnClickDisplayButton());
        selectAllCheckBox = activity.findViewById(R.id.selectAllCheckBox);

        RecyclerView trajectoryFilesRecyclerView = activity.findViewById(R.id.trajectoryFilesRecyclerView);

        PersistenceManager persistenceManager = new PersistenceManager(activity);
        controller = new TrajectoriesVisualizerController(persistenceManager);
        trajectoryViewController = new TrajectoryDrawingViewController();
        recyclerViewController = new RecyclerViewController(trajectoryFilesRecyclerView,this);

        recyclerViewController.setRecyclerViewAdapter(activity,controller.getAllFilenames());

    }

    private View.OnClickListener setOnClickDisplayButton() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }

    private View.OnClickListener setOnClickDeleteButton() {

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

    }

    public void trajectoryFileChecked(int position) {
    }

    public void trajectoryFileUnChecked(int position) {
    }
}
