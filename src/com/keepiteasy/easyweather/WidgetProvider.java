package com.keepiteasy.easyweather;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
		ConditionsObject conditions = Forecast.getConditionsObject();

		for (int widgetId : appWidgetManager.getAppWidgetIds(thisWidget)) {			
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);	
	
			remoteViews.setTextViewText(R.id.tempTextView, conditions.getIcon() + " " + conditions.getTemp() + "K");	
			remoteViews.setTextViewText(R.id.cityTextView, conditions.getCity());

			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}
}
