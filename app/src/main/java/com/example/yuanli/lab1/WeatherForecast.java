package com.example.yuanli.lab1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherForecast extends AppCompatActivity {

    String current_temp, min_temp,max_temp;
    Bitmap cWeatherBitMap;
     TextView first,second,third;
     ProgressBar pBar;
    ImageView WeImageview;
    String iconName;
    private int state;
    static String myURL;
    protected static final String ACTIVITY_NAME = "WeatherForecast";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);
        first = (TextView) findViewById(R.id.cu_temp);
        second = (TextView) findViewById(R.id.min_temp);
        third = (TextView) findViewById(R.id.ma_temp);
        pBar = (ProgressBar) findViewById(R.id.progressBar);
        WeImageview = (ImageView) findViewById(R.id.imageView);
        //  setSupportActionBar(toolbar);
        Log.i(ACTIVITY_NAME, "In onCreate()");
    myURL ="http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=d99666875e0e51521f0040a3d97d0f6a&mode=xml&units=metric";



        new ForecastQuery().execute(myURL);
    }


    public boolean fileExistance(String fname) {

        File file = getBaseContext().getFileStreamPath(fname);

        return file.exists();

    }
    public void  onResume(){
        super.onResume();
        Log.i(ACTIVITY_NAME, "In onResume()");

    }

    public void  onStart(){
        super.onStart();
        Log.i(ACTIVITY_NAME,"In onStart()");
    }
    public void onPause(){
        super.onPause();
        Log.i(ACTIVITY_NAME,"In onPause()");
    }
    public void onStop(){
        super.onStop();
        Log.i(ACTIVITY_NAME,"In onStop()");
    }
    public void onDestory(){
        super.onDestroy();
        Log.i(ACTIVITY_NAME,"In onDestory()");
    }

    private class ForecastQuery extends AsyncTask<String, Integer, String> {


        public String doInBackground(String... args) {

            state = 0;


            HttpURLConnection urlConnection = null;

            try {

                URL url = new URL(args[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream istream = urlConnection.getInputStream();


                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(false);

                XmlPullParser xpp = factory.newPullParser();


                xpp.setInput(istream, "UTF8");

                int type = XmlPullParser.START_DOCUMENT;


                while (type != XmlPullParser.END_DOCUMENT) {


                    switch (type) {

                        case XmlPullParser.START_DOCUMENT:

                            break;

                        case XmlPullParser.END_DOCUMENT:

                            break;

                        case XmlPullParser.START_TAG:

                            String name = xpp.getName();

                            if (name.equals("temperature")) {

                                current_temp = xpp.getAttributeValue(null, "value");

                                publishProgress(25);

                                min_temp = xpp.getAttributeValue(null, "min");

                                publishProgress(50);

                                max_temp = xpp.getAttributeValue(null, "max");

                                publishProgress(75);

                            }

                            if (name.equals("weather")) {

                                iconName = xpp.getAttributeValue(null, "icon");

                            }

                            break;

                        case XmlPullParser.END_TAG:

                            break;

                        case XmlPullParser.TEXT:

                            break;

                    }

                    type = xpp.next(); //advances to next xml event

                }

            } catch (Exception e) {

                Log.e("XML PARSING", e.getMessage());

            } finally {

                if (urlConnection != null) {

                    urlConnection.disconnect();

                }

            }


            return null;

        }


        public void onProgressUpdate(Integer... updateInfo) {

            pBar.setVisibility(View.VISIBLE);


            switch (state++) {

                case 0:

                 pBar.setProgress(updateInfo[0]);

                    break;

                case 1:

                    pBar.setProgress(updateInfo[0]);

                    break;

                case 2:

                   pBar.setProgress(updateInfo[0]);

                    break;

            }


            if (iconName != null) {

                String imageURL = "http://openweathermap.org/img/w/" + iconName + ".png";

                String fileName = iconName + ".png";

                boolean exist = fileExistance(fileName);

                if (exist) {

                    Log.i(ACTIVITY_NAME, fileName + " exists and  again!");

                    FileInputStream fis = null;

                    File file = getBaseContext().getFileStreamPath(fileName);

                    try {

                        fis = new FileInputStream(file);

                    } catch (FileNotFoundException e) {

                        e.printStackTrace();

                        cWeatherBitMap = null;

                    }

                    cWeatherBitMap = BitmapFactory.decodeStream(fis);

                } else {

                    Log.i(ACTIVITY_NAME, fileName + " does not exist and need to download!");

                    new DownloadBitmap().execute(imageURL);

                }


                if (cWeatherBitMap == null) {

                    new DownloadBitmap().execute(imageURL);

                }

            }

        }


        public void onPostExecute(String result) {

            first.setText("current temperature " +current_temp + "°C");

            second.setText("min temperature " + min_temp + "°C");

            third.setText("max temperature " + max_temp + "°C");
//            first.setText("current temperature  11");
//            second.setText(" min temperature 12");
//            third.setText("max temperature 13");
            if (cWeatherBitMap != null) {

                WeImageview.setImageBitmap(cWeatherBitMap);

            }

            pBar.setVisibility(View.INVISIBLE);

        }

    }


    private class DownloadBitmap extends AsyncTask<String, Integer, String> {


        public String doInBackground(String... args) {

            HttpURLConnection connection = null;

            try {

                URL url = new URL(args[0]);

                connection = (HttpURLConnection) url.openConnection();

                connection.setDoInput(true);

                connection.connect();

                int responseCode = connection.getResponseCode();

                if (responseCode == 200) {

                    cWeatherBitMap = BitmapFactory.decodeStream(connection.getInputStream());

                    try {

                        FileOutputStream fos = openFileOutput(iconName + ".png", Context.MODE_PRIVATE);

                        ByteArrayOutputStream outstream = new ByteArrayOutputStream();

                        cWeatherBitMap.compress(Bitmap.CompressFormat.PNG, 80, outstream);

                        byte[] byteArray = outstream.toByteArray();

                        fos.write(byteArray);

                        fos.close();

                        publishProgress(100);

                    } catch (Exception e) {

                        Log.i(ACTIVITY_NAME, "DownloadBitmap doInBackground with Exception " + e.getMessage());

                    }

                } else {

                    return null;

                }

                return null;

            } catch (Exception e) {

                Log.i(ACTIVITY_NAME, "DownloadBitmap doInBackground with Exception " + e.getMessage());

                return null;

            } finally {

                if (connection != null) {

                    connection.disconnect();

                }

            }

        }


        public void onProgressUpdate(Integer... updateInfo) {

            pBar.setProgress(updateInfo[0]);

        }


        public void onPostExecute(String result) {

            WeImageview.setImageBitmap(cWeatherBitMap);

        }

    }

}




