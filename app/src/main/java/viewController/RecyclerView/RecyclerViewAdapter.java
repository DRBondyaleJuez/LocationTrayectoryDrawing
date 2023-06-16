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

            trajectoryFileCheckBox.setOnCheckedChangeListener(setOnTrajectoryCheckBoxChange());
            recyclerViewController.addViewHolder(this);
        }

        public void setTrajectoryFileCheckBox(boolean checkedStatus){
            trajectoryFileCheckBox.setChecked(checkedStatus);
        }

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

        /*
        private View.OnClickListener setOnClick (){
            return new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (recyclerViewController != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            recyclerViewController.onSongClicked(position);
                        }
                    }
                }
            };
        }

         */
    }
}
