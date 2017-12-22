package com.example.derek.test;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.io.IOException;

import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void readFile(View v) throws IOException {
        String myFolderName =  getExternalStorageDirectory() + "/Derek/";
        String myFileName = "Bleep.CSV";

        // if the folder doesn't exist, create it
        if(!dir_exists(myFolderName)) {
            File dir = new File(myFolderName);
            if(!dir.mkdirs())
                throw new IOException("Cannot make directory: " + myFolderName);
        }

        String fullName = myFolderName + myFileName;
        File myFile= new File( fullName );
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
    if(dir.exists() & dir.isDirectory()){
        ret = true;
    }
    return ret;
    }
}
