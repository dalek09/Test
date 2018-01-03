package com.example.derek.test;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Derek on 03/01/2018.
 */

public class Progress {
    Dialog progressBox;
    ProgressBar progressBar;
    int curSize;
    int maxSize = 0;
    float per = 0;
    TextView curValText = null;
    Activity curActivity;

    Progress(Activity activity){
        curActivity = activity;
//        curValText = (TextView) progressBox.findViewById(R.id.cur_pg_tv);    //we don't have a progressBox yet!
    }

    void setMaxSize(int size){maxSize = size;};

    void setCurSize(int size){
        curSize = size;

        if(curValText == null)
            curValText = (TextView) progressBox.findViewById(R.id.cur_pg_tv);

        if(size == 0) {
            curValText.setText("Starting download...");
        }
        else {
            String curSizeUnits =  curSize > 1000 ? "KB" : "B";
            String maxSizeUnits =  maxSize > 1000 ? "KB" : "B";
            per = ((float) curSize / maxSize) * 100;
            curValText.setText("Downloaded " + curSize + curSizeUnits + " / " + maxSize + maxSizeUnits + " (" + (int)per + "%)");
        }
        progressBar.setProgress(curSize);
    };

    void showProgress(String file_path) {
        progressBox = new Dialog(curActivity);
        progressBox.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressBox.setContentView(R.layout.progress_dialog);
        progressBox.setTitle("Download Progress");

        TextView text = (TextView) progressBox.findViewById(R.id.tv1);
        text.setText("Downloading file from ... " + file_path);
        progressBox.show();

        progressBar = (ProgressBar) progressBox.findViewById(R.id.progress_bar);
        progressBar.setProgress(0);
        progressBar.setProgressDrawable(curActivity.getResources().getDrawable(R.drawable.green_progress));
    }

}
