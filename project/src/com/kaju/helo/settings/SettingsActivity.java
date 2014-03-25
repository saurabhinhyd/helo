package com.kaju.helo.settings;

import com.kaju.helo.notify.NotificationScheduler;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;

public class SettingsActivity extends Activity
								implements OnSharedPreferenceChangeListener {
	
	public static final String KEY_PREF_NOTIFICATIONS = "pref_notifications";
	
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
		if (key.equals(KEY_PREF_NOTIFICATIONS)) {
			boolean doNotifications = sharedPreferences.getBoolean(key, true);
			NotificationScheduler notificationScheduler = NotificationScheduler.getInstance(this);
			if (notificationScheduler != null) {
				if (doNotifications) {
					notificationScheduler.scheduleDailyAt(9);
				} else {				
					notificationScheduler.disableNotifications();				
				}
			}
		}		
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    getPreferences(MODE_PRIVATE)
	            .registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
	    super.onPause();
	    getPreferences(MODE_PRIVATE)
	            .unregisterOnSharedPreferenceChangeListener(this);
	}	
}
