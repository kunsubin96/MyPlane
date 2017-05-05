package com.example.kunsubin.myplane;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by kunsubin on 4/16/2017.
 */

public class BotBorder extends GameObject{
    private Bitmap bitmap;

    public BotBorder(Bitmap res, int x, int y) {
        height = 200;
        width = 20;

        this.x=x;
        this.y=y;
        dx=GamePanel.MOVESPEED;

        bitmap=Bitmap.createBitmap(res,0,0,width,height);
    }
    public void update(){
        x+=dx;
    }
    public void draw(Canvas canvas){
        try {
            canvas.drawBitmap(bitmap,x,y,null);
        }catch (Exception ex){

        }
    }
}
