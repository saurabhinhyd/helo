package com.kaju.helo.notify;

import com.kaju.helo.calendar.ContactEvent;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

public class NotificationDispatcher {
	
	public static final int NOTIFICATION_ID = 001;
	
	private final Context mContext;	
	
	public NotificationDispatcher(Context ctx) {
		mContext = ctx;		
	}

	public void dispatchNotification(Notification notification) {
		NotificationManager notifyMgr = 
		        (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		
		// Builds the notification and issues it.
		if (notification != null) {
			notifyMgr.notify(NOTIFICATION_ID, notification);
		}
	}
	
	public void dispatchCalendarNotification(Notification notification, ContactEvent event) {
		NotificationManager notifyMgr = 
		        (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		
		// Builds the notification and issues it.
		if (notification != null) {
			notifyMgr.notify(event.getLookupKey(), event.getEventType(), notification);
		}		
	}
}
