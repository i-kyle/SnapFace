package com.example.kylerfcristin.snapface.fragment;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.kylerfcristin.snapface.R;
import com.example.kylerfcristin.snapface.Singleton.CameraModule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.hardware.Camera.getNumberOfCameras;
import static android.hardware.Camera.open;
import static android.support.constraint.Constraints.TAG;

// Empty Fragment
// Transparent view that "sits" on top of camera
public class EmptyFragment extends Fragment {

    public static EmptyFragment create() {
        return new EmptyFragment();
    }

    static class CameraPreview extends ViewGroup implements SurfaceHolder.Callback {
        private final String TAG = "Preview";

        SurfaceView mSurfaceView;
        SurfaceHolder mHolder;
        Size mPreviewSize;
        List<Size> mSupportedPreviewSizes;
        Camera mCamera = CameraModule.getInstance();
        Camera.Size use_size;

        CameraPreview(Context context, SurfaceView sv) {
            super(context);

            mSurfaceView = sv;

            mHolder = mSurfaceView.getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            mHolder.setKeepScreenOn(true);
        }

        public void setCamera(Camera camera) {
            mCamera = camera;
            if (mCamera != null) {
                mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
                requestLayout();

                Camera.Parameters params = mCamera.getParameters();

                List<String> focusModes = params.getSupportedFocusModes();
                if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    mCamera.setParameters(params);
                }
            }
        }

//        @Override
//        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//            final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
//            final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
//            setMeasuredDimension(width, height);
//
//            if (mSupportedPreviewSizes != null) {
//                mPreviewSize = getOptimalPreviewSizes(mSupportedPreviewSizes, width, height);
//            }
//        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            if (changed && getChildCount() > 0) {
                final View child = getChildAt(0);

                final int width = r - l;
                final int height = b - t;

                int previewWidth = width;
                int previewHeight = height;
                if (mPreviewSize != null) {
                    previewWidth = mPreviewSize.width;
                    previewHeight = mPreviewSize.height;
                }

                if (width * previewHeight > height * previewWidth) {
                    final int scaledChildWidth = previewWidth * height / previewHeight;
                    child.layout((width - scaledChildWidth) / 2, 0,
                            (width + scaledChildWidth) / 2, height);
                } else {
                    final int scaledChildHeight = previewHeight * width / previewWidth;
                    child.layout(0, (height - scaledChildHeight) / 2,
                            width, (height + scaledChildHeight) / 2);
                }
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {

            try {
                if (mCamera != null) {
                    mCamera.setPreviewDisplay(holder);
                }
            } catch (IOException exception) {
                Log.e(TAG, "IOException is caused by setPreviewDisplay()", exception);
            }
        }

        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            if (mCamera != null) {
                mCamera.stopPreview();
            }
        }
//
        private Size getOptimalPreviewSizes(List<Size> sizes, int w, int h) {
            final double ASPECT_TOLERANCE = 0.1;
            double targetRatio = (double) w / h;
            if (sizes == null) ;

            Size optimalSize = null;
            double minDiff = Double.MAX_VALUE;

            int targetHeight = h;

            for (Size size : sizes) {
                double ratio = (double) size.width / size.height;
                if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }

            if (optimalSize == null) {
                minDiff = Double.MAX_VALUE;
                for (Size size : sizes) {
                    if (Math.abs(size.height - targetHeight) < minDiff) {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - targetHeight);
                    }
                }
            }
            return optimalSize;
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            if (mCamera != null) {
                Camera.Parameters parameters = mCamera.getParameters();
                List<Camera.Size> supportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
                Camera.Size optimalPreviewSize = getOptimalPreviewSizes(supportedPreviewSizes, w, h);
//                parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
//                requestLayout();
                if (optimalPreviewSize != null) {
                    parameters.setPreviewSize(optimalPreviewSize.width, optimalPreviewSize.height);
                    mCamera.setParameters(parameters);
                    mCamera.startPreview();
                }
                requestLayout();
            }
        }
    }


    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    CameraPreview preview;
    Button buttonClick;
    Camera camera;
    Activity act;
    Context ctx;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ctx = getContext();
        act = getActivity();
        View rootView = inflater.inflate(R.layout.camera_layout, container, false);
        preview = new CameraPreview(ctx, (SurfaceView) rootView.findViewById(R.id.surfaceView));
        preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        ((FrameLayout) rootView.findViewById(R.id.layout)).addView(preview);
        preview.setKeepScreenOn(true);

        preview.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
            }
        });

        Toast.makeText(ctx, "Welcome", Toast.LENGTH_LONG).show();
        Log.d("EMPTY"," ttttt==========");
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        int numCams = getNumberOfCameras();
        if (numCams > 0) {
            try {
                camera = open(0);
                camera.startPreview();
                preview.setCamera(camera);
                setCameraDisplayOrientation(act, 0, camera);
            } catch (RuntimeException ex) {
                Log.d("EMPTY", "=== " + ex.getLocalizedMessage());
                Toast.makeText(ctx, "Exception Toast " + ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onPause() {
        if (camera != null) {
            camera.stopPreview();
            preview.setCamera(null);
            camera.release();
            camera = null;
        }

        super.onPause();
    }

    public void resetCam() {
        camera.startPreview();
        preview.setCamera(camera);
    }

    private void refreshGallery(File file) {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        mediaScanIntent.setData(Uri.fromFile(file));
//        sendBroadcast(mediaScanIntent);
    }

    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {

        }
    };

    PictureCallback rawCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {

        }
    };

    PictureCallback jpegCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            new SaveImageTask().execute(data);
            resetCam();
            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground( byte[]...data){
            FileOutputStream outStream = null;

            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/camtest");
                dir.mkdirs();

                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());

                refreshGallery(outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return null;
        }
    }

}


