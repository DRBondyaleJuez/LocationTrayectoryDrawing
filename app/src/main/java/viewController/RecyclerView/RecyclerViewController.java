package viewController.RecyclerView;


import android.app.Activity;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import viewController.TrajectoriesVisualizerViewController;


/**
 * Provides an object that encapsulates the responsibilities behind managing the recyclerView it implements
 * RecyclerViewObservable following an observable-observer design pattern to facilitate communication with
 * RecyclerViewObserver implementations
 */
public class RecyclerViewController {

    private final RecyclerView trajectoryVisualizerRecyclerView;
    private final TrajectoriesVisualizerViewController trajectoriesVisualizerViewController;

    private ArrayList<RecyclerViewAdapter.MyViewHolder> viewHoldersOfRecyclerViewAdapter;

    /**
     * This is the constructor.
     * @param
     */
    public RecyclerViewController(RecyclerView trajectoryVisualizerRecyclerView,TrajectoriesVisualizerViewController trajectoriesVisualizerViewController) {
        this.trajectoryVisualizerRecyclerView = trajectoryVisualizerRecyclerView;
        this.trajectoriesVisualizerViewController = trajectoriesVisualizerViewController;
        viewHoldersOfRecyclerViewAdapter = new ArrayList<>();
    }

    /**
     * Procedure required for the inflation/filling of the recyclerView using a RecyclerViewAdapter and
     * a layoutManager
     * @param activity Activity object corresponding to the activity that built the MainViewController and responsible for the apps execution
     * @param trajectoryFileList ArrayList of Song object corresponding to the songs that will be accessed by the player
     */
    //Method to set up the intermediate class necessary to build and control the layout and behaviour of the recyclerView
    public void setRecyclerViewAdapter(Activity activity, ArrayList<String> trajectoryFileList) {
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(trajectoryFileList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity.getApplicationContext());
        trajectoryVisualizerRecyclerView.setLayoutManager(layoutManager);
        trajectoryVisualizerRecyclerView.setItemAnimator(new DefaultItemAnimator());
        trajectoryVisualizerRecyclerView.setAdapter(recyclerViewAdapter);
    }

    public RecyclerView getTrajectoryVisualizerRecyclerViewRecyclerView() {
        return trajectoryVisualizerRecyclerView;
    }

    //Methods related to clicking on the recyclerView
    public void trajectoryFileChecked(int position) {
        trajectoriesVisualizerViewController.trajectoryFileChecked(position);
    }

    public void trajectoryFileUnchecked(int position) {
        trajectoriesVisualizerViewController.trajectoryFileUnChecked(position);
    }

    public void addViewHolder(RecyclerViewAdapter.MyViewHolder viewHolder){
        viewHoldersOfRecyclerViewAdapter.add(viewHolder);
    }

    public void setCheckBoxesOfViewHolders(boolean checkedStatus){
        for (RecyclerViewAdapter.MyViewHolder currentViewHolder : viewHoldersOfRecyclerViewAdapter) {
            currentViewHolder.setTrajectoryFileCheckBox(checkedStatus);
        }
    }
}
