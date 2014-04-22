package com.kaju.helo.notify;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.kaju.helo.ContactReminderActivity;
import com.kaju.helo.R;
import com.kaju.helo.calendar.ContactEvent;

public class CalendarNotificationBuilder {

	private final Context mContext;
	
	public CalendarNotificationBuilder(Context ctx) {
		mContext = ctx;
	}
	
	public Notification build(ContactEvent contactEvent) {

		String contactName = contactEvent.getDisplayName();

		String eventDesc = "";
		switch (contactEvent.getEventType()) {
		case Event.TYPE_BIRTHDAY:
			eventDesc = getString(R.string.event_type_birthday);
			break;
		case Event.TYPE_ANNIVERSARY:
			break;
		case Event.TYPE_OTHER:
			break;
		}
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
		.setDefaults(Notification.DEFAULT_ALL)
		.setSmallIcon(R.drawable.ic_stat_hourglass_dark)
		.setContentTitle(contactName)
		.setContentText(eventDesc)
		.setAutoCancel(true);			

		String imageUriString = contactEvent.getPhotoThumbnail();
		if (imageUriString != null) {
			Uri imageUri = Uri.parse(contactEvent.getPhotoThumbnail());
			try {
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), imageUri);
				mBuilder.setLargeIcon(bitmap);
			} catch (FileNotFoundException e) {
				// do nothing
			} catch (IOException e) {
				// do nothing
			}		
		}
		
		Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactEvent.getPhoneNumber()));
		PendingIntent piCallIntent = PendingIntent.getActivity(mContext, 0, callIntent, 0);		
		mBuilder.addAction(R.drawable.ic_action_call_dark, getString(R.string.action_call), piCallIntent);

		mBuilder.setContentIntent(buildContentIntent());		
		
		return mBuilder.build();		
	}	
	
	private PendingIntent buildContentIntent() {
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(mContext, ContactReminderActivity.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(ContactReminderActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
									            0,
									            PendingIntent.FLAG_UPDATE_CURRENT
									        );		
		return resultPendingIntent;
	}
	
	private String getString(int resId) {
		return mContext.getResources().getString(resId);
	}	
}
