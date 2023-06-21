package viewController;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;

import model.Trajectory;

/**
 * Provides the class in charged of creating the trajectory display on a bitmap to provide to the view.
 *
 */
public class TrajectoryDrawingViewController {

    //CONSTANTS
    private static final double DEFAULT_DOUBLE_STOP_OR_ABSENCE = -2000; //The value assigned to latitude and longitude values when the tracking is stopped or the data is absent
    private static final double INDICATOR_OF_NEW_TRAJECTORY = -2001; //The value assigned to latitude and longitude values when during the display of multiple trajectories the provided data requires the indication of different trajectories
    private static final int HUE_MAX_VALUE = 320; //The maximum value of the hue parameter in the HSV format use to assign colors
    private static final int RATIO_BETWEEN_DRAWING_AND_BORDER = 1; //a compensation parameter to slightly modify the distance between the trajectories and the squares edge
    private static int BITMAP_SIZE = 5000;
    private static int SQUARE_SIZE = 50;
    private static int ADJUSTED_BITMAP_SIZE = BITMAP_SIZE-SQUARE_SIZE;


    private Double previousMaxDistance;

    /**
     * This is the constructor.
     * The attribute corresponding to the maximum distance found is initialized.
     */
    public TrajectoryDrawingViewController( ) {
        previousMaxDistance = 0.0;
    }


    /** Creates and return a BITMAP file corresponding to the representation of the trajectory described by the latitude and longitude data information
     * <p>
     *     This requires first to convert the latitude and longitude data to x and y coordinates for the canvas that will be drawn based on the attributes
     *     and the maximum distance between points in the data.
     * </p>
     * @param latitudes Array list of Doubles corresponding to the latitude data that will be plotted
     * @param longitudes Array list of Doubles corresponding to the longitude data that will be plotted
     * @return Bitmap corresponding to the trajectory represented on a black canvas
     */
    public Bitmap getTrajectoryBITMAP(ArrayList<Double> latitudes, ArrayList<Double> longitudes){

        XYBitmapCoordinates xyBitmapCoordinates = getBitmapCoordinates(latitudes,longitudes);

        return drawTrajectory(xyBitmapCoordinates.getXCoordinates(), xyBitmapCoordinates.getYCoordinates());
    }

    /**
     * Provides a Bitmap similar to the previous method but for multiple trajectory data encapsulated in the model
     * class Trajectory
     * @param selectedTrajectories Array list of the model class Trajectory with the data of selected files with trajectory data
     * @return Bitmap corresponding to the trajectories selected represented on a black canvas
     */
    public Bitmap getSelectedTrajectoriesBITMAP(ArrayList<Trajectory> selectedTrajectories) {
        //Prepare data for BITMAP creation
        ArrayList<Double> allLatitudesTogether = new ArrayList<>();
        ArrayList<Double> allLongitudesTogether = new ArrayList<>();

        for (Trajectory currentTrajectory : selectedTrajectories) {
            allLatitudesTogether.addAll(currentTrajectory.getLatitudes());
            allLatitudesTogether.add(INDICATOR_OF_NEW_TRAJECTORY);

            allLongitudesTogether.addAll(currentTrajectory.getLongitudes());
            allLongitudesTogether.add(INDICATOR_OF_NEW_TRAJECTORY);
        }

        XYBitmapCoordinates xyBitmapCoordinates = getBitmapCoordinatesMultipleTrajectories(allLatitudesTogether,allLongitudesTogether);

        return drawMultipleTrajectoriesTogether(selectedTrajectories.size(),xyBitmapCoordinates.getXCoordinates(), xyBitmapCoordinates.getYCoordinates());

    }


    /**
     * Provides an Bitmap without any trajectories with the same black background. The previousMaxDistance
     * is also set back to zero since normally this no trajectory representation is used in restart context.
     * @return Bitmap black square without trajectories
     */
    public Bitmap drawEmptyBlackSquare(){
        Bitmap currentBitmap = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.RGB_565);

        previousMaxDistance = 0.0;

