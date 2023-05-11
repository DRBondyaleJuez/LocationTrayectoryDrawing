package viewController;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;

public class TrajectoryViewController {
    private static int BITMAP_SIZE = 5000;
    private static int SQUARE_SIZE = 50;
    private static int ADJUSTED_BITMAP_SIZE = BITMAP_SIZE-SQUARE_SIZE;


    private Double previousMaxDistance;

    public TrajectoryViewController( ) {
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

            xCoordinatesInBitmap[i] = (int)((((latitudes.get(i) - latitudes.get(0))/previousMaxDistance) + 1) * ADJUSTED_BITMAP_SIZE/2);
            yCoordinatesInBitmap[i] = (int)((((longitudes.get(i) - longitudes.get(0))/previousMaxDistance) + 1) * ADJUSTED_BITMAP_SIZE/2);
            }

        }


        return drawTrajectory(xCoordinatesInBitmap,yCoordinatesInBitmap);
    }


    private Bitmap drawTrajectory(int[] xCoordinates, int[] yCoordinates){
        //Bitmap currentBitmap = Bitmap.createBitmap(bitmapWidth,bitmapHeight);
        Bitmap currentBitmap = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(currentBitmap);


        for (int i = 0; i < xCoordinates.length; i++) {
            Rect ourRect = new Rect();
            ourRect.set(xCoordinates[i],yCoordinates[i],xCoordinates[i]+SQUARE_SIZE,yCoordinates[i]+SQUARE_SIZE);

            Paint blue = new Paint();
            blue.setColor(Color.BLUE);
            blue.setStyle(Paint.Style.FILL);

            canvas.drawRect(ourRect,blue);
        }


        //viewController.setTrajectory(currentBitmap);
        return currentBitmap;
    }
    /*
    public void setTrajectory(Bitmap currentTrajectory){
        viewController.setTrajectory(currentTrajectory);
    }
     */


}
