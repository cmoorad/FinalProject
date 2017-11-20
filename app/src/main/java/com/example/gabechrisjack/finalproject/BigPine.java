package com.example.gabechrisjack.finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.lang.Math;

/**
 * Created by Jack Taylor on 11/18/17.
 * Based on Simplified Coding "Android Game Development Tutorial"
 * url: https://www.simplifiedcoding.net/android-game-development-tutorial-1/#Building-Game-View
 */
public class BigPine {

    //bitmap for the enemy
    //we have already pasted the bitmap in the drawable folder
    private Bitmap bitmap;

    //x and y coordinates
    private int x;
    private int y;

    //enemy speed
    private int speed = 1;

    //min and max coordinates to keep the enemy inside the screen
    private int maxX;
    private int minX;

    private int maxY;
    private int minY;

    //creating a rect object
    private Rect detectCollision;


    public BigPine(Context context, int screenX, int screenY) {
        //getting bitmap from drawable resource
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bigpine);

        //initializing min and max coordinates
        maxX = screenX - bitmap.getWidth();
        maxY = screenY;
        minX = 0;
        minY = 0;

        //generating a random coordinate to add enemy
        speed = (int) ((Math.random() * 10) + 20);
        if (speed < 1) speed = 1;
        x = (int) (Math.random() * maxX);
        y = 0;

        //initializing rect object
        detectCollision = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());

    }

    public void update(int playerSpeed) {
        //increasing y coordinate so that enemy will move top to bottom
        y += speed;
        //if the enemy reaches halfway down the screen
        if (y > maxY) {
            //adding the enemy again to the right edge
            speed = (int) ((Math.random() * 10) + 20);
            if (speed < 1) speed = 1;
            x = (int) (Math.random() * maxX);
            y = 0;
        }

        //Adding the top, left, bottom and right to the rect object
        detectCollision.left = x;
        detectCollision.top = y;
        detectCollision.right = x + bitmap.getWidth();
        detectCollision.bottom = y + bitmap.getHeight();
    }

    //adding a setter to x coordinate so that we can change it after collision
    public void setX(int x){
        this.x = x;
    }

    //getters
    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }

    //one more getter for getting the rect object
    public Rect getDetectCollision() {
        return detectCollision;
    }

}