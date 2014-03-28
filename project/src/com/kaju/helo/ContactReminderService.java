package com.kaju.helo;

import java.util.ArrayList;

import android.app.IntentService;
import android.content.Intent;

import com.kaju.helo.groups.PrefsDBHelper;
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
		contactList.clear();
	    
		PrefsDBHelper db = new PrefsDBHelper(this);
		for (String lookupKey : db.getAllContacts()) {
			ContactScore contactScore = new ContactScore(lookupKey);
			contactScore.populate(this);
			if (contactScore.getScore() >= 1) {
				contactList.add(contactScore);
			}
		}	
		
		boolean pendingCalls = contactList.size() > 0;
		
		if (pendingCalls) {
			fireNotification();
		}
	}	
	
	private void fireNotification() {
		NotificationBuilder builder = new NotificationBuilder(this);
		
		NotificationDispatcher dispatcher = new NotificationDispatcher(this);
		
		dispatcher.dispatchNotification(builder.build());
	}		
}
