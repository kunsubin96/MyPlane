package com.example.kunsubin.myplane;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by kunsubin on 4/15/2017.
 */

public class Player extends GameObject{
    private Bitmap spritesheet;

    private int score;
    private boolean up;

    private boolean playing;
    private Animation animation;
    private long startTime;
    public  Player(Bitmap res,int w, int h, int numFrames){
        animation=new Animation();
        x=100;
        y=GamePanel.HEIGHT/2;
        dy=0;
        score=0;
        width=w;
        height=h;
        Bitmap[] image=new Bitmap[numFrames];
        spritesheet=res;
        for (int i=0;i<image.length;i++){
            image[i]=Bitmap.createBitmap(spritesheet,i*width,0,width,height);
        }

        animation.setFrames(image);
        animation.setDelay(100);
        startTime= System.nanoTime();
    }
    public void setUp(boolean b){
        up=b;
    }
    public void update(){
        long elapsed=(System.nanoTime()-startTime)/1000000;
        if(elapsed>100){
            score++;
            startTime= System.nanoTime();
        }
        animation.update();
        if(up){
            dy-=2;
        }else{
            dy+=2;
        }
        if(dy>14)dy=14;
        if(dy<-14)dy=-14;
        y+=dy*2;
        dy=0;

    }
    public void draw(Canvas canvas){
        canvas.drawBitmap(animation.getImage(),x,y,null);
    }
    public int getScore() {
        return score;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }
    public void resetDY(){
        dy=0;
    }
    public void resetScore(){
        score=0;
    }
}
