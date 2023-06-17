package viewController;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;

import model.Trajectory;

public class TrajectoryDrawingViewController {

    private static final double DEFAULT_DOUBLE_STOP_OR_ABSENCE = -2000;
    private static final double INDICATOR_OF_NEW_TRAJECTORY = -2001;
    private static int BITMAP_SIZE = 5000;
    private static int SQUARE_SIZE = 50;
    private static int ADJUSTED_BITMAP_SIZE = BITMAP_SIZE-SQUARE_SIZE;


    private Double previousMaxDistance;

    public TrajectoryDrawingViewController( ) {
        previousMaxDistance = 0.0;
    }

    public Bitmap getTrajectoryBITMAP(ArrayList<Double> latitudes, ArrayList<Double> longitudes){

        XYBitmapCoordinates xyBitmapCoordinates = getBitmapCoordinates(latitudes,longitudes);

        return drawTrajectory(xyBitmapCoordinates.getXCoordinates(), xyBitmapCoordinates.getYCoordinates());
    }

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

    private Bitmap drawMultipleTrajectoriesTogether(int numberOfTrajectories, int[] xCoordinates, int[] yCoordinates) {
        //Bitmap currentBitmap = Bitmap.createBitmap(bitmapWidth,bitmapHeight);
        Bitmap currentBitmap = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(currentBitmap);

        float hueUnit = 300/numberOfTrajectories;
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
                currentPaint.setStrokeWidth(10);

                canvas.drawLine(xCoordinates[i], yCoordinates[i], xCoordinates[i+1], yCoordinates[i+1], currentPaint);
            }
        }

        return currentBitmap;
    }

    public Bitmap drawEmptyBlackSquare(){
            //Bitmap currentBitmap = Bitmap.createBitmap(bitmapWidth,bitmapHeight);
            Bitmap currentBitmap = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(currentBitmap);

        return currentBitmap;
    }

    private Bitmap drawTrajectory(int[] xCoordinates, int[] yCoordinates){
        //Bitmap currentBitmap = Bitmap.createBitmap(bitmapWidth,bitmapHeight);
        Bitmap currentBitmap = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(currentBitmap);

/*
        for (int i = 0; i < xCoordinates.length; i++) {
            Rect ourRect = new Rect();
            ourRect.set(xCoordinates[i],yCoordinates[i],xCoordinates[i]+SQUARE_SIZE,yCoordinates[i]+SQUARE_SIZE);

            Paint blue = new Paint();
            blue.setColor(Color.BLUE);
            blue.setStyle(Paint.Style.FILL);

            canvas.drawRect(ourRect,blue);
        }
 */
        float hueUnit = 340/xCoordinates.length;

        if(xCoordinates.length > 1) {
            for (int i = 0; i < xCoordinates.length - 1; i++) {
                if(xCoordinates[i] == DEFAULT_DOUBLE_STOP_OR_ABSENCE || xCoordinates[i+1] == DEFAULT_DOUBLE_STOP_OR_ABSENCE) continue;

                float[] hsv ={hueUnit*i,100,100};
                Paint currentPaint = new Paint();
                currentPaint.setColor(Color.HSVToColor(hsv));
                currentPaint.setStrokeWidth(10);

                canvas.drawLine(xCoordinates[i], yCoordinates[i], xCoordinates[i+1], yCoordinates[i+1], currentPaint);
            }
        }


        //viewController.setTrajectory(currentBitmap);
        return currentBitmap;
    }
    /*
    public void setTrajectory(Bitmap currentTrajectory){
        viewController.setTrajectory(currentTrajectory);
    }
     */

    private static String hsvToRgb(float hue, float saturation, float value) {

        int h = (int)(hue * 6);
        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        switch (h) {
            case 0: return rgbToString(value, t, p);
            case 1: return rgbToString(q, value, p);
            case 2: return rgbToString(p, value, t);
            case 3: return rgbToString(p, q, value);
            case 4: return rgbToString(t, p, value);
            case 5: return rgbToString(value, p, q);
            default: throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        }
    }

    private static String rgbToString(float r, float g, float b) {
        String rs = Integer.toHexString((int)(r * 256));
        String gs = Integer.toHexString((int)(g * 256));
        String bs = Integer.toHexString((int)(b * 256));
        return rs + gs + bs;
    }

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
                    xCoordinatesInBitmap[i] = (int) ((((latitudes.get(i) - latitudes.get(0)) / previousMaxDistance) + 1) * ADJUSTED_BITMAP_SIZE / 2);
                    yCoordinatesInBitmap[i] = (int) ((((longitudes.get(i) - longitudes.get(0)) / previousMaxDistance) + 1) * ADJUSTED_BITMAP_SIZE / 2);
                } else {
                    xCoordinatesInBitmap[i] = latitudes.get(i).intValue();
                    yCoordinatesInBitmap[i] = latitudes.get(i).intValue();

                }
            }
        }

        return new XYBitmapCoordinates(xCoordinatesInBitmap,yCoordinatesInBitmap);
    }

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

    public class XYBitmapCoordinates{

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
