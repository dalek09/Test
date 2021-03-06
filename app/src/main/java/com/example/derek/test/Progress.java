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
    Dialog progressBox = null;
    ProgressBar progressBar;
    int curSize = 0;
    int maxSize = 0;
    float per = 0;
    TextView curValText = null;
    Activity curActivity;
    String sTitle = "initial title";

    Progress(Activity activity, String title){
        this(activity);
        sTitle = title;
//        curValText = (TextView) progressBox.findViewById(R.id.cur_pg_tv);    //we don't have a progressBox yet!
    }

    Progress(Activity activity){
        if (curActivity == null)
            curActivity = activity;

        if (progressBox == null)
            progressBox = new Dialog(activity);
        //curValText = (TextView) progressBox.findViewById(R.id.progress_text_2);    //we don't have a progressBox yet!
    }

    void setTitle(String title){
        sTitle = title;
        if(progressBox != null)
            progressBox.setTitle(title);
    };

    void setMaxSize(int size){
        maxSize = size;
        progressBar.setMax(maxSize);
    };

    void addCurSize(int size){
        curSize += size;
        setCurSize(curSize);
    }

    void setCurSize(int size){
        curSize = size;

//        if(curValText == null)
//            curValText = (TextView) progressBox.findViewById(R.id.progress_text_2);

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
        //progressBox = new Dialog(curActivity);
        progressBox.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressBox.setContentView(R.layout.progress_dialog);
        progressBox.setTitle(sTitle);

        TextView text = (TextView) progressBox.findViewById(R.id.progress_text_1);
        text.setText("Downloading file from ... " + file_path);

        progressBox.show();
        curValText = (TextView) progressBox.findViewById(R.id.progress_text_2);
        progressBar = (ProgressBar) progressBox.findViewById(R.id.progress_bar);
        progressBar.setProgress(0);
        progressBar.setProgressDrawable(curActivity.getResources().getDrawable(R.drawable.green_progress));
    }
}
