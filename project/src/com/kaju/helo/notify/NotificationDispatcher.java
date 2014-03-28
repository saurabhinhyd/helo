package com.kaju.helo.notify;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

public class NotificationDispatcher {
	
	private final Context mContext;	
	
	public NotificationDispatcher(Context ctx) {
		mContext = ctx;		
	}

	public void dispatchNotification(Notification notification) {
		NotificationManager notifyMgr = 
		        (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		
		// Sets an ID for the notification
		int notificationId = 001;
		
		// Builds the notification and issues it.
		notifyMgr.notify(notificationId, notification);		
	}
}