        return currentBitmap;
    }

    /**
     * Calculate de x and y corresponding coordinates to the latitudes and longitudes data
     * @param latitudes Array list of doubles corresponding to the latitude data
     * @param longitudes Array list of doubles corresponding to the latitude data
     * @return XYBitmapCoordinates object. This belongs to a nested class which agglutinates bot x and y coordinates basically.
     */
    //Methodology to convert latitudes and longitudes to the appropriate x and y coordinates using the nested class XYBitmapCoordinates
    private XYBitmapCoordinates getBitmapCoordinates(ArrayList<Double> latitudes, ArrayList<Double> longitudes){

        int lastIndex = latitudes.size()-1;

        //Check if the previousMaxDistance changes
        Double latDifference = Math.abs(latitudes.get(0)-latitudes.get(lastIndex));
        Double lonDifference = Math.abs(longitudes.get(0)-longitudes.get(lastIndex));
        if(previousMaxDistance < latDifference) previousMaxDistance = latDifference;
        if(previousMaxDistance < lonDifference) previousMaxDistance = lonDifference;

        int [] xCoordinatesInBitmap = new int[latitudes.size()];
        int [] yCoordinatesInBitmap = new int[longitudes.size()];

        for (int i = 0; i < latitudes.size(); i++) {

            if(previousMaxDistance == 0){
                xCoordinatesInBitmap[i] = ADJUSTED_BITMAP_SIZE/2;
                yCoordinatesInBitmap[i] = ADJUSTED_BITMAP_SIZE/2;
            } else {

                if(latitudes.get(i) != DEFAULT_DOUBLE_STOP_OR_ABSENCE && latitudes.get(i) != INDICATOR_OF_NEW_TRAJECTORY) {
                    xCoordinatesInBitmap[i] = (int) ((((latitudes.get(i) - latitudes.get(0)) / (previousMaxDistance * RATIO_BETWEEN_DRAWING_AND_BORDER)) + 1) * ADJUSTED_BITMAP_SIZE / 2);
                    yCoordinatesInBitmap[i] = (int) ((((longitudes.get(i) - longitudes.get(0)) / (previousMaxDistance * RATIO_BETWEEN_DRAWING_AND_BORDER)) + 1) * ADJUSTED_BITMAP_SIZE / 2);
                } else {
                    xCoordinatesInBitmap[i] = latitudes.get(i).intValue();
                    yCoordinatesInBitmap[i] = latitudes.get(i).intValue();

                }
            }
        }

        return new XYBitmapCoordinates(xCoordinatesInBitmap,yCoordinatesInBitmap);
    }

    //Methodology to convert latitudes and longitudes of multiple trajectories to the appropriate x and y coordinates using the nested class XYBitmapCoordinates
    /**
     * Calculate de x and y corresponding coordinates to multiple latitudes and longitudes data
     * <p>
     *     The difference with the previous one has especially got to do with maxDisitance calculation correction which
     *     does not employ the attribute required during tracking for persistence but needs to change when working with multiple sources
     * </p>
     * @param latitudes Array list of doubles corresponding to the latitude data
     * @param longitudes Array list of doubles corresponding to the latitude data
     * @return XYBitmapCoordinates object. This belongs to a nested class which agglutinates bot x and y coordinates basically.
     */
    private XYBitmapCoordinates getBitmapCoordinatesMultipleTrajectories(ArrayList<Double> latitudes, ArrayList<Double> longitudes){

        double currentMaxDistance = 0.0;
        //Finding the biggest difference so no trajectory representation falls out of the bitmap
        for (int i = 0; i < latitudes.size()-2; i++) {
            if(latitudes.get(i) != DEFAULT_DOUBLE_STOP_OR_ABSENCE && latitudes.get(i) != INDICATOR_OF_NEW_TRAJECTORY) {
                Double latDifference = Math.abs(latitudes.get(0) - latitudes.get(i));
                Double lonDifference = Math.abs(longitudes.get(0) - longitudes.get(i));
                if (currentMaxDistance < latDifference) currentMaxDistance = latDifference;
                if (currentMaxDistance < lonDifference) currentMaxDistance = lonDifference;
            }
        }

        int [] xCoordinatesInBitmap = new int[latitudes.size()];
        int [] yCoordinatesInBitmap = new int[longitudes.size()];

        for (int i = 0; i < latitudes.size(); i++) {

            if(currentMaxDistance == 0){
                xCoordinatesInBitmap[i] = ADJUSTED_BITMAP_SIZE/2;
                yCoordinatesInBitmap[i] = ADJUSTED_BITMAP_SIZE/2;
            } else {

                if(latitudes.get(i) != DEFAULT_DOUBLE_STOP_OR_ABSENCE && latitudes.get(i) != INDICATOR_OF_NEW_TRAJECTORY) {
                    xCoordinatesInBitmap[i] = (int) ((((latitudes.get(i) - latitudes.get(0)) / currentMaxDistance) + 1) * ADJUSTED_BITMAP_SIZE / 2);
                    yCoordinatesInBitmap[i] = (int) ((((longitudes.get(i) - longitudes.get(0)) / currentMaxDistance) + 1) * ADJUSTED_BITMAP_SIZE / 2);
                } else {
                    xCoordinatesInBitmap[i] = latitudes.get(i).intValue();
                    yCoordinatesInBitmap[i] = latitudes.get(i).intValue();

                }
            }
        }

        return new XYBitmapCoordinates(xCoordinatesInBitmap,yCoordinatesInBitmap);
    }

    /**
     * Drawing the trajectory on a bitmap. The color of the trajectory changes of hue as it is drawn
     * @param xCoordinates Array of ints corresponding to the horizontal coordinate value for the vertex of the line drawn
     * @param yCoordinates Array of ints corresponding to the vertical coordinate value for the vertex of the line drawn
     * @return Bitmap with the trajectory drawn in the form of a line on a black background
     */
    private Bitmap drawTrajectory(int[] xCoordinates, int[] yCoordinates){
        Bitmap currentBitmap = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(currentBitmap);

        float hueUnit = 1 + HUE_MAX_VALUE/xCoordinates.length; //changing hue
        int frequencyOfColorChange = 1 + xCoordinates.length/HUE_MAX_VALUE;
        if(xCoordinates.length > 1) {
            for (int i = 0; i < xCoordinates.length - 1; i++) {
                if(xCoordinates[i] == DEFAULT_DOUBLE_STOP_OR_ABSENCE || xCoordinates[i+1] == DEFAULT_DOUBLE_STOP_OR_ABSENCE) continue; //"Blank" space left when the tracking is interrupted or location data absent

                float[] hsv ={hueUnit*i/frequencyOfColorChange,100,100};//Changing hue

                Paint currentPaint = new Paint();
                currentPaint.setColor(Color.HSVToColor(hsv));
                currentPaint.setAlpha(180);
                currentPaint.setStrokeWidth(20);

                canvas.drawLine(xCoordinates[i], yCoordinates[i], xCoordinates[i+1], yCoordinates[i+1], currentPaint);
            }
        }
        return currentBitmap;
    }

    /**
     * Drawing multiple trajectories on a bitmap. The color of the trajectory changes of hue with each trajectory
     * @param xCoordinates Array of ints corresponding to the horizontal coordinate value for the vertex of the line drawn of every trajectory separated by a particular constant
     * @param yCoordinates Array of ints corresponding to the vertical coordinate value for the vertex of the line drawn of every trajectory separated by a particular constant
     * @return Bitmap with all trajectories drawn in the form of lines on a black background
     */
    private Bitmap drawMultipleTrajectoriesTogether(int numberOfTrajectories, int[] xCoordinates, int[] yCoordinates) {

        Bitmap currentBitmap = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(currentBitmap);

        float hueUnit = HUE_MAX_VALUE/numberOfTrajectories;
        int trajectoryNumber = 1;

        if(xCoordinates.length > 1) {
            for (int i = 0; i < xCoordinates.length - 1; i++) {
                float[] hsv ={hueUnit*trajectoryNumber,100,100};
                if(xCoordinates[i] == DEFAULT_DOUBLE_STOP_OR_ABSENCE || xCoordinates[i+1] == DEFAULT_DOUBLE_STOP_OR_ABSENCE) continue;
                if(xCoordinates[i] == INDICATOR_OF_NEW_TRAJECTORY || xCoordinates[i+1] == INDICATOR_OF_NEW_TRAJECTORY){
                    trajectoryNumber++;
                    continue;
                }
                Paint currentPaint = new Paint();
                currentPaint.setColor(Color.HSVToColor(hsv));
                currentPaint.setAlpha(150);
                currentPaint.setStrokeWidth(20);

                canvas.drawLine(xCoordinates[i], yCoordinates[i], xCoordinates[i+1], yCoordinates[i+1], currentPaint);
            }
        }

        return currentBitmap;
    }

    /**
     * Nested class to facilitate traffic of x and y Coordinates between methods in the class
     */
    private class XYBitmapCoordinates{

        private int[] xCoordinates;
        private int[] yCoordinates;

        public XYBitmapCoordinates(int[] xCoordinates, int[] yCoordinates) {
            this.xCoordinates = xCoordinates;
            this.yCoordinates = yCoordinates;
        }

        private int[] getXCoordinates() {
            return xCoordinates;
        }

        private int[] getYCoordinates() {
            return yCoordinates;
        }
    }


}
