package com.example.kunsubin.myplane;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by kunsubin on 4/14/2017.
 */

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    private MainThread mainThread;
    private Backgroud backgroud;
    private Player player;
    public static final int WIDTH=856;
    public static final int HEIGHT=480;
    public static final int MOVESPEED=-5;
    public ArrayList<Smokepuff> smokepuffs;
    public ArrayList<Missile> missiles;
    public ArrayList<TopBorder> topBorders;
    public ArrayList<BotBorder> botBorders;
    private long smokeStartTime;
    private long missilesStartTime;
    private Random random=new Random();
    private int maxHeightBorder;
    private int minHeightBorder;

    private boolean topDown=true;
    private boolean botDown=true;
    private boolean newGameCreated;

    private int progressDenom=20;
    //
    private Explosion explosion;
    private long startReset;
    private boolean reset;
    private boolean disspapear;
    private boolean started;
    private int best;


    public GamePanel(Context context) {
        super(context);
        //add event the call back
        getHolder().addCallback(this);


        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        backgroud=new Backgroud(BitmapFactory.decodeResource(getResources(),R.drawable.backgroud));
       player=new Player(BitmapFactory.decodeResource(getResources(),R.drawable.helicopter),65,35,3);
        smokepuffs=new ArrayList<Smokepuff>();
        missiles=new ArrayList<Missile>();
        topBorders=new ArrayList<TopBorder>();
        botBorders=new ArrayList<BotBorder>();
        smokeStartTime= System.nanoTime();
        missilesStartTime= System.nanoTime();
        mainThread=new MainThread(getHolder(),this);


        mainThread.setRunning(true);
        mainThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry=true;
        int couter=0;
        while (retry&&couter<1000){
            couter++;
            try {
                mainThread.setRunning(false);
                mainThread.join();
                retry=false;
                mainThread=null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
       if(event.getAction()==MotionEvent.ACTION_DOWN){
            if(!player.isPlaying()&&newGameCreated&&reset){
                player.setPlaying(true);
                player.setUp(true);
            }
            if(player.isPlaying()){
                if(!started) started=true;
                reset=false;
                player.setUp(true);
            }
            return true;
        }
        if(event.getAction()==MotionEvent.ACTION_UP)
        {
            player.setUp(false);
            return true;
        }
        return super.onTouchEvent(event);
    }
    public void update(){
        if(player.isPlaying()){

            if(topBorders.isEmpty()){
                player.setPlaying(false);
                return;
            }
            if(botBorders.isEmpty()){
                player.setPlaying(false);
                return;
            }

            backgroud.update();
            player.update();

            maxHeightBorder=30+player.getScore()/progressDenom;
            if(maxHeightBorder>HEIGHT/4)maxHeightBorder=HEIGHT/4;
            minHeightBorder=5+player.getScore()/progressDenom;

            //kiem tra va cham may bay voi thanh tren
            for (int i=0;i<topBorders.size();i++){
                if(collision(topBorders.get(i),player)){
                    player.setPlaying(false);
                }
            }
            //kiem tra va cham may bay voi thanh duoi
            for (int i=0;i<botBorders.size();i++){
                if(collision(botBorders.get(i),player)){
                    player.setPlaying(false);
                }
            }
            updateTopBorder();
            updateBotBorder();


            long missilesElapsed=(System.nanoTime()-missilesStartTime)/1000000;
            if(missilesElapsed>(1000-(player.getScore())/4)){
                if(missiles.size()==0){
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile),WIDTH+10,HEIGHT/2,45,15,player.getScore(),13));

                }
                else {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile),WIDTH+10,
                            (int)(random.nextDouble()*(HEIGHT-maxHeightBorder*2))+maxHeightBorder,45,15,player.getScore(),13));
                }
                missilesStartTime=System.nanoTime();
            }
            for(int i=0;i<missiles.size();i++){
                missiles.get(i).update();
                //nếu xảy ra va chạm giữa đạn với máy bay thì dừng game
                if(collision(missiles.get(i),player)){
                    missiles.remove(i);
                    player.setPlaying(false);
                }
                if(missiles.get(i).getX()<-100){
                    missiles.remove(i);
                    break;
                }
            }


            long elapsed=(System.nanoTime()-smokeStartTime)/1000000;
            if(elapsed>120){
                smokepuffs.add(new Smokepuff(player.getX(),player.getY()+10));
                smokeStartTime= System.nanoTime();
            }
            for(int i=0;i<smokepuffs.size();i++){
                smokepuffs.get(i).update();
                if(smokepuffs.get(i).getX()<-10)
                    smokepuffs.remove(i);
            }

        }else{
            player.resetDY();
            if(!reset){
                newGameCreated=false;
                startReset=System.nanoTime();
                reset=true;
                disspapear=true;
                explosion=new Explosion(BitmapFactory.decodeResource(getResources(),R.drawable.explosion),player.getX(),player.getY()-30,
                        100,100,25);
            }
            explosion.update();
            long resetElapsed=(System.nanoTime()-startReset)/1000000;
            if(resetElapsed>2500&&!newGameCreated){
                newGame();
            }
        }

    }
    //check va chạm đạn với máy bay
    public boolean collision(GameObject a, GameObject b){
        if(Rect.intersects(a.getRectangle(),b.getRectangle())){
            return true;
        }
        return false;
    }
    @Override
    public void draw(Canvas canvas){
        final float scaleFactorX=getWidth()/(float)WIDTH;
        final float scaleFactorY=getHeight()/(float)HEIGHT;
        if(canvas!=null){
            final int scaleState=canvas.save();
            canvas.scale(scaleFactorX,scaleFactorY);
            backgroud.draw(canvas);
            if(!disspapear){
                player.draw(canvas);
            }

            for (Smokepuff sp:smokepuffs){
                sp.draw(canvas);
            }

            for(Missile m:missiles){
                m.draw(canvas);
            }

            for(TopBorder tb:topBorders){
                tb.draw(canvas);
            }
            for (BotBorder bb:botBorders){
                bb.draw(canvas);
            }

            if(started){
                explosion.draw(canvas);
            }
            drawText(canvas);
            canvas.restoreToCount(scaleState);

        }

    }
    public void updateTopBorder(){
        if(player.getScore()%50==0){
            topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),topBorders.get(topBorders.size()-1).getX()+20,0,
                    (int)(random.nextDouble()*maxHeightBorder)+1));
        }
        for(int i=0;i<topBorders.size();i++){
            topBorders.get(i).update();
            if(topBorders.get(i).getX()<-20){
                topBorders.remove(i);

                if(topBorders.get(topBorders.size()-1).getHeight()>=maxHeightBorder){
                    topDown=false;
                }

                if(topBorders.get(topBorders.size()-1).getHeight()<=minHeightBorder){
                    topDown=true;
                }
                if(topDown){
                    topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),topBorders.get(topBorders.size()-1).getX()+20,0,
                            topBorders.get(topBorders.size()-1).getHeight()+1));
                }else{
                    topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),topBorders.get(topBorders.size()-1).getX()+20,0,
                            topBorders.get(topBorders.size()-1).getHeight()-1));
                }
            }
        }
    }
    public void updateBotBorder(){
        if(player.getScore()%40==0){
            botBorders.add(new BotBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),botBorders.get(botBorders.size()-1).getX()+20,
                    (int)(random.nextDouble()*maxHeightBorder)+HEIGHT-maxHeightBorder));
        }
        for (int i=0;i<botBorders.size();i++){
            botBorders.get(i).update();

            if(botBorders.get(i).getX()<-20){
                botBorders.remove(i);

                if(botBorders.get(botBorders.size()-1).getY()<=HEIGHT-maxHeightBorder){
                    botDown=true;
                }

                if(botBorders.get(botBorders.size()-1).getY()>=HEIGHT-minHeightBorder){
                    botDown=false;
                }
                if(botDown){
                    botBorders.add(new BotBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),botBorders.get(botBorders.size()-1).getX()+20,
                            botBorders.get(botBorders.size()-1).getY()+1));
                }else {
                    botBorders.add(new BotBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),botBorders.get(botBorders.size()-1).getX()+20,
                            botBorders.get(botBorders.size()-1).getY()-1));
                }
            }

        }
    }
    public void newGame(){
        disspapear=false;
        topBorders.clear();
        botBorders.clear();
        missiles.clear();
        smokepuffs.clear();

        minHeightBorder=5;
        maxHeightBorder=30;
        player.resetScore();
        player.resetDY();
        player.setY(HEIGHT/2);


        //top
        for (int i=0;i*20<WIDTH+40;i++){
            if(i==0){
                topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),i*20,0,10));
            }else {
                topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),i*20,0,topBorders.get(i-1).getHeight()+1));
            }
        }
        //bottom
        for (int i=0;i*20<WIDTH+40;i++){
            if(i==0){
                botBorders.add(new BotBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),i*20,HEIGHT-minHeightBorder));
            }else {
                botBorders.add(new BotBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),i*20,botBorders.get(i-1).getY()-1));
            }
        }

        newGameCreated =true;
    }
    public void drawText(Canvas canvas){
        Paint paint=new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        if(player.getScore()>best){
            best=player.getScore();
        }
        canvas.drawText("DISTANCE: "+(player.getScore()),10,HEIGHT-10,paint);
        canvas.drawText("BEST: "+best,WIDTH-170,HEIGHT-10,paint);

        if(!player.isPlaying()&&newGameCreated&&reset){
            Paint paint1=new Paint();
            paint1.setColor(Color.BLACK);
            paint1.setTextSize(40);
            paint1.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            canvas.drawText("PRESS TO START",WIDTH/2-150,HEIGHT/2,paint1);
        }
    }
}
