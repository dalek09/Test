package com.example.derek.test;

import android.app.Dialog;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    Dialog progressBox;
    int downloadedSize = 0;
    int totalSize = 0;
    TextView cur_val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getFile(View v) throws IOException {
        String strURL = "http://bleepcomputing.com/demo/TOLOrder-20171215-0010000003.CSV";
    }

    public void readFile(View v) throws IOException {
        String myFolderName = getExternalStorageDirectory() + "/Derek/";
        String myFileName = "Bleep.CSV";

        // if the folder doesn't exist, create it
        if (!dir_exists(myFolderName)) {
            File dir = new File(myFolderName);
            if (!dir.mkdirs())
                throw new IOException("Cannot make directory: " + myFolderName);
        }

        String fullName = myFolderName + myFileName;
        File myFile = new File(fullName);
        try {
            myFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // return true if the specified directory exists
    private boolean dir_exists(String dirPath) {
        boolean ret = false;
        File dir = new File(dirPath);
        if (dir.exists() & dir.isDirectory()) {
            ret = true;
        }
        return ret;
    }

    void showProgress(String file_path) {
        progressBox = new Dialog(MainActivity.this);
        progressBox.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressBox.setContentView(R.layout.progress_dialog);
        progressBox.setTitle("Download Progress");

        TextView text = (TextView) progressBox.findViewById(R.id.tv1);
        text.setText("Downloading file from ... " + file_path);
        cur_val = (TextView) progressBox.findViewById(R.id.cur_pg_tv);
        cur_val.setText("Starting download...");
        progressBox.show();

        progressBar = (ProgressBar) progressBox.findViewById(R.id.progress_bar);
        progressBar.setProgress(0);
        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.green_progress));
    }

}