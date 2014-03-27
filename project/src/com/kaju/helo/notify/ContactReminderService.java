package com.kaju.helo.notify;

import com.kaju.helo.R;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

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
		boolean pendingCalls = true;
		
		if (pendingCalls) {
			fireNotification();
		}
	}	
	
	private void fireNotification() {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
										.setSmallIcon(R.drawable.ic_hourglass_dark)
										.setContentTitle("My notification")
										.setContentText("Hello World!");
		
		// Sets an ID for the notification
		int mNotificationId = 001;

		// Gets an instance of the NotificationManager service
		NotificationManager mNotifyMgr = 
		        (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		
		// Builds the notification and issues it.
		mNotifyMgr.notify(mNotificationId, mBuilder.build());		
	}		

}
