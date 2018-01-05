package com.example.derek.test;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
    Toolbox toolbox;
    //Progress progress;
    int totalSize;
    //int downloadedSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbox = new Toolbox(MainActivity.this);
    }

    // Get button - download a file from the internet
    public void getFile(View v) throws IOException {

        if (!toolbox.isExternalStorageWritable()){
            toolbox.showError("External Storage is not writable");
            return;
        }
        if (!toolbox.hasExternalStoragePermission()){
            toolbox.showError("No External Storage permission");
            return;
        }

        final Progress progress = new Progress(MainActivity.this);
        //final String urlBleep = "http://bleepcomputing.com/devel/csv.csv";
        final String urlBleep = "http://bleepcomputing.com/devel/bleepdemov7.zip";
        progress.showProgress(urlBleep);
        //progress.setTitle("Download progress");

        new Thread(new Runnable() {
            public void run() {
                downloadFile(progress, urlBleep);
            }
        }).start();
    }

    // Read button - read a file from local storage
    public void readFile(View v) throws IOException {
        CSVParse csvp = new CSVParse();
        //Reader r = new Reader();
        //csvp.readLine(r);

        //String myFolderName = getExternalStorageDirectory() + "/Derek/";
        final String myFileName = "csv.csv";

        // if the folder doesn't exist, create it
        //readAndParseFile(myFolderName, myFileName);
        new Thread(new Runnable() {
            public void run() {
                readAndParseFile(myFileName);
            }
        }).start();


    }

    //public void readAndParseFile(String myFileName) throws IOException {
    public void readAndParseFile(String myFileName) {

        //File myFile = new File(myFileName);
        try {
            List<Product> myProducts;
            myProducts = readData(myFileName);
        }
        catch (Exception e) {
            toolbox.showError(e.toString());
        }
    }

//    public void readAndParseFile(String myFolderName, String myFileName) throws IOException {
//        if (!toolbox.dir_exists(myFolderName)) {
//            File dir = new File(myFolderName);
//            if (!dir.mkdirs())
//                throw new IOException("Cannot make directory: " + myFolderName);
//        }
//
//        String fullName = myFolderName + myFileName;
//        File myFile = new File(fullName);
//        try {
//            myFile.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public  List<Product> readData(String sFileName) throws Exception {
        List<Product> collection = new Vector<>();
        //String myFolderName = getExternalStorageDirectory() + "/Derek/";
        String myFileName = "csv.csv";

        //File fileTemplate = new File(myFolderName + myFileName);
        File fileTemplate = new File(myFileName);

        FileInputStream fileInput = openFileInput(myFileName);

       // FileInputStream fis = new FileInputStream(fileTemplate);
        Reader fr = new InputStreamReader(fileInput, "UTF-8");

        List<String> values = CSVParse.readLine(fr);
        while (values!=null) {
            collection.add( Product.constructFromStrings(values) );
            values = CSVParse.readLine(fr);
        }
        fr.close();
        return collection;
    }

    void downloadFile(final Progress progress, String strURL) {
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
            progress.setTitle("Download progress");
            progress.setMaxSize(totalSize);
            progress.setCurSize(0);

            //downloadedSize = 0;

//            runOnUiThread(new Runnable() {
//                public void run() {
//                    progress.progressBar.setMax(totalSize);
//                }
//            });

//            runOnUiThread(new Runnable() {
//                public void run() {
//                    progress.setMaxSize(totalSize);
//                    progress.setCurSize(0);
//                }
//            });

            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
//                downloadedSize += bufferLength;
                final int curChunkSize = bufferLength;
                // update the progressbar //
                runOnUiThread(new Runnable() {
                    public void run() {
                    //progress.setCurSize(downloadedSize);
                    progress.addCurSize(curChunkSize);

                    //float per = ((float)downloadedSize / totalSize) * 100;
                    //curVal.setText("Downloaded " + downloadedSize + "KB / " + totalSize + "KB (" + (int)per + "%)" );
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
            toolbox.showError("Error : MalformedURLException " + e);
            e.printStackTrace();
        } catch (final SecurityException e) {
            toolbox.showError("Error : SecurityException " + e);
            e.printStackTrace();
        } catch (final IOException e) {
            toolbox.showError("Error : IOException " + e);
            e.printStackTrace();
        }
        catch (final Exception e) {
            toolbox.showError("Error : Please check your internet connection " + e);
        }
    }
}
