package com.keepiteasy.easyweather;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LoadingActivity extends Activity {

	private LocationManager lm;
	private mylocationlistener ll;
	private Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);

		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		ll = new mylocationlistener();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
		
		activity = this;
	}

	public class mylocationlistener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {				
				Forecast.location = location;
				
				String la = String.valueOf(location.getLatitude());
				String lo = String.valueOf(location.getLongitude());
				
				ForecastParser parser = new ForecastParser();
				parser.execute("http://54.245.106.49/easy-weather-api/index.php/weather/forecast/"+la+"/"+lo);
				
				ConditionsParser cParser = new ConditionsParser(activity);
				cParser.execute("http://54.245.106.49/easy-weather-api/index.php/weather/conditions/"+la+"/"+lo);
				
				lm.removeUpdates(this);
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
