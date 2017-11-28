package com.example.user.hoppingball;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.security.Policy;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity
        implements SurfaceHolder.Callback{

    int mSurfaceWidth;
    int getmSurfaceHeight;
    Point point;

    //難易度
    boolean easyMode;
    boolean normalMode;
    boolean hardMode;

    //トップスコア
    int firstScore;
    int secondScore;
    int thirdScore;

    //SurfaceViewの上下左右
    int itibanUe = 0;
    int itibanShita = 1560;
    int itibanMigi = 1000;//※適当
    int itibanHidari = 0;

    //zigunの持つデータ
    int zigunX;
    int zigunY;
    int zigunHankei = 50;
    int zigunHayasa;
    int zigunTobuTikara = -40;

    //kabe共通データ
    int kabeNoSukima = 500;
    int kabeNoAtusa = 100;
    int kabeNoSokudo = 4;

    //kabe1の持つデータ
    int kabe1UeNoHidari;
    int kabe1UeNoSoko;
    int kabe1ShitaNoHidari;
    int kabe1ShitaNoTakasa;
    boolean kabe1Ganai = true;

    //kabe2の持つデータ
    int kabe2UeNoHidari = -1;
    int kabe2UeNoSoko = 100;
    int kabe2ShitaNoHidari = -1;
    int kabe2ShitaNoTakasa = kabe2UeNoSoko + kabeNoSukima;
    boolean kabe2GaNai = false;

    ///////////////////////////////要編集/////////////////////////////////////
    boolean aaaaa = true;

    //trueの間タップ操作を受け付けない→連続ジャンプを防ぐため
    boolean koutyoku = false;

    //trueの間タップ操作を受け付けない→スタート画面
    //trueの間は描写し続ける
    boolean gamenUgoku = false;

    //zigunの描写用
    SurfaceView zigunSurfaceView;
    SurfaceHolder zigunHolder;

    //得点関係
    int tokuten = 0;
    boolean tokutenFlag = true;


    //0:RED, 1:BLUE, 2:GREEN, 3:WHITE, 4:BLACK
    int haikenoiro = 0;

    //FPS管理
    Handler tienHandler;
    Runnable tienRunnable;

    //背景管理
    Rect haike1MoveRect;
    Rect haike2MoveRect;
    Rect haike3MoveRect;
    Rect haike1Rect;
    Rect haike2Rect;
    Rect haike3Rect;

    int haike1X;
    int haike1Y;
    int haike2X;
    int haike2Y;
    int haike3X;
    int haike3Y;

    boolean haike1GaNai;
    boolean haike2GaNai;
    boolean haike3GaNai;

    int haikeSokudo = 2;
    int haikeHaba = 540;

    float f;
    float x;
    float y;

    Bitmap haike;

    //基準とするディスプレイサイズとの比率
    DisplayRatio displayRatio;

    //timerを使えるように
    Timer timer = new Timer();
    TimerTask timerTask = new MainTimerTask();
    Handler timerHandler = new Handler();


    //onCreateの中で定義したtimerのscheduleに沿って、一定時間ごとに処理を行う
    public class MainTimerTask extends TimerTask{
        @Override
        public void run() {

            if(gamenUgoku) {

                timerHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        //zigunに重力を与え座標を求める
                        zyuryoku();
                        //kabeの座標を求める
                        kabeNoUgoki();
                        //背景の処理を計算
                        haikeiNoUgoki();
                        //zigunのあたり判定の処理
                        atariHante();
                        //zigunを描写
                        zigunDrawCanvas();

                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //zigun描写用のSurfaceView
        zigunSurfaceView = (SurfaceView) findViewById(R.id.surfaceView_circle);
        //背景用のImageView
        ImageView EMT = (ImageView)findViewById(R.id.EMTHB);

        EMT.setImageResource(R.drawable.danboru);

        Bitmap haikei = BitmapFactory.decodeResource(getResources(), R.drawable.droid_kabegami);




        //zigunSurfaceViewとzigunHolderを同期させる
        zigunHolder = zigunSurfaceView.getHolder();
        //zigunSurfaceViewを監視する※使わなくても書かなければならない
        zigunHolder.addCallback(this);

        //zigunSurfaceViewの背景を半透明に
        zigunHolder.setFormat(PixelFormat.TRANSLUCENT);
        //zigunSurfaceViewを一番前に持ってくる
        ////zigunSurfaceView.setZOrderOnTop(true);
        Button continueButton = (Button)findViewById(R.id.continueSuru);

        continueButton.setVisibility(View.GONE);
        zigunSurfaceView.setVisibility(View.INVISIBLE);

        haike = BitmapFactory.decodeResource(getResources(), R.drawable.droid_kabegami);

        //  10 / 1000秒ごとに処理を行わせる
        timer.schedule(timerTask, 0, 12);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Point point = new Point(0,0);
        displayRatio = new DisplayRatio();

        point.set(zigunSurfaceView.getWidth(),zigunSurfaceView.getHeight());
        displayRatio.referenceWidth = 1080;
        displayRatio.referenceHeight = 1572;
        displayRatio.displayRatioCast(point.x,point.y);

    }

    //zigunに重力を与え、座標を計算する
    void zyuryoku (){

        zigunHayasa =  zigunHayasa + 1;
        zigunY = zigunY + zigunHayasa / 2;
    }

    //zigunの当たり判定
    void atariHante (){

        //zigunが画面上部に当たったときの処理
        if(zigunY < 50){
            zigunY = 50;
            zigunHayasa = -1;
        }
        //zigunが画面下部に当たったときの処理
        if(zigunY > 1510){
            zigunY = 1510;
            gamenUgoku = false;

            return;
        }

        if(kabe2GaNai) {

            if (zigunX + zigunHankei >= kabe1UeNoHidari && kabe1UeNoHidari >= zigunX) {

                if (zigunY < kabe1UeNoSoko || kabe1ShitaNoTakasa < zigunY) {
                    gamenUgoku = false;
                    return;
                }

                if (Math.pow((zigunX - kabe1UeNoHidari), 2) + Math.pow((zigunY - kabe1UeNoSoko), 2) <= Math.pow(zigunHankei, 2)
                        || Math.pow((zigunX - kabe1ShitaNoHidari), 2) + Math.pow((zigunY - kabe1ShitaNoTakasa), 2) <= Math.pow(zigunHankei, 2)) {
                    gamenUgoku = false;
                    return;
                }
            }
            if (zigunX > kabe1UeNoHidari && kabe1UeNoHidari + kabeNoAtusa > zigunX) {

                if (zigunY < kabe1UeNoSoko + zigunHankei || kabe1ShitaNoTakasa < zigunY + zigunHankei) {
                    gamenUgoku = false;
                    return;
                }
            }
            if (zigunX >= kabe1UeNoHidari + kabeNoAtusa && kabe1UeNoHidari + kabeNoAtusa + zigunHankei >= zigunX) {

                if (Math.pow((zigunX - kabe1UeNoHidari + kabeNoAtusa), 2) + Math.pow((zigunY - kabe1UeNoSoko), 2) <= Math.pow(zigunHankei, 2)
                        || Math.pow((zigunX - kabe1ShitaNoHidari + kabeNoAtusa), 2) + Math.pow((zigunY - kabe1ShitaNoTakasa), 2) <= Math.pow(zigunHankei, 2)) {
                    gamenUgoku = false;
                    return;
                }

                if (tokutenFlag){
                    tokuten++;
                    tokutenFlag = false;
                }
            }

            /////////////////////


            if (zigunX + zigunHankei >= kabe1UeNoHidari && kabe1UeNoHidari >= zigunX) {

                if (zigunY < kabe1UeNoSoko || kabe1ShitaNoTakasa < zigunY) {
                    gamenUgoku = false;
                    return;
                }

                if (Math.pow((zigunX - kabe1UeNoHidari), 2) + Math.pow((zigunY - kabe1UeNoSoko), 2) <= Math.pow(zigunHankei, 2)
                        || Math.pow((zigunX - kabe1ShitaNoHidari), 2) + Math.pow((zigunY - kabe1ShitaNoTakasa), 2) <= Math.pow(zigunHankei, 2)) {
                    gamenUgoku = false;
                    return;
                }
            }
            if (zigunX > kabe1UeNoHidari && kabe1UeNoHidari + kabeNoAtusa > zigunX) {

                if (zigunY < kabe1UeNoSoko + zigunHankei || kabe1ShitaNoTakasa < zigunY + zigunHankei) {
                    gamenUgoku = false;
                    return;
                }
            }
            if (zigunX >= kabe1UeNoHidari + kabeNoAtusa && kabe1UeNoHidari + kabeNoAtusa + zigunHankei >= zigunX) {

                if ((int) Math.pow(zigunX - kabe1UeNoHidari - kabeNoAtusa, 2) + (int) Math.pow((zigunY - kabe1UeNoSoko), 2) <= (int) Math.pow(zigunHankei, 2)
                        || Math.pow((zigunX - kabe1ShitaNoHidari - kabeNoAtusa), 2) + Math.pow((zigunY - kabe1ShitaNoTakasa), 2) <= Math.pow(zigunHankei, 2)) {
                    gamenUgoku = false;
                    return;
                }
            }
        }

        if (kabe1Ganai){

            if (zigunX + zigunHankei >= kabe2UeNoHidari && kabe2UeNoHidari >= zigunX) {

                if (zigunY < kabe2UeNoSoko || kabe2ShitaNoTakasa < zigunY) {
                    gamenUgoku = false;
                    return;
                }

                if (Math.pow((zigunX - kabe2UeNoHidari), 2) + Math.pow((zigunY - kabe2UeNoSoko), 2) <= Math.pow(zigunHankei, 2)
                        || Math.pow((zigunX - kabe2ShitaNoHidari), 2) + Math.pow((zigunY - kabe2ShitaNoTakasa), 2) <= Math.pow(zigunHankei, 2)) {
                    gamenUgoku = false;
                    return;
                }
            }
            if (zigunX > kabe2UeNoHidari && kabe2UeNoHidari + kabeNoAtusa > zigunX) {

                if (zigunY < kabe2UeNoSoko + zigunHankei || kabe2ShitaNoTakasa < zigunY + zigunHankei) {
                    gamenUgoku = false;
                    return;
                }
            }
            if (zigunX >= kabe2UeNoHidari + kabeNoAtusa && kabe2UeNoHidari + kabeNoAtusa + zigunHankei >= zigunX) {

                if (Math.pow((zigunX - kabe2UeNoHidari + kabeNoAtusa), 2) + Math.pow((zigunY - kabe2UeNoSoko), 2) <= Math.pow(zigunHankei, 2)
                        || Math.pow((zigunX - kabe2ShitaNoHidari + kabeNoAtusa), 2) + Math.pow((zigunY - kabe2ShitaNoTakasa), 2) <= Math.pow(zigunHankei, 2)) {
                    gamenUgoku = false;
                    return;
                }
            }

            /////////////////////


            if (zigunX + zigunHankei >= kabe2UeNoHidari && kabe2UeNoHidari >= zigunX) {

                if (zigunY < kabe2UeNoSoko || kabe2ShitaNoTakasa < zigunY) {
                    //Button button = (Button) findViewById(R.id.startyo);
                    //button.setVisibility(View.VISIBLE);
                    gamenUgoku = false;
                    return;
                }

                if (Math.pow((zigunX - kabe2UeNoHidari), 2) + Math.pow((zigunY - kabe2UeNoSoko), 2) <= Math.pow(zigunHankei, 2)
                        || Math.pow((zigunX - kabe2ShitaNoHidari), 2) + Math.pow((zigunY - kabe2ShitaNoTakasa), 2) <= Math.pow(zigunHankei, 2)) {

                    //Button button = (Button) findViewById(R.id.startyo);
                    //button.setVisibility(View.VISIBLE);
                    gamenUgoku = false;
                    return;
                }
            }
            if (zigunX > kabe2UeNoHidari && kabe2UeNoHidari + kabeNoAtusa > zigunX) {

                if (zigunY < kabe2UeNoSoko + zigunHankei || kabe2ShitaNoTakasa < zigunY + zigunHankei) {

                    //Button button = (Button) findViewById(R.id.startyo);
                    //button.setVisibility(View.VISIBLE);
                    gamenUgoku = false;
                    return;
                }
            }
            if (zigunX >= kabe2UeNoHidari + kabeNoAtusa && kabe2UeNoHidari + kabeNoAtusa + zigunHankei >= zigunX) {

                if ((int) Math.pow(zigunX - kabe2UeNoHidari - kabeNoAtusa, 2) + (int) Math.pow((zigunY - kabe2UeNoSoko), 2) <= (int) Math.pow(zigunHankei, 2)
                        || Math.pow((zigunX - kabe2ShitaNoHidari - kabeNoAtusa), 2) + Math.pow((zigunY - kabe2ShitaNoTakasa), 2) <= Math.pow(zigunHankei, 2)) {

                    // Button button = (Button) findViewById(R.id.startyo);
                    // button.setVisibility(View.VISIBLE);
                    gamenUgoku = false;
                    return;
                }

                if (tokutenFlag){
                    tokuten++;
                    tokutenFlag = false;
                }
            }

        }
    }

    private void zigunDrawCanvas(){

        if(gamenUgoku) {
            Canvas zigunCanvas = zigunHolder.lockCanvas();
            zigunCanvas.scale(displayRatio.ratio,displayRatio.ratio);
            Paint zigunPaint = new Paint();
            Paint kabePaint = new Paint();
            Paint tokutenPaint = new Paint();
            Paint TopScore = new Paint();
            zigunPaint.setColor(Color.RED);
            kabePaint.setColor(Color.BLUE);
            tokutenPaint.setColor(Color.rgb(255,100,30));
            tokutenPaint.setTextSize(200);
            TopScore.setColor(Color.rgb(255,100,30));
            TopScore.setTextSize(100);
            zigunCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            zigunCanvas.drawBitmap(haike,haike1Rect,haike1MoveRect,null);
            zigunCanvas.drawBitmap(haike,haike2Rect,haike2MoveRect,null);
            zigunCanvas.drawBitmap(haike,haike3Rect,haike3MoveRect,null);
            zigunCanvas.drawRect(kabe1UeNoHidari, itibanUe, kabe1UeNoHidari + kabeNoAtusa, kabe1UeNoSoko, kabePaint);
            zigunCanvas.drawRect(kabe1ShitaNoHidari, kabe1ShitaNoTakasa, kabe1ShitaNoHidari + kabeNoAtusa, itibanShita, kabePaint);
            zigunCanvas.drawRect(kabe2UeNoHidari, itibanUe, kabe2UeNoHidari + kabeNoAtusa, kabe2UeNoSoko, kabePaint);
            zigunCanvas.drawRect(kabe2ShitaNoHidari, kabe2ShitaNoTakasa, kabe2ShitaNoHidari + kabeNoAtusa, itibanShita, kabePaint);
            zigunCanvas.drawCircle(zigunX, zigunY, zigunHankei, zigunPaint);
            zigunCanvas.drawColor(Color.TRANSPARENT);
            zigunCanvas.drawText("score",itibanHidari + 100, itibanShita / 3 - 200,TopScore);
            zigunCanvas.drawText(tokuten + "", itibanHidari + 100, itibanShita / 3, tokutenPaint);
            zigunCanvas.drawText("1st  :" + firstScore,itibanHidari + 100, itibanShita - 300,TopScore);
            zigunCanvas.drawText("2nd :" + secondScore,itibanHidari + 100, itibanShita - 200,TopScore);
            zigunCanvas.drawText("3rd  :" + thirdScore,itibanHidari + 100, itibanShita - 100,TopScore);
            zigunHolder.unlockCanvasAndPost(zigunCanvas);
        }else if(aaaaa){
            comparisonScore();
            saveScore();
            Canvas zigunCanvas = zigunHolder.lockCanvas();
            zigunCanvas.scale(displayRatio.ratio,displayRatio.ratio);
            Paint zigunPaint = new Paint();
            Paint kabePaint = new Paint();
            Paint tokutenPaint = new Paint();
            Paint TopScore = new Paint();
            zigunCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            zigunPaint.setColor(Color.RED);
            kabePaint.setColor(Color.BLUE);
            tokutenPaint.setColor(Color.rgb(255,100,30));
            tokutenPaint.setTextSize(200);
            TopScore.setColor(Color.rgb(255,100,30));
            TopScore.setTextSize(100);
            zigunCanvas.drawBitmap(haike,haike1Rect,haike1MoveRect,null);
            zigunCanvas.drawBitmap(haike,haike2Rect,haike2MoveRect,null);
            zigunCanvas.drawBitmap(haike,haike3Rect,haike3MoveRect,null);
            zigunCanvas.drawCircle(zigunX, zigunY, zigunHankei, zigunPaint);
            zigunCanvas.drawRect(kabe1UeNoHidari, itibanUe, kabe1UeNoHidari + kabeNoAtusa, kabe1UeNoSoko, kabePaint);
            zigunCanvas.drawRect(kabe1ShitaNoHidari, kabe1ShitaNoTakasa, kabe1ShitaNoHidari + kabeNoAtusa, itibanShita, kabePaint);
            zigunCanvas.drawRect(kabe2UeNoHidari, itibanUe, kabe2UeNoHidari + kabeNoAtusa, kabe2UeNoSoko, kabePaint);
            zigunCanvas.drawRect(kabe2ShitaNoHidari, kabe2ShitaNoTakasa, kabe2ShitaNoHidari + kabeNoAtusa, itibanShita, kabePaint);
            zigunCanvas.drawText("score",itibanHidari + 100, itibanShita / 3 - 200,TopScore);
            zigunCanvas.drawText(tokuten + "", itibanHidari + 100, itibanShita / 3, tokutenPaint);
            zigunCanvas.drawText("1st  :" + firstScore,itibanHidari + 100, itibanShita - 300,TopScore);
            zigunCanvas.drawText("2nd :" + secondScore,itibanHidari + 100, itibanShita - 200,TopScore);
            zigunCanvas.drawText("3rd  :" + thirdScore,itibanHidari + 100, itibanShita - 100,TopScore);
            zigunCanvas.drawColor(Color.TRANSPARENT);
            zigunHolder.unlockCanvasAndPost(zigunCanvas);
            Button continueButton = (Button)findViewById(R.id.continueSuru);
            continueButton.setVisibility(View.VISIBLE);
            aaaaa = false;
        }

    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(koutyoku){

        }else {

            zigunHayasa = zigunTobuTikara;
            koutyoku = true;

        }

        if(event.getAction() == MotionEvent.ACTION_UP){
            koutyoku = false;
        }

        return true;
    }

    void kabeNoUgoki(){

        if(kabe1UeNoHidari < 200 && kabe2GaNai){
            Random random = new Random();
            int randomNum = random.nextInt(710) + 200;
            kabe2UeNoSoko = randomNum;
            kabe2ShitaNoTakasa = randomNum + kabeNoSukima;
            kabe2UeNoHidari = itibanMigi + 100;
            kabe2ShitaNoHidari = itibanMigi + 100;
            kabe1Ganai = true;
            kabe2GaNai = false;
            tokutenFlag = true;
        }

        if(kabe2UeNoHidari < 200 && kabe1Ganai){
            Random random = new Random();
            int randomNum = random.nextInt(710) + 200;
            kabe1UeNoSoko = randomNum;
            kabe1ShitaNoTakasa = randomNum + kabeNoSukima;
            kabe1UeNoHidari = itibanMigi + 100;
            kabe1ShitaNoHidari = itibanMigi + 100;
            kabe1Ganai = false;
            kabe2GaNai = true;
            tokutenFlag = true;
        }

        kabe1UeNoHidari = kabe1UeNoHidari - kabeNoSokudo;
        kabe1ShitaNoHidari = kabe1UeNoHidari;

        kabe2UeNoHidari = kabe2UeNoHidari - kabeNoSokudo;
        kabe2ShitaNoHidari = kabe2UeNoHidari;

    }

    void haikeiNoUgoki(){

        haike1MoveRect = new Rect(0,0,haikeHaba, itibanShita);
        haike2MoveRect = new Rect(0,0,haikeHaba, itibanShita);
        haike3MoveRect = new Rect(0,0,haikeHaba, itibanShita);

        haike1Rect = new Rect(0,0,haikeHaba, itibanShita);
        haike2Rect = new Rect(haikeHaba, 0, haikeHaba * 2, itibanShita);
        haike3Rect = new Rect(haikeHaba * 2,0, haikeHaba * 3, itibanShita);

        if (haike2X < 0 && haike1GaNai){
            haike1X = haike3X + haikeHaba;
            haike1GaNai = false;
            haike2GaNai = true;
        }

        if (haike3X < 0 && haike2GaNai){
            haike2X = haike1X + haikeHaba;
            haike2GaNai = false;
            haike3GaNai = true;
        }

        if (haike1X < 0 && haike3GaNai){
            haike3X = haike2X + haikeHaba;
            haike3GaNai = false;
            haike1GaNai = true;
        }

        haike1X = haike1X - haikeSokudo;
        haike2X = haike2X - haikeSokudo;
        haike3X = haike3X - haikeSokudo;

        haike1MoveRect.offset(haike1X, haike1Y);
        haike2MoveRect.offset(haike2X, haike2Y);
        haike3MoveRect.offset(haike3X, haike3Y);



    }


    public void continueButton(View v){

        zigunSurfaceView.setVisibility(View.INVISIBLE);
        Button continueButton = (Button)findViewById(R.id.continueSuru);
        Button easyButton = (Button)findViewById(R.id.easyModeStart);
        Button normalButton = (Button)findViewById(R.id.normalModeStart);
        Button hardButton = (Button)findViewById(R.id.hardModeStart);
        continueButton.setVisibility(View.GONE);
        easyButton.setVisibility(View.VISIBLE);
        normalButton.setVisibility(View.VISIBLE);
        hardButton.setVisibility(View.VISIBLE);





    }

    public void easyButton (View v){

        Button easyButton = (Button)findViewById(R.id.easyModeStart);
        Button normalButton = (Button)findViewById(R.id.normalModeStart);
        Button hardButton = (Button)findViewById(R.id.hardModeStart);

        easyButton.setVisibility(View.GONE);
        normalButton.setVisibility(View.GONE);
        hardButton.setVisibility(View.GONE);
        zigunSurfaceView.setVisibility(View.VISIBLE);

        easyMode = true;
        normalMode = false;
        hardMode = false;
        aaaaa = true;
        gamenUgoku = true;
        zigunX = 520;
        zigunY = 300;
        zigunHankei = 50;
        zigunHayasa = 0;

        //kabe1の持つデータ
        kabe1UeNoHidari = -100;
        kabe1UeNoSoko = 100;
        kabe1ShitaNoHidari = - 100;
        kabe1ShitaNoTakasa = kabe1UeNoSoko + kabeNoSukima;
        kabe1Ganai = true;

        //kabe2の持つデータ
        kabe2UeNoHidari = -100;
        kabe2UeNoSoko = 100;
        kabe2ShitaNoHidari = -100;
        kabe2ShitaNoTakasa = kabe2UeNoSoko + kabeNoSukima;
        kabe2GaNai = false;

        //得点のリセット
        tokuten = 0;

        //難易度関係
        kabeNoSukima = 600;
        zigunTobuTikara = -35;
        kabeNoAtusa = 80;
        kabeNoSokudo = 6;

        //背景
        haike1X = 0;
        haike2X = haikeHaba;
        haike3X = haikeHaba * 2;
        haike1Y = 0;
        haike2Y = 0;
        haike3Y = 0;
        haike1GaNai = true;
        haike2GaNai = false;
        haike3GaNai = false;

        loadScore();
    }


    public void normalButton (View v){

        Button easyButton = (Button)findViewById(R.id.easyModeStart);
        Button normalButton = (Button)findViewById(R.id.normalModeStart);
        Button hardButton = (Button)findViewById(R.id.hardModeStart);

        easyButton.setVisibility(View.GONE);
        normalButton.setVisibility(View.GONE);
        hardButton.setVisibility(View.GONE);
        zigunSurfaceView.setVisibility(View.VISIBLE);

        easyMode = false;
        normalMode = true;
        hardMode = false;
        aaaaa = true;
        gamenUgoku = true;
        zigunX = 520;
        zigunY = 300;
        zigunHankei = 50;
        zigunHayasa = 0;

        //kabe1の持つデータ
        kabe1UeNoHidari = -100;
        kabe1UeNoSoko = 100;
        kabe1ShitaNoHidari = - 100;
        kabe1ShitaNoTakasa = kabe1UeNoSoko + kabeNoSukima;
        kabe1Ganai = true;

        //kabe2の持つデータ
        kabe2UeNoHidari = -100;
        kabe2UeNoSoko = 100;
        kabe2ShitaNoHidari = -100;
        kabe2ShitaNoTakasa = kabe2UeNoSoko + kabeNoSukima;
        kabe2GaNai = false;

        //得点のリセット
        tokuten = 0;

        //難易度関係
        kabeNoSukima = 600;
        zigunTobuTikara = -40;
        kabeNoAtusa = 100;
        kabeNoSokudo = 6;

        //背景
        haike1X = 0;
        haike2X = haikeHaba;
        haike3X = haikeHaba * 2;
        haike1Y = 0;
        haike2Y = 0;
        haike3Y = 0;
        haike1GaNai = true;
        haike2GaNai = false;
        haike3GaNai = false;

        loadScore();
    }

    public void hardButton (View v){

        Button easyButton = (Button)findViewById(R.id.easyModeStart);
        Button normalButton = (Button)findViewById(R.id.normalModeStart);
        Button hardButton = (Button)findViewById(R.id.hardModeStart);

        easyButton.setVisibility(View.GONE);
        normalButton.setVisibility(View.GONE);
        hardButton.setVisibility(View.GONE);
        zigunSurfaceView.setVisibility(View.VISIBLE);

        easyMode = false;
        normalMode = false;
        hardMode = true;
        aaaaa = true;
        gamenUgoku = true;
        zigunX = 520;
        zigunY = 300;
        zigunHankei = 50;
        zigunHayasa = 0;

        //kabe1の持つデータ
        kabe1UeNoHidari = -300;
        kabe1UeNoSoko = 100;
        kabe1ShitaNoHidari = - 300;
        kabe1ShitaNoTakasa = kabe1UeNoSoko + kabeNoSukima;
        kabe1Ganai = true;

        //kabe2の持つデータ
        kabe2UeNoHidari = -300;
        kabe2UeNoSoko = 100;
        kabe2ShitaNoHidari = -300;
        kabe2ShitaNoTakasa = kabe2UeNoSoko + kabeNoSukima;
        kabe2GaNai = false;

        //得点のリセット
        tokuten = 0;

        //難易度関係
        kabeNoSukima = 500;
        zigunTobuTikara = -41;
        kabeNoAtusa = 180;
        kabeNoSokudo = 8;

        //背景
        haike1X = 0;
        haike2X = haikeHaba;
        haike3X = haikeHaba * 2;
        haike1Y = 0;
        haike2Y = 0;
        haike3Y = 0;
        haike1GaNai = true;
        haike2GaNai = false;
        haike3GaNai = false;

        loadScore();
    }

    public void loadScore(){

        SharedPreferences prefer =
                getSharedPreferences( "hoppingBallScore", Context.MODE_PRIVATE );

        if(easyMode) {
            firstScore = prefer.getInt("easyModeFirstScore", 0);
            secondScore = prefer.getInt("easyModeSecondScore", 0);
            thirdScore = prefer.getInt("easyModeThirdScore", 0);
        }else if(normalMode){
            firstScore = prefer.getInt("normalModeFirstScore", 0);
            secondScore = prefer.getInt("normalModeSecondScore", 0);
            thirdScore = prefer.getInt("normalModeThirdScore", 0);
        }else if(hardMode){
            firstScore = prefer.getInt("hardModeFirstScore", 0);
            secondScore = prefer.getInt("hardModeSecondScore", 0);
            thirdScore = prefer.getInt("hardModeThirdScore", 0);
        }


    }

    public void comparisonScore(){

        if(tokuten > firstScore){
            thirdScore = secondScore;
            secondScore = firstScore;
            firstScore = tokuten;
        }else if (tokuten > secondScore){
            thirdScore = secondScore;
            secondScore = tokuten;
        }else if (tokuten > thirdScore){
            thirdScore = tokuten;
        }
    }

    public void saveScore(){

        SharedPreferences prefer =
                getSharedPreferences( "hoppingBallScore", Context.MODE_PRIVATE );

        SharedPreferences.Editor editor = prefer.edit();

        if(easyMode) {
            editor.putInt("easyModeFirstScore", firstScore);
            editor.putInt("easyModeSecondScore", secondScore);
            editor.putInt("easyModeThirdScore", thirdScore);
            editor.commit();
        }else if(normalMode){
            editor.putInt("normalModeFirstScore", firstScore);
            editor.putInt("normalModeSecondScore", secondScore);
            editor.putInt("normalModeThirdScore", thirdScore);
            editor.commit();
        }else if(hardMode){
            editor.putInt("hardModeFirstScore", firstScore);
            editor.putInt("hardModeSecondScore", secondScore);
            editor.putInt("hardModeThirdScore", thirdScore);
            editor.commit();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
