package com.example.kylerfcristin.snapface.Singleton;

import android.app.Activity;
import android.hardware.Camera;

public class CameraModule {
    private static Camera mCamera;
    public static Camera getInstance(){
        if (mCamera == null){
            mCamera = Camera.open(0);
        } return mCamera;
    }
}
