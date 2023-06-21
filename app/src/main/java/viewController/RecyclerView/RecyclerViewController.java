package viewController.RecyclerView;


import android.app.Activity;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import viewController.TrajectoriesVisualizerViewController;


/**
 * Provides an object that encapsulates the responsibilities behind managing the recyclerView communicating
 * between the recyclerViewAdapter and TrajectoriesVisualizerViewController.
 */
public class RecyclerViewController {

    private final RecyclerView trajectoryVisualizerRecyclerView;
    private final TrajectoriesVisualizerViewController trajectoriesVisualizerViewController;

    private final ArrayList<RecyclerViewAdapter.MyViewHolder> viewHoldersOfRecyclerViewAdapter; //this attribute will allow the communication with the elements displayed in the recyclerView


    /**
     * This is the constructor.
     * <p>
     *     The ArrayList of RecyclerViewAdapter.MyViewHolder that will hold the elements displayed in the
     *     recycleView are collected here.
     * </p>
     * @param trajectoryVisualizerRecyclerView RecyclerView of this controller and display in the View
     * @param trajectoriesVisualizerViewController The viewController where this RecyclerView is initialized following a "simplified
     *                                             observer-observable with no interfaces"
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
     * @param trajectoryFileList ArrayList of Strings corresponding to the names of the files containing the trajectory data
     */
    //Method to set up the intermediate class necessary to build and control the layout and behaviour of the recyclerView
    public void setRecyclerViewAdapter(Activity activity, ArrayList<String> trajectoryFileList) {
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(trajectoryFileList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity.getApplicationContext());
        trajectoryVisualizerRecyclerView.setLayoutManager(layoutManager);
        trajectoryVisualizerRecyclerView.setItemAnimator(new DefaultItemAnimator());
        trajectoryVisualizerRecyclerView.setAdapter(recyclerViewAdapter);
    }

    /**
     * Return the state of the select all checkbox in the ViewController
     * @return
     */
    public boolean getSelectAllCheckBoxState(){
        return trajectoriesVisualizerViewController.getSelectAllCheckBoxState();
    }

    //Methods related to clicking on the recyclerView

    /**
     * Communicate to the ViewController and successive controllers that an element of the recyclerView
     * corresponding to a file has been checked
     * @param position the position of said file in the recyclerView and therefore in the container in the controller
     */
    public void trajectoryFileChecked(int position) {
        if(position > -1) { //Due to functioning of the recyclerView it can return -1 as the position so to avoid crashing this position is filtered
            trajectoriesVisualizerViewController.trajectoryFileChecked(position);
        }
    }

    /**
     * Communicate to the ViewController and successive controllers that an element of the recyclerView
     * corresponding to a file has been unchecked
     * @param position the position of said file in the recyclerView and therefore in the container in the controller
     */
    public void trajectoryFileUnchecked(int position) {
        if(position>-1) {
            trajectoriesVisualizerViewController.trajectoryFileUnChecked(position);
        }
    }


    /**
     * Adding components of the RecyclerView to the list attribute of the RecyclerViewController to facilitate
     * exchange and communication between the RecyclerViewController" and the elements displayed in the recyclerView
     * @param viewHolder RecyclerViewAdapter.MyViewHolder object are the elements displayed by the recyclerView based on a xml layout
     */
    public void addViewHolder(RecyclerViewAdapter.MyViewHolder viewHolder){
        viewHoldersOfRecyclerViewAdapter.add(viewHolder);
    }

    /**
     * Setting the checkBox state of the checkBoxes of the ViewHolders displayed by the RecyclerView
     * @param checkedStatus boolean corresponding to the status desired for the checkBox
     */
    public void setCheckBoxesOfViewHolders(boolean checkedStatus){
        for (RecyclerViewAdapter.MyViewHolder currentViewHolder : viewHoldersOfRecyclerViewAdapter) {
            currentViewHolder.setTrajectoryFileCheckBox(checkedStatus);
        }
    }
}
