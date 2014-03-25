package com.kaju.helo.notify;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationScheduler {
	private static NotificationScheduler sInstance;
	
	private AlarmManager mAlarmMgr;
	
	private PendingIntent mAlarmIntent;	
	
	private NotificationScheduler(Context context) {
	
		mAlarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		
		Intent intent = new Intent(context, ContactReminderService.class);
		mAlarmIntent = PendingIntent.getService(context, 0, intent, 0);		
	}
	
	public static NotificationScheduler getInstance(Context ctx) {
		if (sInstance == null) {
			sInstance = new NotificationScheduler(ctx); 
		}
		
		return sInstance;
	}

	public void scheduleDailyAt(int hourOfDay) {		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);

		// With setInexactRepeating(), you have to use one of the AlarmManager interval
		// constants--in this case, AlarmManager.INTERVAL_DAY.
		mAlarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
		        AlarmManager.INTERVAL_DAY, mAlarmIntent);		
	}

}
