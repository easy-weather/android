package com.keepiteasy.easyweather;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ForecastObject {
	private static ArrayList<ForecastDay> forecast = new ArrayList<ForecastDay>();
	
	public ForecastObject(JSONArray data) {
		try {
			for (int x = 0; x < data.length(); x++) {
				JSONObject day = data.getJSONObject(x);
				
				String t = day.getString("text");
				String d = day.getString("day");
				String i = day.getString("icon");
				
				ForecastDay forecastDay = new ForecastDay(t, d, i);
				forecast.add(forecastDay);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<ForecastDay> getForecast() {
		return forecast;
	}
	
	public class ForecastDay {
		private String text;
		private String day;
		private String icon;
		
		public ForecastDay(String t, String d, String i) {
			setText(t);
			setDay(d);
			setIcon(i);
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getDay() {
			return day;
		}

		public void setDay(String day) {
			this.day = day;
		}

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}
	}
}
