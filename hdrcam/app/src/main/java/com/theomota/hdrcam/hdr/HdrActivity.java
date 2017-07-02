package com.theomota.hdrcam.hdr;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.theomota.hdrcam.R;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.photo.Photo;

import java.io.File;
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

    File file1;
    File file2;
    File file3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hdr);

        imageView1 = (ImageView) findViewById(R.id.image1);
        imageView2 = (ImageView) findViewById(R.id.image2);
        imageView3 = (ImageView) findViewById(R.id.image3);
        button = (Button) findViewById(R.id.button);

        file1 = new File(getDir() + File.separator + "0.jpg");
        file2 = new File(getDir() + File.separator + "1.jpg");
        file3 = new File(getDir() + File.separator + "2.jpg");

        Picasso.with(this).load(file1).into(imageView1);
        Picasso.with(this).load(file2).into(imageView2);
        Picasso.with(this).load(file3).into(imageView3);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //createHDR();
                Toast.makeText(HdrActivity.this, "Init HDR", Toast.LENGTH_SHORT).show();
                new generateHDR().execute();

            }
        });

    }

    private File getDir() {
        File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "CameraAPIDemo");
    }


    class generateHDR extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            //float[] InTimes = {0.03333f, 0.33333f, 4f};
            List<Mat> images = new ArrayList<>();
//            Mat response = new Mat();
//            Mat times = new MatOfFloat(InTimes);
//            CalibrateDebevec debevec = Photo.createCalibrateDebevec();

            try {
                images.add(Imgcodecs.imread(file1.getAbsolutePath()));
                images.add(Imgcodecs.imread(file2.getAbsolutePath()));
                images.add(Imgcodecs.imread(file3.getAbsolutePath()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            Mat hdr = new Mat();
            Photo.createMergeMertens().process(images, hdr);



//            debevec.process(images, response, times);
//
//            Mat hdr = new Mat();
//            MergeDebevec mergeDebevec = Photo.createMergeDebevec();
//            mergeDebevec.process(images, hdr, times, response);
//
//            Mat ldr = new Mat();
//            TonemapDurand tonemapDurand = Photo.createTonemapDurand();
//            tonemapDurand.setGamma(2.2f);
//            tonemapDurand.process(hdr, ldr);

            String filename = getDir() + File.separator + "teste.jpg";
            Mat temp = new Mat();

            hdr.convertTo(temp, CvType.CV_8U, 255);

            Imgcodecs.imwrite(filename, temp);


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(HdrActivity.this, "HDR pronto", Toast.LENGTH_SHORT).show();
            HdrActivity.this.finish();
        }
    }

}
