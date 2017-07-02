package com.theomota.hdrcam.Camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.theomota.OnPictureSaved;
import com.theomota.hdrcam.R;
import com.theomota.hdrcam.hdr.HdrActivity;

import java.io.File;

/**
 * Created by theolm on 01/07/17.
 */

public class MainActivity extends Activity implements OnPictureSaved {
    private static final String TAG = "CamTestActivity";
    Preview preview;
    Button buttonClick;
    Camera camera;
    Activity act;
    Context ctx;

    int pictureTaken = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        act = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);

        preview = new Preview(this, (SurfaceView)findViewById(R.id.surfaceView));
        preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        ((FrameLayout) findViewById(R.id.layout)).addView(preview);
        preview.setKeepScreenOn(true);

        preview.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                takePicture(-10);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        if(numCams > 0){
            try{
                camera = Camera.open(0);
                preview.setCamera(camera);
            } catch (RuntimeException ex){
                //Toast.makeText(ctx, getString(R.string.camera_not_found), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        if(camera != null) {
            camera.stopPreview();
            preview.setCamera(null);
            camera.release();
            camera = null;
        }
        super.onPause();
    }

    private void resetCam() {
        camera.startPreview();
        preview.setCamera(camera);
        //lockWhiteBallance();
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }

    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            //			 Log.d(TAG, "onShutter'd");
        }
    };

    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            //			 Log.d(TAG, "onPictureTaken - raw");
        }
    };

    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            new SaveImageTask(MainActivity.this, pictureTaken).execute(data);
            resetCam();
            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };


    @Override
    public void onPictureSaved(String path) {
        pictureTaken++;

        if(pictureTaken == 1)
            takePicture(0);
        else if(pictureTaken == 2)
            takePicture(10);
        else {
            pictureTaken = 0;
            Intent i = new Intent(this, HdrActivity.class);
            startActivity(i);
        }

        Log.d(TAG, "onPictureSaved: ENTROU NO CALLBACK");
    }

//    private void takeHDR(){
//        Camera.Parameters cameraParameters = camera.getParameters();
//        cameraParameters.setSceneMode(Camera.Parameters.SCENE_MODE_HDR);
//        cameraParameters.set("ae-bracket-hdr", "AE-Bracket"); //"ae-bracket-hdr-values" -> "Off,AE-Bracket"
//        camera.setParameters(cameraParameters);
//        camera.takePicture(shutterCallback, rawCallback, jpegCallback);
//    }

    private void takePicture(final int exposureCompensation){
        //"min-exposure-time" -> "0.016733"
        //"max-exposure-time" -> "657.770600"
        //"cur-exposure-time" -> "66.666961"

        Log.d(TAG, "takePicture: " );
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Camera.Parameters params = camera.getParameters();
                params.setExposureCompensation(exposureCompensation);
                params.set("iso", "ISO400");
                //params.setAutoExposureLock(false); //"manual-exposure-modes" -> "off,exp-time-priority,iso-priority,user-setting"
                camera.setParameters(params);
                resetCam();
//                params.setAutoExposureLock(true);
//                camera.setParameters(params);

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                            }
                        });
                    }
                }, 1000);
            }
        });


    }

    private void lockWhiteBallance(){
        Camera.Parameters params = camera.getParameters();
        params.setAutoWhiteBalanceLock(true);
        camera.setParameters(params);
    }

    private void lockAutoExposure(){
        Camera.Parameters params = camera.getParameters();
        params.setAutoExposureLock(true);
        camera.setParameters(params);
    }

    private void setCameraIso(){
        Camera.Parameters params = camera.getParameters();
        params.set("iso", "ISO400");
        camera.setParameters(params);
    }
}
