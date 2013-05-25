package com.keepiteasy.easyweather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class ConditionsParser extends AsyncTask<String, Void, JSONObject> {
	private Activity caller;
	public ConditionsParser(Activity act) {
		caller = act;
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		JSONObject obj = null;
		String link = params[0];

		try {
			URL url = new URL(link);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			int responseCode = urlConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                
                obj = new JSONObject(sb.toString());
			}

			return obj;
		} catch (IOException e) {
			Log.d("Error reading from server", e.getMessage());
			return null;
		} catch (JSONException e) { 
			Log.d("Error parsing JSON", e.getMessage()); return null; 
		}
		 
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		if (result != null) {
			Log.d("results", result.toString());
			Forecast.setConditions(new ConditionsObject(result));
			caller.finish();
		}
	}

}