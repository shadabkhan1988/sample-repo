package com.rahulhooda.sampleweatherapp.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rahulhooda.sampleweatherapp.Adapters.CustomAdapter;
import com.rahulhooda.sampleweatherapp.Model.NewsDetail;
import com.rahulhooda.sampleweatherapp.R;
import com.rahul.weatheradsdk.WeatherSdk;
import com.rahul.weatheradsdk.Interfaces.OnWeatherUpdateReceiver;
import com.rahul.weatheradsdk.PermissionException;

import java.util.Random;

/**
 * This class loads a list of news and shows the WeatherAd in between the list.
 *
 * */
public class MainActivity extends AppCompatActivity implements OnWeatherUpdateReceiver{

    private RecyclerView mRecyclerView;
    private NewsDetail mNewsDetail;
    private int mListSize = 10;    //Number of items in RecyclerView
    //Constant used while requesting the location permission
    private final int PERMISSION_ACCESS_FINE_LOCATION = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set this code to intialise the weather sdk
        try {
            WeatherSdk.getInstance().initialise(this);
        } catch (PermissionException e) {
            //App doesn't have permission to access location
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
            e.printStackTrace();
        }

        //Create a news deail bundle with some static data
        mNewsDetail = new NewsDetail();
        mNewsDetail.setNewsTitle("One of India's largest IT parks now has electricity that's digital");
        mNewsDetail.setNewDescription("A tech park near Thiruvananthapuram has take a big step in its attempt " +
                "to ensure unfettered power supply to the companies operating out of its campus.");
        mNewsDetail.setNewsDateAndTime("Feb 17, 2017, 11.07 PM IST");
        mNewsDetail.setNewsReporter("Prasad Sanyal");
        mNewsDetail.setNewsImage("http://timesofindia.indiatimes.com/thumb/msid-57203490,width-400,resizemode-4/57203490.jpg");

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //lets set initial adapter to recycler view
        mRecyclerView.setAdapter(new CustomAdapter(mNewsDetail,mListSize,0,null));
    }

    /**
     * This callback method is invoked when weather view has been created.
     * @param view weather view
     * */
    @Override
    public void onWeatherUpdateReceived(View view) {

        //Now the WeatherAd is ready to be displayed. Lets select some random position in list to show the Ad
        //Lets pass a random position for displaying weather ad
        Random random = new Random();
        int weatherAdPosition = random.nextInt(mListSize - 0 ) + 1;

        //Let's set updated adapter with weather view to recycler view
        mRecyclerView.setAdapter(new CustomAdapter(mNewsDetail,mListSize,weatherAdPosition,view));

    }

   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    try {
                        WeatherSdk.getInstance().initialise(MainActivity.this);
                    } catch (PermissionException e) {
                        e.printStackTrace();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    return;
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
