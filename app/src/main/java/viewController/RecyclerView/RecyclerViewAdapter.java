package viewController.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.locationtrayectorydrawing.R;

import java.util.ArrayList;


/**
 * Provides the class required to fill the recyclerView correctly based on a particular xml layout
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private final RecyclerViewController recyclerViewController;
    private final ArrayList<String> trajectoryFileArrayList;

    public RecyclerViewAdapter(ArrayList<String> trajectoryFileArrayList, RecyclerViewController recyclerViewController) {
        this.trajectoryFileArrayList = trajectoryFileArrayList;
        this.recyclerViewController = recyclerViewController;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trajectory_file_list_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.MyViewHolder holder, int position) {

        //Change the display elements of the holder
        holder.trajectoryFileCheckBox.setText(trajectoryFileArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return trajectoryFileArrayList.size();
    }

    //Nested class necessary for the Recycler Adapter
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox trajectoryFileCheckBox;

        public MyViewHolder(final View view) {
            super(view);
            trajectoryFileCheckBox = view.findViewById(R.id.trajectoryFileCheckBox);
            trajectoryFileCheckBox.setChecked(recyclerViewController.getSelectAllCheckBoxState()); //Verify the state of the selectAll checkBox before building the one inside
            trajectoryFileCheckBox.setOnCheckedChangeListener(setOnTrajectoryCheckBoxChange());
            recyclerViewController.addViewHolder(this); //This line is to fill a lsit of ViewHolders in the RecyclerViewController and facilitate changes in here called by the RecycleViewHolder
        }

        /**
         * Changes the checkBox state based on a checkedStatus provided
         * <p>
         *     This was implemented to allow exterior actions to change this state. For example actions
         *     outside the recyclerView like a select all checkBox
         * </p>
         * @param checkedStatus
         */
        public void setTrajectoryFileCheckBox(boolean checkedStatus){
            trajectoryFileCheckBox.setChecked(checkedStatus);
        }

        /**
         * Provides the response when the checkBox of an element in the recyclerView changes which requires
         * communicating this to the successive controllers (recycleViewController, viewController and controller)
         * @return
         */
        private CompoundButton.OnCheckedChangeListener setOnTrajectoryCheckBoxChange() {
            return new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = getAdapterPosition();
                    if (recyclerViewController != null) {
                        if (isChecked) {
                            recyclerViewController.trajectoryFileChecked(position);
                        } else {
                            recyclerViewController.trajectoryFileUnchecked(position);
                        }
                    }
                }
            };
        }
    }
}
