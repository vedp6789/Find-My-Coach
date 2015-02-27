package com.findmycoach.app.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ImageLoadTask extends AsyncTask<Void, Void, OutputStream> {

    private String url;
    private static final String TAG="FMC:";
    private Context context;
    private String fileName;
    private String storageFolder;
    private ArrayList<String> fileNames;

    public ImageLoadTask(String url, Context context, String fileName, String storageFolder, ArrayList<String> fileNames) {
        this.url = url;
        this.context = context;
        this.fileName = fileName;
        this.storageFolder = storageFolder;
        this.fileNames = fileNames;
    }

    @Override
    protected OutputStream doInBackground(Void... params) {
        try {
            Log.e(TAG, "Image URL:" + url);
            Log.e(TAG, "Started downloading... : " + fileName);
            Log.e(TAG, "Store to : " + storageFolder);
            URL urlConnection = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            OutputStream outputStream = new FileOutputStream(new File(storageFolder + "/" + fileName));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return outputStream;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(OutputStream result) {
        super.onPostExecute(result);
        try{
            fileNames.add(fileName);
        }catch (Exception e){
            e.printStackTrace();
        }
        Toast.makeText(context,"Done",Toast.LENGTH_LONG).show();
    }

}