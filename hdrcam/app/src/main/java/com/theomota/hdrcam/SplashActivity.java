package com.theomota.hdrcam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.theomota.hdrcam.hdr.HdrActivity;

import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

/**
 * Created by theolm on 01/07/17.
 */

public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, new LoaderCallbackInterface() {
            @Override
            public void onManagerConnected(int i) {
                switch (i) {
                    case LoaderCallbackInterface.SUCCESS: {
                        Intent intent = new Intent(SplashActivity.this, HdrActivity.class);
                        startActivity(intent);
                        finish();
                        Log.i("OpenCV", "OpenCV loaded successfully");
                    } break;
                }
            }

            @Override
            public void onPackageInstall(int i, InstallCallbackInterface installCallbackInterface) {
                Log.d(TAG, "onPackageInstall: ");
            }
        });
    }
}
