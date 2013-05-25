package com.keepiteasy.easyweather;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ConditionsObject {
	private String city, state, icon, temp, temp_c, feelslike, humidity,
	visibility, windchill, time;
	
	private int precip, UV;
	
	public ConditionsObject(JSONObject data) {
		try {
			city = data.getString("city");
			state = data.getString("state");
			icon = data.getString("icon");
			temp = data.getString("temp");
			temp_c = data.getString("temp_c");
			feelslike = data.getString("feelslike");
			humidity = data.getString("humidity");
			UV = data.getInt("UV");
			visibility = data.getString("visibility");
			precip = data.getInt("precip");
			windchill = data.getString("windchill");
			time = data.getString("time");
			
		} catch (JSONException e) {
			Log.d("error parsing data", e.getMessage());
		}
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getWindchill() {
		return windchill;
	}

	public void setWindchill(String windchill) {
		this.windchill = windchill;
	}

	public int getPrecip() {
		return precip;
	}

	public void setPrecip(int precip) {
		this.precip = precip;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public String getTemp_c() {
		return temp_c;
	}

	public void setTemp_c(String temp_c) {
		this.temp_c = temp_c;
	}

	public String getFeelslike() {
		return feelslike;
	}

	public void setFeelslike(String feelslike) {
		this.feelslike = feelslike;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	public int getUV() {
		return UV;
	}

	public void setUV(int uv) {
		UV = uv;
	}
}
