package com.kaju.helo.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.kaju.helo.BootCompleteReceiver;
import com.kaju.helo.notify.NotificationScheduler;

public class SettingsActivity extends Activity
								implements OnSharedPreferenceChangeListener {
	
	public static final String KEY_PREF_NOTIFICATIONS = "pref_notifications";
	
	public static final String KEY_PREF_NOTIFICATION_TIME = "pref_notification_time";
	
	private PreferenceFragment mSettingsFragment;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mSettingsFragment = new SettingsFragment();
        
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, mSettingsFragment)
                .commit();        
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		boolean doNotifications = sharedPreferences.getBoolean(KEY_PREF_NOTIFICATIONS, true);
		
		int notificationTime = sharedPreferences.getInt(KEY_PREF_NOTIFICATION_TIME, 900);
		int notificationHour = notificationTime / 100;
		int notificationMinute = notificationTime % 100;

		NotificationScheduler notificationScheduler = NotificationScheduler.getInstance(this);
		
		if (key.equals(KEY_PREF_NOTIFICATIONS) && !doNotifications) {
			notificationScheduler.disableNotifications();	
			BootCompleteReceiver.disableBroadcastReceiver(this);
		} else {			
			notificationScheduler.scheduleDailyAt(notificationHour, notificationMinute);
			BootCompleteReceiver.enableBroadcastReceiver(this);
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
