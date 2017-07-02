package com.theomota.hdrcam.Camera;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by theolm on 01/07/17.
 */

public class SaveImageTask extends AsyncTask<byte[], Void, Void> {
    private static final String TAG = "SaveImageTask";

    private Context context;
    private int number;

    public SaveImageTask(Context context, int number) {
        this.context = context;
        this.number = number;
    }

    @Override
    protected Void doInBackground(byte[]... data) {
        File pictureFileDir = getDir();

        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

            Log.d(TAG, "Can't create directory to save image.");
            //Toast.makeText(context, "Can't create directory to save image.", Toast.LENGTH_LONG).show();
            return null;

        }

        String photoFile = String.valueOf(number) + ".jpg";

        String filename = pictureFileDir.getPath() + File.separator + photoFile;

        File pictureFile = new File(filename);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data[0]);
            fos.close();
        } catch (Exception error) {
            error.printStackTrace();
            Log.d(TAG, "File" + filename + "not saved: " + error.getMessage());
            //Toast.makeText(context, "Image could not be saved.", Toast.LENGTH_LONG).show();
        }

        ((MainActivity) context).onPictureSaved(filename);

        return null;
    }

    private File getDir() {
        File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "CameraAPIDemo");
    }
}
