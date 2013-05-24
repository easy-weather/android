package com.keepiteasy.easyweather;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
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
		
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(link);
		try {
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			String responseBody = EntityUtils.toString(entity);
			obj = new JSONObject(responseBody.toString());
			return obj;
		} catch (IOException e) {
			Log.d("Error reading from server", e.getMessage());
			return null;
		} catch (JSONException e) {
			Log.d("Error parsing JSON", e.getMessage());
			return null;
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