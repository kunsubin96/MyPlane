package com.example.kunsubin.myplane;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

/**
 * Created by kunsubin on 4/16/2017.
 */

public class Missile extends GameObject {

    private int score;
    private int speed;
    private Random random=new Random();
    private Animation animation=new Animation();
    private Bitmap spritesheet;
    public Missile(Bitmap res, int x, int y, int w, int h,int s, int numFrames){
        super.x=x;
        super.y=y;
        width=w;
        height=h;
        score=s;
        speed=10+(int)(random.nextDouble()*score/30);

        if(speed>40) speed=40;
        spritesheet=res;
        Bitmap[] bitmaps=new Bitmap[numFrames];
        for (int i=0;i<bitmaps.length;i++){
            bitmaps[i]=Bitmap.createBitmap(spritesheet,0,i*height,width,height);
        }
        animation.setFrames(bitmaps);
        animation.setDelay(100-speed);

    }
    public void update(){
        x-=speed;
        animation.update();
    }
    public void draw(Canvas canvas){
        try{
            canvas.drawBitmap(animation.getImage(),x,y,null);
        }catch (Exception ex){

        }
    }
    public int getWidth(){
        return width-10;
    }

}
