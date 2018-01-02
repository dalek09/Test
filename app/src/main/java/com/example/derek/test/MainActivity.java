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
import android.webkit.URLUtil;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    Dialog progressBox;
    int downloadedSize;
    int totalSize = 0;
    TextView curVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Get button - download a file from the internet
    public void getFile(View v) throws IOException {
        downloadedSize = 0;

        if (!isExternalStorageWritable()){
            showError("External Storage is not writable");
            return;
        }
        if (!hasExternalStoragePermission()){
            showError("No External Storage permission");
            return;
        }

        final String urlBleep = "http://bleepcomputing.com/devel/csv.csv";
        showProgress(urlBleep);

        //Runnable bad = new Runnable();

//        Runnable me = new Runnable() {    //lambda?
//            public void run() {
//                downloadFile(urlBleep);
//            }
//        };
//
//        new Thread(me).start();
        new Thread(new Runnable() {
            public void run() {
                downloadFile(urlBleep);
            }
        }).start();
    }

    // Read button - read a file from local storage
    public void readFile(View v) throws IOException {
        CSVParse csvp = new CSVParse();
        //Reader r = new Reader();
        //csvp.readLine(r);

        String myFolderName = getExternalStorageDirectory() + "/Derek/";
        String myFileName = "csv.csv";

        // if the folder doesn't exist, create it
        readAndParseFile(myFolderName, myFileName);
    }

    public void readAndParseFile(String myFolderName, String myFileName) throws IOException {
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

    public static List<Product> readData() throws Exception {
        List<Product> collection = new Vector<Product>();
        File fileTemplate = new File("Derek/Bleep.CSV");
        FileInputStream fis = new FileInputStream(fileTemplate);
        Reader fr = new InputStreamReader(fis, "UTF-8");

        List<String> values = CSVParse.readLine(fr);
        while (values!=null) {
            collection.add( Product.constructFromStrings(values) );
            values = CSVParse.readLine(fr);
        }
        fr.close();
        return collection;
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
        curVal = (TextView) progressBox.findViewById(R.id.cur_pg_tv);
        curVal.setText("Starting download...");
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

            //setup the URL to download
            URL url = new URL(strURL);
            File outFile = new File(strURL);
            String strName = outFile.getName();
            //FileOutputStream fileOutput = new FileOutputStream("Test.csv", Context.MODE_PRIVATE);
            FileOutputStream fileOutput = openFileOutput(strName, Context.MODE_PRIVATE);


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
                        curVal.setText("Downloaded " + downloadedSize + "KB / " + totalSize + "KB (" + (int)per + "%)" );
                    }
                });
            }
            //close the output stream when complete //
            fileOutput.close();
            runOnUiThread(new Runnable() {
                public void run() {
                    // progressBar.dismiss(); // if you want close it..
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
