package com.example.user.hoppingball;

import android.view.Display;

/**
 * Created by user on 2017/10/08.
 */

public class DisplayRatio {


    public float referenceWidth = 780;
    public float referenceHeight = 1280;

    public float ratio = 1;
    public float widthRatio;
    public float heightRatio;

    public float displayRatioCast(int width,int height){

        widthRatio = width / referenceWidth;
        heightRatio = height / referenceHeight;
        if (widthRatio < heightRatio){
            ratio = widthRatio;
        }else {
            ratio = heightRatio;
        }

        return ratio;
    }

}