package com.example.gabechrisjack.finalproject;

/**
 * Created by Jack Taylor on 11/18/17.
 * Based on Simplified Coding "Android Game Development Tutorial"
 * url: https://www.simplifiedcoding.net/android-game-development-tutorial-1/#Building-Game-View
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

public class ForestGameView extends SurfaceView implements Runnable {

    volatile boolean playing;
    private Thread gameThread = null;
    private Lumberjack jack;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private BigPine bigPines;

    //created a reference of the class LittlePine
    private LittlePine littlepines;

    //defining a boom object to display blast
    private Boom boom;

    //a screenX holder
    int screenX;

    //to count the number of Misses
    int countMisses;

    //indicator that the enemy has just entered the game screen
    boolean flag ;

    //an indicator if the game is over or won
    public boolean isGameOver;
    public boolean isGameWon;


    //the score holder
    public int score;

    // media jack objects for background music
    static MediaPlayer gameOnsound;
    final MediaPlayer killedEnemysound;
    final MediaPlayer gameOversound;

    //context to be used in onTouchEvent to cause the activity transition from GameAvtivity to MainActivity.
    Context context;

    Bitmap bitmap;

    Bundle extras;

    public ForestGameView(Context context, int screenX, int screenY) {
        super(context);
        jack = new Lumberjack(context, screenX, screenY);

        surfaceHolder = getHolder();
        paint = new Paint();

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.woodland);

        //BigPine initialization
        bigPines = new BigPine(context, screenX, screenY);

        //initializing boom object
        boom = new Boom(context);

        //LittlePine initialization
        littlepines = new LittlePine(context, screenX, screenY);

        this.screenX = screenX;

        countMisses = 0;

        isGameOver = false;

        //setting the score to 0 initially
        score = 0;

        //initializing the media jacks for the game sounds
        gameOnsound = MediaPlayer.create(context,R.raw.gameon);
        killedEnemysound = MediaPlayer.create(context,R.raw.killedenemy);
        gameOversound = MediaPlayer.create(context,R.raw.gameover);

        //starting the game music as the game starts
        gameOnsound.start();

        /*
        extras.putString("user", ForestGameActivity.user);
        extras.putString("pass", ForestGameActivity.pass);
        extras.putBoolean("rivergame", ForestGameActivity.rivergame);
        extras.putBoolean("urbangame", ForestGameActivity.urbangame);
        */

        //initializing context
        this.context = context;

    }


    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    private void update() {
        //incrementing score as time passes
        score++;

        if (score > 1000) {
            isGameWon = true;
        }

        jack.update();

        //setting boom outside the screen
        boom.setX(-250);
        boom.setY(-250);

        //setting the flag true when the enemy just enters the screen
        if(bigPines.getX()==screenX){
            flag = true;
        }

        bigPines.update(jack.getSpeed());
        //if collision occurs with jack
        if (Rect.intersects(jack.getDetectCollision(), bigPines.getDetectCollision())) {
            //displaying boom at that location
            boom.setX(bigPines.getX());
            boom.setY(bigPines.getY());

            //playing a sound at the collision between jack and the enemy
            killedEnemysound.start();
            bigPines.setX(-200);
        }

        else{// the condition where jack misses the enemy
            //if the enemy has just entered
            if(flag){
                //if jack's x coordinate is equal to bigPines's y coordinate
                if(jack.getDetectCollision().exactCenterX()>=bigPines.getDetectCollision().exactCenterX()){

                    //increment countMisses
                    countMisses++;

                    //setting the flag false so that the else part is executed only when new enemy enters the screen
                    flag = false;

                    //if no of Misses is equal to 3, then game is over.
                    if(countMisses==5){

                        //setting playing false to stop the game.
                        playing = false;
                        isGameOver = true;


                        //stopping the gameon music
                        gameOnsound.stop();
                        //play the game over sound
                        gameOversound.start();
                    }
                }
            }

        }

        //updating the littlepines ships coordinates
        littlepines.update(jack.getSpeed());
        //checking for a collision between jack and a littlepines
        if(Rect.intersects(jack.getDetectCollision(),littlepines.getDetectCollision())){

            //displaying the boom at the collision
            boom.setX(littlepines.getX());
            boom.setY(littlepines.getY());
            //setting playing false to stop the game
            playing = false;
            //setting the isGameOver true as the game is over
            isGameOver = true;

            //stopping the gameon music
            gameOnsound.stop();
            //play the game over sound
            gameOversound.start();
        }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawBitmap(bitmap, 0, 0, paint);


            paint.setColor(Color.WHITE);
            paint.setTextSize(20);

            //drawing the score on the game screen
            paint.setTextSize(30);
            canvas.drawText("Score:"+score,100,50,paint);

            canvas.drawBitmap(
                    jack.getBitmap(),
                    jack.getX(),
                    jack.getY(),
                    paint);


            canvas.drawBitmap(
                    bigPines.getBitmap(),
                    bigPines.getX(),
                    bigPines.getY(),
                    paint
            );


            //drawing boom image
            canvas.drawBitmap(
                    boom.getBitmap(),
                    boom.getX(),
                    boom.getY(),
                    paint
            );


            //drawing littlepines image
            canvas.drawBitmap(

                    littlepines.getBitmap(),
                    littlepines.getX(),
                    littlepines.getY(),
                    paint
            );

            //draw game Over when the game is over
            if(isGameOver){
                paint.setTextSize(150);
                paint.setTextAlign(Paint.Align.CENTER);

                int yPos=(int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
                canvas.drawText("Game Over",canvas.getWidth()/2,yPos,paint);
            }

            if(isGameWon) {
                paint.setTextSize(150);
                paint.setTextAlign(Paint.Align.CENTER);

                int yPos=(int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
                canvas.drawText("Game Won!",canvas.getWidth()/2,yPos,paint);
            }

            surfaceHolder.unlockCanvasAndPost(canvas);

        }
    }

    //stop the music on exit
    public static void stopMusic(){
        gameOnsound.stop();
    }

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        // coordinates of the TouchEvent
        //float x = motionEvent.getX();
        float x = motionEvent.getX();

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                jack.stopMoving();
                break;
            case MotionEvent.ACTION_DOWN:
                if (x <= getWidth() / 2) jack.setMoveLeft();
                else if (x > getWidth() / 2) jack.setMoveRight();
                break;

        }
        //if the game's over, tappin on game Over screen sends you to MainActivity
        if(isGameOver){
            if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                Intent myIntent = new Intent(context, Game.class);
                myIntent.putExtra("user", ForestGameActivity.user);
                myIntent.putExtra("pass", ForestGameActivity.pass);
                myIntent.putExtra("rivergame", ForestGameActivity.rivergame);
                myIntent.putExtra("urbangame", ForestGameActivity.urbangame);
                myIntent.putExtra("forestgame", ForestGameActivity.forestgame);
                context.startActivity(myIntent);
            }
        }

        if (isGameWon) {
            if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                Intent myIntent = new Intent(context, Game.class);
                myIntent.putExtra("user", ForestGameActivity.user);
                myIntent.putExtra("pass", ForestGameActivity.pass);
                myIntent.putExtra("rivergame", ForestGameActivity.rivergame);
                myIntent.putExtra("urbangame", ForestGameActivity.urbangame);
                myIntent.putExtra("forestgame", true);
                context.startActivity(myIntent);
            }
        }
        return true;
    }
}