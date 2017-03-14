package com.awchoudhary.bookpocket;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by awaeschoudhary on 3/13/17.
 * Saves image to provided file
 */

public class SaveImageTask extends
        AsyncTask<File, Void, Void> {

    private Context context;
    private Bitmap image; // Bitmap to save

    public SaveImageTask(Context context, Bitmap image){
        this.context = context;
        this.image = image;
    }

    protected Void doInBackground(File... params) {
        File outFile = params[0];

        try {
            FileOutputStream fos = new FileOutputStream(outFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
        }
        return null;
    }
}
