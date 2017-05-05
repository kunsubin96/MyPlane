package com.example.kunsubin.myplane;

import android.graphics.Bitmap;
import android.provider.Settings;

/**
 * Created by kunsubin on 4/15/2017.
 */

public class Animation {
    private Bitmap[] frames;
    private int curentFrame;
    private long startTime;
    private long delay;
    private boolean playedOnce;
    public void setFrames(Bitmap[] frames){
        this.frames=frames;
        curentFrame=0;
        startTime= System.nanoTime();
    }
    public void setDelay(long delay){
        this.delay=delay;
    }
    public void setFrame(int i){
        curentFrame=i;
    }
    public void update(){
        long elapsed=(System.nanoTime()-startTime)/1000000;
        if(elapsed>delay){
            curentFrame++;
            startTime=System.nanoTime();
        }
        if(curentFrame==frames.length){
            curentFrame=0;
            playedOnce=true;
        }


    }
    public Bitmap getImage(){
        return frames[curentFrame];
    }
    public int getFrame(){
        return curentFrame;
    }
    public boolean isPlayedOnce(){
        return playedOnce;
    }
}
