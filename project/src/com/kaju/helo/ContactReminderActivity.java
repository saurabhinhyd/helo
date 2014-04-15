package com.kaju.helo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.kaju.helo.calendar.CalendarEventActivity;
//import com.kaju.helo.notify.TestNotifications;
import com.kaju.helo.groups.ContactGroupsActivity;
import com.kaju.helo.groups.PrefsDBHelper;
import com.kaju.helo.notify.NotificationScheduler;
import com.kaju.helo.settings.SettingsActivity;

public class ContactReminderActivity extends ListActivity {

	
	class ContactScoreDesc implements Comparator<ContactScore> {
		@Override
		public int compare(ContactScore o1, ContactScore o2) {
			return Double.compare(o2.getScore(), o1.getScore());
		}			
	}
	
	ArrayList<ContactScore> mContactList;
	
	ContactScoreRowAdapter mAdapter; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_layout_contact_reminder);
		
		mContactList = new ArrayList<ContactScore>();
				
		mAdapter = new ContactScoreRowAdapter(this, mContactList);
		mAdapter.setDialButtonClickHandler(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ImageButton dialBtn = (ImageButton)v;
				String phoneNumber = (String) dialBtn.getTag(R.id.contact_phone_number);
				
		    	Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
		    	startActivity(intent);    				
			}
		});
		
		setListAdapter(mAdapter);

		// initialize the associated SharedPreferences file with default values 
		// for each Preference when the user first opens the application
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);		
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isFirstRun = prefs.getBoolean("isFirstRun", true);
		if (isFirstRun) {		
			setupNotifications();
			
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("isFirstRun", false);
			editor.commit();						
		}
	}
	
	@Override
	protected void onStart() {
	    super.onStart(); 
	    
	    mContactList.clear();
	    
		PrefsDBHelper db = new PrefsDBHelper(this);
		for (String lookupKey : db.getAllContacts()) {
			ContactScore contactScore = new ContactScore(lookupKey);
			contactScore.populate(this);
			mContactList.add(contactScore);
		}
		
		Collections.sort(mContactList, new ContactScoreDesc());	    
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.actions_contact_reminder, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
        case R.id.action_calendar:
        	startCalendarActivity();
        	return true;
        case R.id.action_groups:
        	startGroupsActivity();
        	return true;
        case R.id.action_settings:
        	startSettingsActivity();
        	return true;
        default:
        	return super.onOptionsItemSelected(item);
        }
    }
    
    private void startCalendarActivity() {
    	Intent intent = new Intent(this, CalendarEventActivity.class);
    	startActivity(intent);    	
    }
    
    private void startGroupsActivity() {
    	Intent intent = new Intent(this, ContactGroupsActivity.class);
    	try {
    		startActivity(intent);
    	} catch(Exception e) {
    		System.out.println(e);
    	}
    }
    
    private void startSettingsActivity() {
    	Intent intent = new Intent(this, SettingsActivity.class);
    	startActivity(intent);
    }
    
    private void setupNotifications() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean doNotifications = prefs.getBoolean(SettingsActivity.KEY_PREF_NOTIFICATIONS, true);
		
		if (doNotifications) {
			int notificationTime = prefs.getInt(SettingsActivity.KEY_PREF_NOTIFICATION_TIME, 900);
			int notificationHour = notificationTime / 100;
			int notificationMinute = notificationTime % 100;
			
			NotificationScheduler notificationScheduler = NotificationScheduler.getInstance(this);
			notificationScheduler.scheduleDailyAt(notificationHour, notificationMinute);
			BootCompleteReceiver.enableBroadcastReceiver(this);			
		}
		    	
    }
}
