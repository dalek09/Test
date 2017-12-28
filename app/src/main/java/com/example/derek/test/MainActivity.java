package com.example.derek.test;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

        if (!isExternalStorageWritable()){
            showError("External Storage is not writable");
            return;
        }
        if (!hasExternalStoragePermission()){
            showError("No External Storage permission");
            return;
        }

        final String urlBleep = "http://bleepcomputing.com/demo/TOLOrder-20171215-0010000003.CSV";
        showProgress(urlBleep);

        //Runnable bad = new Runnable();

        Runnable me = new Runnable() {
            public void run() {
                downloadFile(urlBleep);
            }
        };

        new Thread(me).start();
//        new Thread(new Runnable() {
//            public void run() {
//                downloadFile(urlBleep);
//            }
//        }).start();
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

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private boolean hasExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            return true;
        else return false;
    }

    void showError(final String err){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, err, Toast.LENGTH_LONG).show();
            }
        });
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

    void downloadFile(String strURL) {
        try {
            //create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            //set the path where we want to save the file
//            File SDCardRoot = Environment.getExternalStorageDirectory();
//            File derekDir = new File(SDCardRoot, "Derek/");
//            //create a new file, to save the downloaded file
//            File file = new File(derekDir,"downloaded_file.csv");
//            FileOutputStream fileOutput = new FileOutputStream(file);

            //FileOutputStream fileOutput = new FileOutputStream("Test.csv", Context.MODE_PRIVATE);
            FileOutputStream fileOutput = openFileOutput("Test.csv", Context.MODE_PRIVATE);
            //setup the URL to download
            URL url = new URL(strURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);

            //connect
            urlConnection.connect();

            //Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();

            //this is the total size of the file which we are downloading
            totalSize = urlConnection.getContentLength();

            runOnUiThread(new Runnable() {
                public void run() {
                    progressBar.setMax(totalSize);
                }
            });

            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                // update the progressbar //
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressBar.setProgress(downloadedSize);
                        float per = ((float)downloadedSize/totalSize) * 100;
                        cur_val.setText("Downloaded " + downloadedSize + "KB / " + totalSize + "KB (" + (int)per + "%)" );
                    }
                });
            }
            //close the output stream when complete //
            fileOutput.close();
            runOnUiThread(new Runnable() {
                public void run() {
                    // pb.dismiss(); // if you want close it..
                }
            });
        } catch (final MalformedURLException e) {
            showError("Error : MalformedURLException " + e);
            e.printStackTrace();
        } catch (final SecurityException e) {
            showError("Error : SecurityException " + e);
            e.printStackTrace();
        } catch (final IOException e) {
            showError("Error : IOException " + e);
            e.printStackTrace();
        }
        catch (final Exception e) {
            showError("Error : Please check your internet connection " + e);
        }
    }
}
