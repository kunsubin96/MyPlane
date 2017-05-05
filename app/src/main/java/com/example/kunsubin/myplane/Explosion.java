package com.example.kunsubin.myplane;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by kunsubin on 4/16/2017.
 */

public class Explosion {
    private int x;
    private int y;
    private int width;
    private int height;
    private int row;
    private Bitmap spritesheet;
    private Animation animation=new Animation();
    public Explosion(Bitmap res, int x, int y, int w, int h,int numFrames){
        this.x=x;
        this.y=y;
        this.width=w;
        this.height=h;
        spritesheet=res;

        Bitmap[] bitmaps=new Bitmap[numFrames];
        for (int i=0;i<bitmaps.length;i++){
            if(i%5==0&&i>0)row++;
            bitmaps[i]=Bitmap.createBitmap(spritesheet,(i-5*row)*width,row*height,width,height);
        }
        animation.setFrames(bitmaps);
        animation.setDelay(19);
    }
    public void update(){
        if(!animation.isPlayedOnce()){
            animation.update();
        }
    }
    public void draw(Canvas canvas){
        if(!animation.isPlayedOnce()){
            canvas.drawBitmap(animation.getImage(),x,y,null);
        }

    }
    public int getHeight(){
        return height;
    }
}
