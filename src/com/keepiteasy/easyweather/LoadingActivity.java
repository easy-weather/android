package com.keepiteasy.easyweather;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;
import android.net.NetworkInfo;

public class LoadingActivity extends Activity {

	private static final long MINTIME = 1800000;
	private LocationManager lm;
	private mylocationlistener ll;
	private Activity activity;
	private Location lastLocation = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);

		activity = this;

		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			final Intent intent = new Intent(LoadingActivity.this, ErrorActivity.class);
			startActivity(intent);
			finish();
		} else {
			ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			if (conMgr.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED || conMgr.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
				final Intent intent = new Intent(LoadingActivity.this, ErrorActivity.class);
				intent.putExtra("error", "You don't seem to be connected to the internet");
				startActivity(intent);
				finish();

			} else {
				ll = new mylocationlistener();

				Date date = new Date();
				long lastSafeTime = date.getTime() + MINTIME;

				List<String> matchingProviders = lm.getAllProviders();
				for (String provider : matchingProviders) {
					Location location = lm.getLastKnownLocation(provider);

					if (location != null) {
						long time = location.getTime();

						if (time < lastSafeTime) {
							lastLocation = location;
						}
					}
				}

				if (lastLocation == null) {
					String text = "Secret Ninjas are searching the globe to find your location...";
					Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
					toast.show();

					lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
				} else {
					loadWeather();
				}
			}
		}
	}

	protected void loadWeather() {
		String text = "We know where you are, now let's ask the weather Gods what its like...";
		makeToast(text);

		Forecast.location = lastLocation;

		String la = String.valueOf(lastLocation.getLatitude());
		String lo = String.valueOf(lastLocation.getLongitude());

		ForecastParser parser = new ForecastParser();
		parser.execute("http://54.245.106.49/easy-weather-api/index.php/weather/forecast/" + la + "/" + lo);

		ConditionsParser cParser = new ConditionsParser(activity);
		cParser.execute("http://54.245.106.49/easy-weather-api/index.php/weather/conditions/" + la + "/" + lo);
	}

	public void makeToast(String text) {
		Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
		toast.show();

	}

	public class mylocationlistener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				lastLocation = location;
				loadWeather();
			}
		}
		@Override
		public void onProviderDisabled(String provider) {
		}
		@Override
		public void onProviderEnabled(String provider) {
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
}
