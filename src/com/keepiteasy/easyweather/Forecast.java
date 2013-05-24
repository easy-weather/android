package com.keepiteasy.easyweather;

import java.util.ArrayList;
import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

		final Intent intent = new Intent(Forecast.this, LoadingActivity.class);
		startActivity(intent);
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
		private static TextView dayTitle1, dayTitle2,dayTitle3, dayText1, dayText2, dayText3;

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
		public static TextView city, temp, temp_c, humidity_label, humidity, uv_index_label, uv_index, precip_label, precip;

		public ConditionsFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_conditions, container, false);

			city = (TextView) rootView.findViewById(R.id.textView1);
			temp = (TextView) rootView.findViewById(R.id.textView2);
			temp_c = (TextView) rootView.findViewById(R.id.textView3);
			humidity_label = (TextView) rootView.findViewById(R.id.textView4);
			uv_index_label = (TextView) rootView.findViewById(R.id.textView5);
			precip_label = (TextView) rootView.findViewById(R.id.textView6);
			
			humidity = (TextView) rootView.findViewById(R.id.textView7);
			uv_index = (TextView) rootView.findViewById(R.id.textView8);
			precip = (TextView) rootView.findViewById(R.id.textView9);

			return rootView;
		}
		
		public void setConditions() {
			ConditionsObject conditions = Forecast.conditionsObject;
			
			uv_index_label.setVisibility(View.VISIBLE);
			humidity_label.setVisibility(View.VISIBLE);
			precip_label.setVisibility(View.VISIBLE);
			
			city.setVisibility(View.VISIBLE);
			temp.setVisibility(View.VISIBLE);
			temp_c.setVisibility(View.VISIBLE);
			uv_index.setVisibility(View.VISIBLE);
			humidity.setVisibility(View.VISIBLE);
			
			city.setText("For " + conditions.getCity());
			temp.setText(conditions.getTemp() + "K");
			temp_c.setText("(" + conditions.getTemp_c() + "� C)");
			humidity.setText(conditions.getHumidity());
			uv_index.setText(conditions.getUV());
			precip.setText(conditions.getPrecip() + "mm");
		}
	}

}
