package com.keepiteasy.easyweather;

import java.util.ArrayList;
import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Forecast extends FragmentActivity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
	 * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
	 * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best to
	 * switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	public static LocationManager lm;

	public static mylocationlistener ll;

	public static double latValue;
	public static double longValue;
	public static Fragment forecastFragment = new ForecastFragment();
	public static Fragment conditionsFragement = new ConditionsFragment();
	public static ForecastObject forecastObject;

	public static Location location;

	private static ConditionsObject conditionsObject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forecast);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
		}

		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		ll = new mylocationlistener();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.forecast, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}
	
	public static void setForecast(ForecastObject data) {
		forecastObject = data;
		((ForecastFragment) forecastFragment).setForecast();
	}
	
	public static void setConditions(ConditionsObject data) {
		conditionsObject = data;
		((ConditionsFragment) conditionsFragement).setConditions();
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the
	 * sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment;
			Bundle args = new Bundle();

			switch (position) {
				case 0 :
					fragment = conditionsFragement;
					
					args.putInt(ConditionsFragment.ARG_SECTION_NUMBER, position + 1);
					fragment.setArguments(args);
					return fragment;
				case 1 :
					fragment = forecastFragment;

					args.putInt(ConditionsFragment.ARG_SECTION_NUMBER, position + 1);
					fragment.setArguments(args);
					return fragment;
				default :
			}
			
			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
				case 0 :
					return getString(R.string.title_section2).toUpperCase(l);
				case 1 :
					return getString(R.string.title_section1).toUpperCase(l);
			}
			return null;
		}
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
				
				ConditionsParser cParser = new ConditionsParser();
				cParser.execute("http://54.245.106.49/easy-weather-api/index.php/weather/conditions/"+la+"/"+lo);
				
				Forecast.lm.removeUpdates(this);
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

	public static class ForecastFragment extends Fragment {
		public static final String ARG_SECTION_NUMBER = "section_number";
		private static TextView dayTitle1, dayTitle2,dayTitle3, dayText1, dayText2, dayText3;
		private static ProgressBar progressBar;

		public ForecastFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);
			progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
			
			dayTitle1 = (TextView) rootView.findViewById(R.id.DayTitle1);
			dayTitle2 = (TextView) rootView.findViewById(R.id.DayTitle2);
			dayTitle3 = (TextView) rootView.findViewById(R.id.DayTitle3);
			dayText1 = (TextView) rootView.findViewById(R.id.DayForecast1);
			dayText2 = (TextView) rootView.findViewById(R.id.DayForecast2);
			dayText3 = (TextView) rootView.findViewById(R.id.DayForecast3);

			return rootView;
		}
		
		public void setForecast() {
			progressBar.setVisibility(View.GONE);
			
			dayTitle1.setVisibility(View.VISIBLE);
			dayTitle2.setVisibility(View.VISIBLE);
			dayTitle3.setVisibility(View.VISIBLE);
			
			dayText1.setVisibility(View.VISIBLE);
			dayText2.setVisibility(View.VISIBLE);
			dayText3.setVisibility(View.VISIBLE);
			
			ForecastObject forecast = Forecast.forecastObject;
			ArrayList<ForecastObject.ForecastDay> days = forecast.getForecast();
			
			for(int i = 0; i < days.size(); i++) {
				ForecastObject.ForecastDay day = days.get(i);
				
				switch (i) {
					case 0: 
						dayTitle1.setText(day.getDay());
						dayText1.setText(day.getText());
					case 1:
						dayTitle2.setText(day.getDay());
						dayText2.setText(day.getText());
					case 2:
						dayTitle3.setText(day.getDay());
						dayText3.setText(day.getText());
				}
			}
		}
	}

	public static class ConditionsFragment extends Fragment {
		public static final String ARG_SECTION_NUMBER = "section_number";
		public static TextView city, temp, temp_c;
		private ProgressBar progressBar;

		public ConditionsFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_conditions, container, false);
			
			progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);

			city = (TextView) rootView.findViewById(R.id.textView1);
			temp = (TextView) rootView.findViewById(R.id.textView2);
			temp_c = (TextView) rootView.findViewById(R.id.textView3);

			return rootView;
		}
		
		public void setConditions() {
			progressBar.setVisibility(View.GONE);
			
			ConditionsObject conditions = Forecast.conditionsObject;
			
			city.setVisibility(View.VISIBLE);
			temp.setVisibility(View.VISIBLE);
			temp_c.setVisibility(View.VISIBLE);
			
			city.setText(conditions.getCity());
			temp.setText(conditions.getTemp());
			temp_c.setText(conditions.getTemp_c());
		}
	}

}
