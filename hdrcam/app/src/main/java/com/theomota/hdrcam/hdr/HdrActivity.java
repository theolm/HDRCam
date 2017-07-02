package com.theomota.hdrcam.hdr;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.theomota.hdrcam.R;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.photo.CalibrateDebevec;
import org.opencv.photo.MergeDebevec;
import org.opencv.photo.Photo;
import org.opencv.photo.TonemapDurand;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by theolm on 01/07/17.
 */

public class HdrActivity extends Activity {
    private static final String TAG = "HdrActivity";

    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hdr);


        imageView1 = (ImageView) findViewById(R.id.image1);
        imageView2 = (ImageView) findViewById(R.id.image2);
        imageView3 = (ImageView) findViewById(R.id.image3);
        button = (Button) findViewById(R.id.button);

        //Picasso.with(this).load(R.drawable.office_1).into(imageView1);
        //Picasso.with(this).load(R.drawable.office_2).into(imageView2);
        //Picasso.with(this).load(R.drawable.office_3).into(imageView3);
        float[] InTimes = {0.03333f, 0.33333f, 4f};
        List<Mat> images = new ArrayList<>();
        Mat response = new Mat();
        Mat times = new MatOfFloat(InTimes);
        CalibrateDebevec debevec = Photo.createCalibrateDebevec();

        try {
            images.add(Utils.loadResource(this, R.drawable.office_1, Imgcodecs.CV_LOAD_IMAGE_COLOR));
            images.add(Utils.loadResource(this, R.drawable.office_2, Imgcodecs.CV_LOAD_IMAGE_COLOR));
            images.add(Utils.loadResource(this, R.drawable.office_3, Imgcodecs.CV_LOAD_IMAGE_COLOR));
        } catch (IOException e) {
            e.printStackTrace();
        }

        debevec.process(images, response, times);

        Mat hdr = new Mat();
        MergeDebevec mergeDebevec = Photo.createMergeDebevec();
        mergeDebevec.process(images, hdr, times, response);

        Mat ldr = new Mat();
        TonemapDurand tonemapDurand = Photo.createTonemapDurand();
        tonemapDurand.process(hdr, ldr);

        String filename = getDir() + File.separator + "teste.jpg";
        Mat temp = new Mat();

        ldr.convertTo(temp, CvType.CV_8U, 255);

        Imgcodecs.imwrite(filename, temp);

//        Imgproc.cvtColor(ldr, dst, Imgproc.COLOR_RGB2GRAY, 1);
//        dst.convertTo(dst_f, CvType.CV_32F, 1/255, 0);
//        dst_f.convertTo(finalImg, CvType.CV_32F, 255, 0);
//        Imgproc.cvtColor(finalImg, roi, Imgproc.COLOR_GRAY2RGB, 0);
//        Imgproc.cvtColor(roi, roi, Imgproc.COLOR_RGB2RGBA, 0);
//        dst.convertTo(dst_f, CvType.CV_32F, 1.0/255, 0); // Convert Mat image to CV_32F and scale it to 1.0/255
//        dst_f.convertTo(final, CvType.CV_32F, 255, 0); //Convert it Back to Default type before
//        Imgproc.cvtColor(final, roi, Imgproc.COLOR_GRAY2RGB, 0);
//        Imgproc.cvtColor(roi, roi, Imgproc.COLOR_RGB2RGBA, 0);

//        Bitmap bmp = Bitmap.createBitmap(temp.cols(), temp.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(temp, bmp);

        Log.d(TAG, "onCreate: ");

    }

    private File getDir() {
        File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "CameraAPIDemo");
    }
}
