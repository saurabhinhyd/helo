package com.kaju.helo.settings;

import com.kaju.helo.notify.NotificationScheduler;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class SettingsActivity extends Activity
								implements OnSharedPreferenceChangeListener {
	
	public static final String KEY_PREF_NOTIFICATIONS = "pref_notifications";
	
	public static final String KEY_PREF_NOTIFY_AT_HOUR = "pref_notify_at_hour";
	
	public static final String KEY_PREF_NOTIFY_AT_MINUTE = "pref_notify_at_minute";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();        
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		boolean doNotifications = sharedPreferences.getBoolean(KEY_PREF_NOTIFICATIONS, true);
		int notificationHour = sharedPreferences.getInt(KEY_PREF_NOTIFY_AT_HOUR, 9);
		int notificationMinute = sharedPreferences.getInt(KEY_PREF_NOTIFY_AT_MINUTE, 0);
		
		NotificationScheduler notificationScheduler = NotificationScheduler.getInstance(this);
		
		if (key.equals(KEY_PREF_NOTIFICATIONS) && !doNotifications) {
			notificationScheduler.disableNotifications();			
		} else {
			notificationScheduler.scheduleDailyAt(notificationHour, notificationMinute);
		}
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    prefs.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
	    super.onPause();
	    
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    prefs.unregisterOnSharedPreferenceChangeListener(this);
	}	
}
