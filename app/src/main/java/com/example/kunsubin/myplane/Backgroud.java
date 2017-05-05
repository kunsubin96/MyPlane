package com.example.kunsubin.myplane;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by kunsubin on 4/14/2017.
 */

public class Backgroud {
    private Bitmap bitmap;
    private int x, y, dx;
    public Backgroud(Bitmap bitmap){
        this.bitmap=bitmap;
        dx=GamePanel.MOVESPEED;

    }
    public void update(){

        x+=dx;
        if(x<-GamePanel.WIDTH)
            x=0;
    }
    public void draw(Canvas canvas){
        canvas.drawBitmap(bitmap,x,y,null);
        if(x<0){
            canvas.drawBitmap(bitmap,x+GamePanel.WIDTH,y,null);
        }
    }
}
