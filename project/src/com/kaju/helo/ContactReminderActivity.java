package com.kaju.helo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.kaju.helo.calendar.CalendarEventActivity;
import com.kaju.helo.calendar.ContactEvent;
//import com.kaju.helo.notify.TestNotifications;
import com.kaju.helo.groups.ContactGroupsActivity;
import com.kaju.helo.groups.PrefsDBHelper;
import com.kaju.helo.notify.NotificationScheduler;
import com.kaju.helo.settings.SettingsActivity;

public class ContactReminderActivity extends ListActivity {
	ArrayList<ContactScore> mContactList;
	
	ContactScoreRowAdapter mAdapter;
	
	ListView mEventsTodayListView;
	ArrayList<ContactEvent> mContactEventsList;
	EventsTodayAdapter mEventsTodayAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_layout_contact_reminder);
		
		View birthdayHeader = getLayoutInflater().inflate(R.layout.header_layout_events_today, null);
		mEventsTodayListView = (ListView) findViewById(R.id.eventsTodayList);		
		mEventsTodayListView.addHeaderView(birthdayHeader);
		mContactEventsList = new ArrayList<ContactEvent>();
		mEventsTodayAdapter = new EventsTodayAdapter(this, R.layout.row_layout_events_today, 
														mContactEventsList); 
		mEventsTodayListView.setAdapter(mEventsTodayAdapter);
		
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
	    
	    PrefsDBHelper db = new PrefsDBHelper(this);	    
	    
	    mEventsTodayAdapter.clear();
	    for (String lookupKey : db.getAllContactEvents()) {
	    	ContactEvent event = new ContactEvent(lookupKey, Event.TYPE_BIRTHDAY);
	    	event.populate(this);
	    	if (filter(event)) {
	    		mEventsTodayAdapter.add(event);
	    	}
	    }

    	mEventsTodayAdapter.sort(ContactEvent.CompareName);
    	
	    if (mEventsTodayAdapter.getCount() > 0) {
	    	mEventsTodayListView.setVisibility(View.VISIBLE);
	    } else {
	    	mEventsTodayListView.setVisibility(View.GONE);
	    }
	    
	    mAdapter.clear();
		for (String lookupKey : db.getAllContacts()) {
			ContactScore contactScore = new ContactScore(lookupKey);
			contactScore.populate(this);
			mAdapter.add(contactScore);
		}
		
		mAdapter.sort(ContactScore.ContactScoreDesc);
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
    
    private boolean filter(ContactEvent event) {
    	Date eventDate = event.getEventDate();
    	if (eventDate == null)
    		return false;
    	
    	Calendar eventCalendar = Calendar.getInstance();
    	eventCalendar.setTime(eventDate);
    	
    	Calendar today = Calendar.getInstance();
    	
    	return today.get(Calendar.DAY_OF_MONTH) == eventCalendar.get(Calendar.DAY_OF_MONTH) &&
    			today.get(Calendar.MONTH) == eventCalendar.get(Calendar.MONTH);
    }
}
