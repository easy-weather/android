package com.keepiteasy.easyweather;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

	private static AssetManager assets;

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
		
		assets = getAssets();

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

		try {
			File httpCacheDir = new File(getApplicationContext().getCacheDir(), "http");
			long httpCacheSize = 1 * 1024 * 1024;
			HttpResponseCache.install(httpCacheDir, httpCacheSize);
		} catch (IOException e) {
			Log.i("uh-oh", "HTTP response cache installation failed:" + e);
		}

		final Intent intent = new Intent(Forecast.this, LoadingActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		HttpResponseCache cache = HttpResponseCache.getInstalled();
		if (cache != null) {
			cache.flush();
		}
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

	public static class ForecastFragment extends Fragment {
		public static final String ARG_SECTION_NUMBER = "section_number";
		private static TextView dayTitle1, dayTitle2, dayTitle3, dayText1, dayText2, dayText3;

		public ForecastFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);

			dayTitle1 = (TextView) rootView.findViewById(R.id.DayTitle1);
			dayTitle2 = (TextView) rootView.findViewById(R.id.DayTitle2);
			dayTitle3 = (TextView) rootView.findViewById(R.id.DayTitle3);
			dayText1 = (TextView) rootView.findViewById(R.id.DayForecast1);
			dayText2 = (TextView) rootView.findViewById(R.id.DayForecast2);
			dayText3 = (TextView) rootView.findViewById(R.id.DayForecast3);

			return rootView;
		}

		public void setForecast() {
			dayTitle1.setVisibility(View.VISIBLE);
			dayTitle2.setVisibility(View.VISIBLE);
			dayTitle3.setVisibility(View.VISIBLE);

			dayText1.setVisibility(View.VISIBLE);
			dayText2.setVisibility(View.VISIBLE);
			dayText3.setVisibility(View.VISIBLE);

			ForecastObject forecast = Forecast.forecastObject;
			ArrayList<ForecastObject.ForecastDay> days = forecast.getForecast();

			for (int i = 0; i < days.size(); i++) {
				ForecastObject.ForecastDay day = days.get(i);

				switch (i) {
					case 0 :
						dayTitle1.setText(day.getDay());
						dayText1.setText(day.getText());
					case 1 :
						dayTitle2.setText(day.getDay());
						dayText2.setText(day.getText());
					case 2 :
						dayTitle3.setText(day.getDay());
						dayText3.setText(day.getText());
				}
			}
		}
	}

	public static class ConditionsFragment extends Fragment {
		public static final String ARG_SECTION_NUMBER = "section_number";
		public static ImageView icon_view;
		public static TextView city, temp, temp_c, humidity_label, humidity, uv_index_label, time, uv_index, precip_label, precip, feels, feels_label, visib, visib_label, windchill, windchill_label;

		public ConditionsFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_conditions, container, false);

			city = (TextView) rootView.findViewById(R.id.city);
			temp = (TextView) rootView.findViewById(R.id.temp_k);
			temp_c = (TextView) rootView.findViewById(R.id.temp_c);

			feels_label = (TextView) rootView.findViewById(R.id.feels_label);
			feels = (TextView) rootView.findViewById(R.id.feels);

			humidity_label = (TextView) rootView.findViewById(R.id.humidity_label);
			humidity = (TextView) rootView.findViewById(R.id.humidity);

			visib_label = (TextView) rootView.findViewById(R.id.visibility_label);
			visib = (TextView) rootView.findViewById(R.id.visibility);

			uv_index_label = (TextView) rootView.findViewById(R.id.uv_label);
			uv_index = (TextView) rootView.findViewById(R.id.uv);

			precip_label = (TextView) rootView.findViewById(R.id.precip_label);
			precip = (TextView) rootView.findViewById(R.id.precip);

			windchill_label = (TextView) rootView.findViewById(R.id.windchill_label);
			windchill = (TextView) rootView.findViewById(R.id.windchill);

			time = (TextView) rootView.findViewById(R.id.time);
			
			icon_view = (ImageView) rootView.findViewById(R.id.imageView1);

			return rootView;
		}

		public void setConditions() {
			ConditionsObject conditions = Forecast.conditionsObject;

			city.setText("For " + conditions.getCity());
			temp.setText(conditions.getTemp() + "K");
			temp_c.setText("(" + conditions.getTemp_c() + "¡ C)");
			humidity.setText(conditions.getHumidity());
			feels.setText(conditions.getFeelslike() + "¡ C");

			if (conditions.getVisibility() != "N/A") {
				visib.setText(conditions.getVisibility() + "km");
			} else {
				visib_label.setVisibility(View.GONE);
				visib.setVisibility(View.GONE);
			}

			if (conditions.getUV() > 0) {
				uv_index.setText(String.valueOf(conditions.getUV()));
			} else {
				uv_index_label.setVisibility(View.GONE);
				uv_index.setVisibility(View.GONE);
			}

			precip.setText(conditions.getPrecip() + "mm");

			if (conditions.getWindchill() != "NA") {
				windchill.setText(conditions.getWindchill());
			} else {
				windchill_label.setVisibility(View.GONE);
				windchill.setVisibility(View.GONE);
			}
			
			try 
			{
			    // get input stream
			    InputStream ims = assets.open(conditions.getIcon()+".gif");
			    // load image as Drawable
			    Drawable d = Drawable.createFromStream(ims, null);
				// set image to ImageView
			    icon_view.setImageDrawable(d);
			}
			catch(IOException ex) 
			{
				icon_view.setVisibility(View.GONE);
			}
			
			//icon.setIm

			time.setTag(conditions.getTime());
		}
	}

}
