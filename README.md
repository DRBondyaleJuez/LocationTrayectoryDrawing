# 📱 📍 🗺️ __LOCATION TRAJECTORY DRAWING__ 🎨 🖍️ 📉
## A simple android phone app that allows the visualization of line drawn based on the trajectory described by your location data (latitude and longitude) from the phone GPS
___

![GitHub contributors](https://img.shields.io/github/contributors/DRBondyaleJuez/LocationTrayectoryDrawing) 
![GitHub repo size](https://img.shields.io/github/repo-size/DRBondyaleJuez/LocationTrayectoryDrawing)
___

## __DESCRIPTION & USAGE__
This code was used as a training exercise to practice coding in java and the use of AndroidStudio, phone location information and the use of changing Bitmap display.
The application is structured following a MVC design pattern precisely a Model-View-ViewController-Controller. The view section of the design pattern is basically distributed 
along the res package in the layout package using elements from drawable and values packages.
Additionally, it has a persistence package to mediate between the app and the phone storage. 

The application is not responsive to dark mode or phone rotation.

Basically when started the app will ask to turn on your GPS location in the phone and ask for permissions to use this location.
Then the user can activate a continuous location tracking that will use GPS latitude and longitude data to draw a line describing the trajectory of the phone with the start location
remaining at the centre of the drawing.
This trajectory can be saved which will automatically assign it a name based on the date and time.
This stored trajectories can be accessed an displayed more than one at the same time in the second view of the app.
This second view shows the files stored by the app in the form of a recycleView and one or more can be selected for display or deletion.

During the display of more than one stored trajectory each one will have assigned a different color.

When the single continuous tracking trajectory is displayed it changes color along the route having the line changing hue from beginning to end.

<div style="text-align: center;">

![App-Diagram](https://github.com/DRBondyaleJuez/LocationTrayectoryDrawing/assets/98281752/ce8f96fa-9159-4e0e-bc24-bd4fa3eb33d0)
</div>

## __Navigation between Views:__

This images show how user interactions with the view might work:

<div style="text-align: center;">

![GPS and permission view](https://github.com/DRBondyaleJuez/LocationTrayectoryDrawing/assets/98281752/f95f19dd-68ce-4a6a-ac3a-418c269b073c)
</div>

Image of the phone screen when the app is turned on without GPS location active and image of the phone screen when the app is turn on without permission to access location information

After accepting these two windows the MainView is shown.

<div style="text-align: center;">

![MainView Explained](https://github.com/DRBondyaleJuez/LocationTrayectoryDrawing/assets/98281752/c74261b4-d5a8-44fe-9a1f-086dde99db91)
</div>

Image of the phone screen of the MainView before turning on the continuous tracking and after some considerable amount of time tracking and drawing.
Sections and interactive elements of the screen are defined. (Blurred latitude and longitude data for privacy reasons but visible when using the app.)

You can switch to the other view of the app, as explained in the image, to the following view:

<div style="text-align: center;">

![VisualizerView Explained](https://github.com/DRBondyaleJuez/LocationTrayectoryDrawing/assets/98281752/877cfd0c-c16e-486a-97ea-2a5d282aecf1)
</div>
Image of the phone screen of the TrajectoriesVisualizerView displaying some of the trajectories.
Sections and interactive elements of the screen are defined.

Several toast serve to communicate with the user if the interaction has been successful.

---

Application in use:

<div style="text-align: center;">

![Gif Location Drawing](https://github.com/DRBondyaleJuez/LocationTrayectoryDrawing/assets/98281752/89f4d68f-79e1-4b70-b1b1-eac984ca4a20)

</div>

___
___

## __PERSISTENCE__

The persistence manager class of the application stores the trajectory files in the directory accessed
by the method openFileOutput of the Activity class. The path to this directory is: /data/user/0/com.locationtrayectorydrawing/files/

The trajectory files are stored in a csv inspired format containing only the latitude and longitude data.

The filename created during saving follows this format based on the date and time of the moment of saving: YYYY_MM_DD_hh_mm_ss.trajectory

___
___

## __DEPENDENCIES AND PERMISSIONS__

To use the phones location tracking it use the following dependency: 'com.google.android.gms:play-services-location:21.0.1'

It requires the following permissions:
<!-- OL -->
   ```bash 
   - <uses-permission android:name="android.permission.INTERNET"/>
   - <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
   - <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
   ```
___
___

## __KNOWN ISSUES__

The direction calculations based on the locations and the display need to be fine-tuned and tested to
work properly. Currently it only displays STOP symbol.
___
___

## __INSTALLATION INSTRUCTIONS__
### __For IDE:__
<!-- OL -->
1. Clone the repository in your local server
2. Run the project's Main Class in your IDE. I used AndroidStudio
3. (If desired to try in your Android phone) Run the program with your phone connected allowing developer actions to install the application.

___
___
## __INSTRUCTIONS FOR CONTRIBUTORS__
The objective of the project was to practice and apply java knowledge and android app development. No further contributions will be needed all of this is just a training exercise.

Hope you may find the code useful and please acknowledge its origin and authorship if used for any other purpose.


