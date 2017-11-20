package com.example.gabechrisjack.finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by Jack Taylor on 11/18/17.
 * Based on Simplified Coding "Android Game Development Tutorial"
 * url: https://www.simplifiedcoding.net/android-game-development-tutorial-1/#Building-Game-View
 */

public class LittlePine {

    private Bitmap bitmap;
    private int x;
    private int y;
    private int speed = 1;

    private int maxX;
    private int minX;

    private int maxY;
    private int minY;

    //creating a rect object for a friendly ship
    private Rect detectCollision;


    public LittlePine(Context context, int screenX, int screenY) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.littlepine);
        maxX = screenX - bitmap.getWidth();
        maxY = screenY;
        minX = 0;
        minY = 0;
        //generating a random coordinate to add friend
        speed = (int) ((Math.random() * 10) + 20);
        if (speed < 1) speed = 1;
        x = (int) (Math.random() * maxX);
        y = 0;

        //initializing rect object
        detectCollision = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());

    }

    public void update(int playerSpeed) {
        y += speed;
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

    //one more getter for getting the rect object
    public Rect getDetectCollision() {
        return detectCollision;
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

}