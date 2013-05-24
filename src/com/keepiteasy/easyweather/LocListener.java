package com.keepiteasy.easyweather;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocListener extends Activity implements LocationListener {
	LocationManager mLocationManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
			Forecast.location = location;
			//((ForecastFragment) Forecast.forecastFragment).setPosition(location.getLatitude(), location.getLongitude());
		} else {
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			Forecast.location = location;
			//((ForecastFragment) Forecast.forecastFragment).setPosition(location.getLatitude(), location.getLongitude());
			mLocationManager.removeUpdates(this);
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
