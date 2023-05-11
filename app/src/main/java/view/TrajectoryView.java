package view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import viewController.TrajectoryViewController;

public class TrajectoryView  {

    private Activity activity;
    private TrajectoryViewController viewController;

    public TrajectoryView(TrajectoryViewController viewController) {
        this.activity = activity;
        this.viewController = viewController;
    }

    public Bitmap drawTrajectory(){
        //Bitmap currentBitmap = Bitmap.createBitmap(bitmapWidth,bitmapHeight);
        Bitmap currentBitmap = Bitmap.createBitmap(100,100, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(currentBitmap);

        Rect ourRect = new Rect();
        ourRect.set(0,0,canvas.getWidth(),canvas.getHeight()/2);

        Paint blue = new Paint();
        blue.setColor(Color.BLUE);
        blue.setStyle(Paint.Style.FILL);

        canvas.drawRect(ourRect,blue);

        //viewController.setTrajectory(currentBitmap);
        return currentBitmap;
    }

    /*
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        Rect ourRect = new Rect();
        ourRect.set(0,0,canvas.getWidth(),canvas.getHeight()/2);

        Paint blue = new Paint();
        blue.setColor(Color.BLUE);
        blue.setStyle(Paint.Style.FILL);

        canvas.drawRect(ourRect,blue);
        //canvas.save
        viewController.setTrajectory();
    }

     */
}
