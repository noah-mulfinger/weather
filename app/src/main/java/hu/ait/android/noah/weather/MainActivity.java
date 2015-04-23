package hu.ait.android.noah.weather;


import android.content.SharedPreferences;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import de.greenrobot.event.EventBus;
import hu.ait.android.noah.weather.data.WeatherData;


public class MainActivity extends ActionBarActivity {

    private final String URL_BASE =
            "http://api.openweathermap.org/data/2.5/";
    private final String URL_END = "&units=metric";

    private final String ICON_URL_BASE = "http://openweathermap.org/img/w/";
    private final String ICON_URL_END = ".png";

    private final String KEY_DATA = "KEY_DATA";
    private final String PREF_SETTINGS = "PREF_SETTINGS";


    private EditText etCityName;
    private TextView tvCurrentCity;
    private ImageView ivIcon;
    private TextView tvSunrise;
    private TextView tvSunset;
    private TextView tvCurrentTemp;
    private TextView tvTempMin;
    private TextView tvTempMax;
    private TextView tvHumidity;
    private TextView tvDesc;

    private String currentCity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCityName = (EditText) findViewById(R.id.etCityName);

        tvCurrentCity = (TextView) findViewById(R.id.tvCurrentCity);
        ivIcon = (ImageView) findViewById(R.id.ivIcon);
        tvSunrise = (TextView) findViewById(R.id.tvSunrise);
        tvSunset = (TextView) findViewById(R.id.tvSunset);
        tvCurrentTemp = (TextView) findViewById(R.id.tvCurrentTemp);
        tvTempMin = (TextView) findViewById(R.id.tvTempMin);
        tvTempMax = (TextView) findViewById(R.id.tvTempMax);
        tvHumidity = (TextView) findViewById(R.id.tvHumidity);
        tvDesc = (TextView) findViewById(R.id.tvDesc);


        Button btnGetData = (Button) findViewById(R.id.btnGetData);
        btnGetData.setOnClickListener(v -> {
            currentCity = etCityName.getText().toString();
            runAsyncTask();
        });

    }

    private void runAsyncTask() {

        String query = URL_BASE+
                "weather?q="+currentCity+URL_END;
        new HTTPGetTask(getApplicationContext()).execute(query);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);

        SharedPreferences sp = getSharedPreferences(PREF_SETTINGS, MODE_PRIVATE);
        String city = sp.getString(KEY_DATA, "");

        if (!"".equals(city)) {
            currentCity = city;
           runAsyncTask();
        }
//        LocalBroadcastManager.getInstance(this).registerReceiver(
//                weatherReceiver, new IntentFilter(HTTPGetTask.FILTER_RESULT));
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);

        SharedPreferences sp = getSharedPreferences(PREF_SETTINGS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_DATA, tvCurrentCity.getText().toString());
        editor.apply();
       // LocalBroadcastManager.getInstance(this).unregisterReceiver(weatherReceiver);
    }

    public void onEventMainThread(WeatherData data) {

        try {

            tvCurrentCity.setText(currentCity);

            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            format.setTimeZone(TimeZone.getDefault());
            Log.d("tag_",TimeZone.getDefault().getDisplayName());

            Date sunrise = new Date((long)data.getSys().getSunrise()*1000);
            Date sunset = new Date((long)data.getSys().getSunset()*1000);

            Glide.with(MainActivity.this).load(ICON_URL_BASE
                    +data.getWeather().get(0).getIcon()+ICON_URL_END).into(ivIcon);
            tvSunrise.setText(format.format(sunrise));
            tvSunset.setText(format.format(sunset));
            tvCurrentTemp.setText(data.getMain().getTemp()+" C");
            tvTempMin.setText(data.getMain().getTempMin()+" C");
            tvTempMax.setText(data.getMain().getTempMax()+" C");
            tvHumidity.setText(data.getMain().getHumidity()+"%");
            tvDesc.setText(data.getWeather().get(0).getDescription());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error getting data", Toast.LENGTH_LONG).show();
        }



    }

//    private BroadcastReceiver weatherReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String rawResult = intent.getStringExtra(HTTPGetTask.KEY_RESULT);
//            Log.d("tag_", rawResult);
//
////            try {
////                rawJson.getString("message");
////                Toast.makeText(MainActivity.this, "City not found", Toast.LENGTH_LONG).show();
////                return;
////            } catch (JSONException e) {
////                Toast.makeText(MainActivity.this, "City found", Toast.LENGTH_LONG).show();
////            }
////
//
//            try {
//                JSONObject rawJson = new JSONObject(rawResult);
//                if (rawJson.has("message")) {
//                   // Toast.makeText(MainActivity.this, "City not found", Toast.LENGTH_LONG).show();
//                    etCityName.setError("No city found");
//                    return;
//                }
//                //Toast.makeText(MainActivity.this, "City found", Toast.LENGTH_LONG).show();
//                String iconCode = ((JSONObject)rawJson.getJSONArray("weather").get(0)).
//                        getString("icon");
//                Date sunrise = new Date(rawJson.getJSONObject("sys").getInt("sunrise"));
//                Date sunset = new Date(rawJson.getJSONObject("sys").getInt("sunset"));
//                int currentTemp = rawJson.getJSONObject("main").getInt("temp");
//                int tempMin = rawJson.getJSONObject("main").getInt("temp_min");
//                int tempMax = rawJson.getJSONObject("main").getInt("temp_max");
//                int humidity = rawJson.getJSONObject("main").getInt("humidity");
//                String desc = ((JSONObject)rawJson.getJSONArray("weather").get(0)).
//                        getString("description");
//
//                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.US);
//
//                tvCurrentCity.setText(etCityName.getText().toString());
//                Glide.with(MainActivity.this).load(ICON_URL_BASE
//                        +iconCode+ICON_URL_END).into(ivIcon);
//                tvSunrise.setText(format.format(sunrise));
//                tvSunset.setText(format.format(sunset));
//                tvCurrentTemp.setText(String.valueOf(currentTemp)+" C");
//                tvTempMin.setText(String.valueOf(tempMin)+" C");
//                tvTempMax.setText(String.valueOf(tempMax)+" C");
//                tvHumidity.setText(String.valueOf(humidity)+"%");
//                tvDesc.setText(desc);
//
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    };

}
