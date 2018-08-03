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

    public static void turnOnFlash() {
        Camera.Parameters myCameraParameters = mCamera.getParameters();
        String stringFlashMode;
        stringFlashMode = myCameraParameters.getFlashMode();
        if (stringFlashMode.equals("torch"))
            myCameraParameters.setFlashMode("on");
        else
            myCameraParameters.setFlashMode("torch");
        mCamera.setParameters(myCameraParameters);
        if (stringFlashMode.equals("on")) {
            myCameraParameters.setFlashMode("off");
        }
    }
}
