package com.kaju.helo;

import com.kaju.helo.notify.NotificationScheduler;
import com.kaju.helo.settings.SettingsActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
			
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			boolean doNotifications = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_NOTIFICATIONS, true);
			
			if (doNotifications) {
				int notificationTime = sharedPreferences.getInt(SettingsActivity.KEY_PREF_NOTIFICATION_TIME, 900);
				int notificationHour = notificationTime / 100;
				int notificationMinute = notificationTime % 100;			
				
				NotificationScheduler notificationScheduler = NotificationScheduler.getInstance(context);
				notificationScheduler.scheduleDailyAt(notificationHour, notificationMinute);
			}
        }		
	}
	
	public static void enableBroadcastReceiver(Context context) {
		ComponentName receiver = new ComponentName(context, BootCompleteReceiver.class);
		PackageManager pm = context.getPackageManager();

		pm.setComponentEnabledSetting(receiver,
		        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
		        PackageManager.DONT_KILL_APP);		
	}
	
	public static void disableBroadcastReceiver(Context context) {
		ComponentName receiver = new ComponentName(context, BootCompleteReceiver.class);
		PackageManager pm = context.getPackageManager();

		pm.setComponentEnabledSetting(receiver,
		        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
		        PackageManager.DONT_KILL_APP);		
	}

}
