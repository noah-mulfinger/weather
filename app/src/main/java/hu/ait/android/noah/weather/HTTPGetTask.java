package hu.ait.android.noah.weather;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.greenrobot.event.EventBus;
import hu.ait.android.noah.weather.data.WeatherData;

/**
 * Created by noah on 4/16/15.
 */

//first param defines type of input to send
//parameter which can give progress about operation
//third string because we will be getting a string (json)
public class HTTPGetTask extends AsyncTask<String, Void, String> {

    private Context context;

    public HTTPGetTask(Context context) {
        this.context = context;
    }
    @Override
    protected String doInBackground(String... params) {

        String result = "";
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {

            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();

                int ch;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((ch = inputStream.read()) != -1) {
                    bos.write(ch);
                }

                result = new String(bos.toByteArray());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                connection.disconnect();
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
//        Intent intentResult = new Intent(FILTER_RESULT);
//        intentResult.putExtra(KEY_RESULT, result);
//        LocalBroadcastManager.getInstance(context).sendBroadcast(intentResult);
        Log.d("tag__", result);
        Gson gson = new Gson();
        WeatherData data = gson.fromJson(result, WeatherData.class);

        EventBus.getDefault().post(data);

    }
}
