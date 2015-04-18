package hu.ait.android.noah.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {

    private final String URL_BASE =
            "http://api.openweathermap.org/data/2.5/";
    private final String URL_END = "&units=metric";

    private final String ICON_URL_BASE = "http://openweathermap.org/img/w/";
    private final String ICON_URL_END = ".png";

    private ImageView ivIcon;
    private TextView tvSunrise;
    private TextView tvSunset;
    private TextView tvTempMin;
    private TextView tvTempMax;
    private TextView tvHumidity;
    private TextView tvDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText etCityName = (EditText) findViewById(R.id.etCityName);

        ivIcon = (ImageView) findViewById(R.id.ivIcon);
        tvSunrise = (TextView) findViewById(R.id.tvSunrise);
        tvSunset = (TextView) findViewById(R.id.tvSunset);
        tvTempMin = (TextView) findViewById(R.id.tvTempMin);
        tvTempMax = (TextView) findViewById(R.id.tvTempMax);
        tvHumidity = (TextView) findViewById(R.id.tvHumidity);
        tvDesc = (TextView) findViewById(R.id.tvDesc);


        Button btnGetData = (Button) findViewById(R.id.btnGetData);
        btnGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = URL_BASE+
                        "weather?q="+etCityName.getText().toString()+URL_END;

                new HTTPGetTask(getApplicationContext()).execute(query);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                weatherReceiver, new IntentFilter(HTTPGetTask.FILTER_RESULT));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(weatherReceiver);
    }

    private BroadcastReceiver weatherReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String rawResult = intent.getStringExtra(HTTPGetTask.KEY_RESULT);


            try {
                JSONObject rawJson = new JSONObject(rawResult);
                String iconCode = ((JSONObject)rawJson.getJSONArray("weather").get(0)).
                        getString("icon");
                int sunrise = rawJson.getJSONObject("sys").getInt("sunrise");
                int sunset = rawJson.getJSONObject("sys").getInt("sunset");
                int tempMin = rawJson.getJSONObject("main").getInt("temp_min");
                int tempMax = rawJson.getJSONObject("main").getInt("temp_max");
                int humidity = rawJson.getJSONObject("main").getInt("humidity");
                String desc = ((JSONObject)rawJson.getJSONArray("weather").get(0)).
                        getString("description");

                Glide.with(MainActivity.this).load(ICON_URL_BASE
                        +iconCode+ICON_URL_END).into(ivIcon);
                tvSunrise.setText(String.valueOf(sunrise));
                tvSunset.setText(String.valueOf(sunset));
                tvTempMin.setText(String.valueOf(tempMin));
                tvTempMax.setText(String.valueOf(tempMax));
                tvHumidity.setText(String.valueOf(humidity));
                tvDesc.setText(desc);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
