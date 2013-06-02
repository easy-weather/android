package com.keepiteasy.easyweather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

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
			((LoadingActivity) caller).onError("We are having some trouble reaching our servers.");
			Log.d("Error reading from server", e.getMessage());
			return null;
		} catch (JSONException e) { 
			((LoadingActivity) caller).onError("We are having some trouble reaching our servers.");
			Log.d("Error parsing JSON", e.getMessage()); return null; 
		}
		 
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		if (result != null) {
			Forecast.setConditions(new ConditionsObject(result));
			caller.finish();
		}
	}

}