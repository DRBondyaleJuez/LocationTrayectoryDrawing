package com.locationtrayectorydrawing;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import viewController.MainViewController;
import viewController.TrajectoriesVisualizerViewController;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setContentView(R.layout.saved_trajectories_visualizer);
        new MainViewController(this);
        //new TrajectoriesVisualizerViewController(this);
    }


}