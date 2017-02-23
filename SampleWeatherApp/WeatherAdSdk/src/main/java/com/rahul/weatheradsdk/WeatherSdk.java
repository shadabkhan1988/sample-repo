package com.rahul.weatheradsdk;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.rahul.weatheradsdk.Interfaces.OnWeatherUpdateReceiver;
import com.rahul.weatheradsdk.Model.Weather;
import com.rahul.weatheradsdk.Utils.JSONWeatherParser;
import com.rahul.weatheradsdk.Utils.WeatherHttpClient;

import org.json.JSONException;

/**
 * Created by rahul.hooda on 2/19/17.
 */

public class WeatherSdk implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private View view;
    private static final WeatherSdk instance = new WeatherSdk();
    private OnWeatherUpdateReceiver mOnWeatherUpdateReceiver;

    //Constant used while requesting the location permission
    private final int PERMISSION_ACCESS_FINE_LOCATION = 123;

    //private constructor to avoid client applications to use constructor
    private WeatherSdk() {
    }

    /**
     * This method returns the instance of WeatherSdk class
     *
     * @return WeatherSdk instance
     */
    public static WeatherSdk getInstance() {
        return instance;
    }

    /**
     * This method initialises the WeatherSdk and makes a call to fetch user's current location
     *
     * @param context
     */
    public void initialise(Context context) throws PermissionException {
        mContext = context;

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw new PermissionException("Access fine location not granted");
        } else {
            //Lets get user's location
            makeCallForCurrentLocation(mContext);

            mOnWeatherUpdateReceiver = (OnWeatherUpdateReceiver) mContext;
        }
    }

    /**
     * This method makes a call to fetch user's current location.
     *
     * @param ctx Application Contect
     */
    private void makeCallForCurrentLocation(Context ctx) {

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(ctx)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            //let's connect with GoogleApiClient
            mGoogleApiClient.connect();
        }
    }

    /**
     * This method will be invoked when GoogleApiClient is connected.
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        android.location.Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        String latitude = String.valueOf(lastLocation.getLatitude());
        String longitude = String.valueOf(lastLocation.getLongitude());

        Log.d("WeatherSdk", "Latitude : " + latitude + ", Longitude : " + longitude);

        //disconnect the google api client
        mGoogleApiClient.disconnect();

        try {
            JSONWeatherTask task = new JSONWeatherTask();
            task.execute(new String[]{latitude, longitude});
        } catch (Exception e) {
            Log.d("WeatherSdk", "Something went wrong, try again later");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        ConnectionResult cResult = connectionResult;
    }

    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {

            Weather weather = new Weather();
            String data = ((new WeatherHttpClient()).getWeatherData(params[0], params[1]));

            try {
                if (data != null) {
                    weather = JSONWeatherParser.getWeather(data);

                    // Let's retrieve the icon
                    weather.iconData = ((new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;

        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(com.rahul.weatheradsdk.R.layout.weather_sdk_view, null);

            TextView cityText = (TextView) view.findViewById(R.id.cityText);
            TextView condDescr = (TextView) view.findViewById(R.id.condDescr);
            TextView temp = (TextView) view.findViewById(R.id.temp);
            TextView hum = (TextView) view.findViewById(R.id.hum);
            TextView press = (TextView) view.findViewById(R.id.press);
            TextView windSpeed = (TextView) view.findViewById(R.id.windSpeed);
            TextView windDeg = (TextView) view.findViewById(R.id.windDeg);
            ImageView imgView = (ImageView) view.findViewById(R.id.condIcon);

            if (weather.iconData != null && weather.iconData.length > 0) {
                Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                imgView.setImageBitmap(img);
            }

            //If city is null, then return null view
            if (weather.location == null) {
                mOnWeatherUpdateReceiver.onWeatherUpdateReceived(null);
                return;
            }


            if (weather.location != null)
                cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());

            if (weather.currentCondition != null) {
                condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
                hum.setText("" + weather.currentCondition.getHumidity() + "%");
                press.setText("" + weather.currentCondition.getPressure() + " hPa");

            }
            if (weather.temperature != null)
                temp.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) + "°C");

            if (weather.wind != null) {
                windSpeed.setText("" + weather.wind.getSpeed() + " m/s");
                windDeg.setText("" + weather.wind.getDeg() + "°");
            }

            mOnWeatherUpdateReceiver.onWeatherUpdateReceived(view);
        }
    }
}
