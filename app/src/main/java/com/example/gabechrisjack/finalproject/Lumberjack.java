package com.example.gabechrisjack.finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Created by Jack Taylor on 11/18/17.
 * Based on Simplified Coding "Android Game Development Tutorial"
 * url: https://www.simplifiedcoding.net/android-game-development-tutorial-1/#Building-Game-View
 */
public class Lumberjack {
    private Bitmap bitmap;
    private int x;
    private int y;
    private final int SPEED = 10;
    private int horiSpeed = 0;


    //boolean variables to track if the player is moving
    private boolean moveLeft;
    private boolean moveRight;

    //Controlling X coordinate so that player won't go outside the screen
    private int maxX;
    private int minX;

    //Limit the bounds of the player's speed
    private final int MAX_HORI_SPEED = 50;
    private final int MIN_HORI_SPEED = -50;

    private Rect detectCollision;

    public Lumberjack(Context context, int screenX, int screenY) {
        x = screenX / 2;
        y = screenY - 350;
        //speed = 1;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.lumberjack);

        maxX = screenX - bitmap.getWidth();

        minX = 0;

        //initializing rect object
        detectCollision =  new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }

    // set move up true
    public void setMoveLeft() {
        moveLeft = true;
        moveRight = false;
    }

    // set move down true
    public void setMoveRight() {
        moveRight = true;
        moveLeft = false;
    }

    // method to stop moving
    public void stopMoving() {
        moveLeft = false;
        moveRight = false;
    }

    public void update() {

        //if the player is moving left
        if (moveLeft) horiSpeed -= 5;

            // if the player is moving right
        else if (moveRight) horiSpeed += 5;

        else horiSpeed = 0;

        //controlling the top speed
        if (horiSpeed > MAX_HORI_SPEED)
            horiSpeed = MAX_HORI_SPEED;

        if (horiSpeed < MIN_HORI_SPEED)
            horiSpeed = MIN_HORI_SPEED;

        //move the player
        x += horiSpeed;

        //but controlling it also so that it won't go off the screen
        if (x < minX) {
            x = minX;
        }
        if (x > maxX) {
            x = maxX;
        }

        //adding top, left, bottom and right to the rect object
        detectCollision.left = x;
        detectCollision.top = y;
        detectCollision.right = x + bitmap.getWidth();
        detectCollision.bottom = y + bitmap.getHeight();
    }

    //one more getter for getting the rect object
    public Rect getDetectCollision() {
        return detectCollision;
    }

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
        return SPEED;
    }
}