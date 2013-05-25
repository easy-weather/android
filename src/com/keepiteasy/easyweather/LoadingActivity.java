package com.keepiteasy.easyweather;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LoadingActivity extends Activity {

	private static final long MINTIME = 1800000;
	private LocationManager lm;
	private mylocationlistener ll;
	private Activity activity;
	private Location lastLocation = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		
		activity = this;

		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		ll = new mylocationlistener();

		Date date = new Date();
		long lastSafeTime = date.getTime() + MINTIME;
		
		List<String> matchingProviders = lm.getAllProviders();
		for (String provider: matchingProviders) {
			Location location = lm.getLastKnownLocation(provider);
			
			 if (location != null) {
			    long time = location.getTime();
		        
			    if (time < lastSafeTime) {
					lastLocation = location;
			    }
			 }
		}
		
		if(lastLocation == null) {
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
		} else {
			loadWeather();
		}
	}
	
	protected void loadWeather() {
		Forecast.location = lastLocation;
		
		String la = String.valueOf(lastLocation.getLatitude());
		String lo = String.valueOf(lastLocation.getLongitude());
		
		ForecastParser parser = new ForecastParser();
		parser.execute("http://54.245.106.49/easy-weather-api/index.php/weather/forecast/"+la+"/"+lo);
		
		ConditionsParser cParser = new ConditionsParser(activity);
		cParser.execute("http://54.245.106.49/easy-weather-api/index.php/weather/conditions/"+la+"/"+lo);
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
