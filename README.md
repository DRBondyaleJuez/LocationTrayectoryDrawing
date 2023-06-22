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

To use the phones location tracking it use the following dependency: 'com.google.android.gms:play-services-location:21.0.1'

<div style="text-align: center;">

![App-Diagram](https://github.com/DRBondyaleJuez/LocationTrayectoryDrawing/assets/98281752/dbf42281-4f16-43d9-9941-69d178fcab7c)

</div>

## __Navigation between Views:__

This images show how user interactions with the view might work:

<div style="text-align: center;">

![GPS and permission view](https://github.com/DRBondyaleJuez/LocationTrayectoryDrawing/assets/98281752/581e5ea8-c5d9-4514-91db-4965231b6d76)
</div>

Image of the phone screen when the app is turned on without GPS location active and image of the phone screen when the app is turn on without permission to access location information

After accepting these two windows the MainView is shown.

<div style="text-align: center;">

![MainView Explained](https://github.com/DRBondyaleJuez/LocationTrayectoryDrawing/assets/98281752/ca91d7e8-627d-4b9f-897b-e471ba774717)
</div>

Image of the phone screen of the MainView before turning on the continuous tracking and after some considerable amount of time tracking and drawing.
Sections and interactive elements of the screen are defined. (Blurred latitude and longitude data for privacy reasons but visible when using the app.)

You can switch to the other view of the app, as explained in the image, to the following view:

<div style="text-align: center;">

![VisualizerView Explained](https://github.com/DRBondyaleJuez/LocationTrayectoryDrawing/assets/98281752/8a928e4a-0a20-4b92-9ff9-9eb00d0fef54)
</div>
Image of the phone screen of the TrajectoriesVisualizerView displaying some of the trajectories.
Sections and interactive elements of the screen are defined.

Several toast serve to communicate with the user if the interaction has been successful.

Application in use:

<div style="text-align: center;">

![App-Diagram](https://github.com/DRBondyaleJuez/LocationTrayectoryDrawing/assets/98281752/dbf42281-4f16-43d9-9941-69d178fcab7c)

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


## __INSTALLATION INSTRUCTIONS__
### __For IDE:__
<!-- OL -->
1. Clone the repository in your local server
2. Run the project's Main Class in your IDE

### __For Ubuntu (In terminal):__
<!-- OL -->
1. If necessary [install java version 11 or higher](https://stackoverflow.com/questions/52504825/how-to-install-jdk-11-under-ubuntu)

    ```bash
        sudo apt-get install openjdk-11-jdk
    ```


2. If necessary [install maven version 3.6.3 or higher](https://phoenixnap.com/kb/install-maven-on-ubuntu)

   ```bash 
       sudo apt install maven
   ``` 

3. If necessary [install git](https://www.digitalocean.com/community/tutorials/how-to-install-git-on-ubuntu-20-04)

   ```bash 
       apt install git
   ```

4. Clone the repository

   ```bash 
       git clone https://github.com/DRBondyaleJuez/ThePetShopApp.git
   ```

5. Go to the project folder. Make sure the [pom.xml](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html) is there.

6. Create [.jar file](https://en.wikipedia.org/wiki/JAR_(file_format)) executable in target folder using the following code:

    ```bash
        mvn install 
    ```
   in case tests are giving trouble, this can be used to install without tests. However, it is not recommended.
   ```bash
        mvn package -Dmaven.test.skip
    ```

7. Build the database and tables needed for this application's persistence. Follow the recommendations in the resources.RelationalDatabaseSchema which describes the code in PostgreSQL.
   ([Help creating your first database in pgAdmin](https://www.tutorialsteacher.com/postgresql/create-database))


8. Fill in the parameters needed in the secrets.properties for access to the PostgreSQL database and for the encryption

   These are:
    <!-- OL -->
    - <ins>*DBUser*</ins>  (String username of the database software)
    - <ins>*DBPassword*</ins>  (String password of the database)
    - <ins>*encryptionKey*</ins>  (String a key for the encryption)
    - <ins>*saltSize*</ins>  (int the size of the desired salt)
    - <ins>*initialSubstringPositionForTransposition*</ins>  (int related to the transposition during the encryption. It should __not__ be 0, 1 or larger than half the encryption key size)

   Make sure the PropertiesReader boots the properties from the secrets.properties file

9. This code uses javafx FXML, so we recommend the use of the following code  to run the program :

   ([*Source*](https://github.com/openjfx/javafx-maven-plugin))

    ```bash 
        mvn javafx:run
    ```

___
___
## __INSTRUCTIONS FOR CONTRIBUTORS__
The objective of the project was to practice and apply java knowledge. No further contributions will be needed all of this is just a training excercise.

Hope you may find the code useful and please acknowledge its origin and authorship if used for any other purpose.


