package com.kaju.helo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.provider.ContactsContract.CommonDataKinds.Event;

import com.kaju.helo.calendar.ContactEvent;
import com.kaju.helo.groups.PrefsDBHelper;
import com.kaju.helo.notify.CalendarNotificationBuilder;
import com.kaju.helo.notify.NotificationBuilder;
import com.kaju.helo.notify.NotificationDispatcher;

public class ContactReminderService extends IntentService {

	/**
	   * A constructor is required, and must call the super IntentService(String)
	   * constructor with a name for the worker thread.
	   */
	public ContactReminderService() {
		super("ContactReminderService");
	}	
	
	/**
	   * The IntentService calls this method from the default worker thread with
	   * the intent that started the service. When this method returns, IntentService
	   * stops the service, as appropriate.
	   */
	@Override
	protected void onHandleIntent(Intent intent) {
		ArrayList<ContactScore> contactList = new ArrayList<ContactScore>();		
	    
		PrefsDBHelper db = new PrefsDBHelper(this);
		for (String lookupKey : db.getAllContacts()) {
			ContactScore contactScore = new ContactScore(lookupKey);
			contactScore.populate(this);
			if (contactScore.getScore() >= 1) {
				contactList.add(contactScore);
			}
		}		
		
		fireNotification(contactList);
		
		ArrayList<ContactEvent> contactEvents = new ArrayList<ContactEvent>();
		for (String lookupKey : db.getAllContactEvents()) {
			ContactEvent event = new ContactEvent(lookupKey, Event.TYPE_BIRTHDAY);
			event.populate(this);
			if (filterEvent(event)) {
				contactEvents.add(event);
			}
		}
		
		fireCalendarNotifications(contactEvents);
	}	
	
	private boolean filterEvent(ContactEvent event) {
		Date eventDate = event.getEventDate();
    	if (eventDate == null)
    		return false;
    	
    	Calendar eventCalendar = Calendar.getInstance();
    	eventCalendar.setTime(eventDate);
    	
    	Calendar today = Calendar.getInstance();
    	
    	return today.get(Calendar.DAY_OF_MONTH) == eventCalendar.get(Calendar.DAY_OF_MONTH) &&
    			today.get(Calendar.MONTH) == eventCalendar.get(Calendar.MONTH);		
	}
	
	private void fireNotification(List<ContactScore> contactList) {
		NotificationBuilder builder = new NotificationBuilder(this);
		
		NotificationDispatcher dispatcher = new NotificationDispatcher(this);
		
		dispatcher.dispatchNotification(builder.build(contactList));
	}	
	
	private void fireCalendarNotifications(List<ContactEvent> contactEvents) {
		CalendarNotificationBuilder builder = new CalendarNotificationBuilder(this);
		
		NotificationDispatcher dispatcher = new NotificationDispatcher(this);
		
		for (ContactEvent event : contactEvents) {
			dispatcher.dispatchCalendarNotification(builder.build(event), event);
		}
	}
}
