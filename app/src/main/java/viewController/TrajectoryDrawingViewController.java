package viewController;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;

public class TrajectoryDrawingViewController {

    private static final double DEFAULT_DOUBLE_STOP_OR_ABSENCE = -2000;
    private static int BITMAP_SIZE = 5000;
    private static int SQUARE_SIZE = 50;
    private static int ADJUSTED_BITMAP_SIZE = BITMAP_SIZE-SQUARE_SIZE;


    private Double previousMaxDistance;

    public TrajectoryDrawingViewController( ) {
        previousMaxDistance = 0.0;
    }

    public Bitmap getTrajectory(ArrayList<Double> latitudes, ArrayList<Double> longitudes){

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

                if(latitudes.get(i) != DEFAULT_DOUBLE_STOP_OR_ABSENCE) {
                    xCoordinatesInBitmap[i] = (int) ((((latitudes.get(i) - latitudes.get(0)) / previousMaxDistance) + 1) * ADJUSTED_BITMAP_SIZE / 2);
                    yCoordinatesInBitmap[i] = (int) ((((longitudes.get(i) - longitudes.get(0)) / previousMaxDistance) + 1) * ADJUSTED_BITMAP_SIZE / 2);
                } else {
                    xCoordinatesInBitmap[i] = latitudes.get(i).intValue();
                    yCoordinatesInBitmap[i] = latitudes.get(i).intValue();

                }
            }

        }


        return drawTrajectory(xCoordinatesInBitmap,yCoordinatesInBitmap);
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
                Paint yellow = new Paint();
                yellow.setColor(Color.HSVToColor(hsv));
                yellow.setStrokeWidth(10);

                canvas.drawLine(xCoordinates[i], yCoordinates[i], xCoordinates[i+1], yCoordinates[i+1], yellow);
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


}
