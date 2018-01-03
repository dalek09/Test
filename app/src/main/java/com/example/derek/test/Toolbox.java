package com.example.derek.test;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Derek on 03/01/2018.
 */

public class Toolbox {
    Toolbox(Activity activity){
        curActivity = activity;
    }


Activity curActivity;
        // return true if the specified directory exists
public boolean dir_exists(String dirPath) {
    boolean ret = false;
    File dir = new File(dirPath);
    if (dir.exists() & dir.isDirectory()) {
        ret = true;
    }
    return ret;
}

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public boolean hasExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(curActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            return true;
        else return false;
    }

    void showError(final String err) {
        curActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(curActivity, err, Toast.LENGTH_LONG).show();
            }
        });
    }

}