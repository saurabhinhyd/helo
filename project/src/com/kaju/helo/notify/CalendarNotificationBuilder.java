package com.kaju.helo.notify;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.kaju.helo.ContactEvent;
import com.kaju.helo.ContactInfo;
import com.kaju.helo.ContactReminderActivity;
import com.kaju.helo.R;

public class CalendarNotificationBuilder {

	private final Context mContext;
	
	public CalendarNotificationBuilder(Context ctx) {
		mContext = ctx;
	}
	
	public Notification build(ContactEvent contactEvent) {

		ContactInfo contact = contactEvent.getContact();
		String contactName = contact.getDisplayName();

		String eventDesc = getEventLabel(contactEvent);
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
		.setDefaults(Notification.DEFAULT_ALL)
		.setSmallIcon(R.drawable.ic_stat_hourglass_dark)
		.setContentTitle(contactName)
		.setContentText(eventDesc)
		.setAutoCancel(true);			

		String imageUriString = contact.getPhotoThumbnail();
		if (imageUriString != null) {
			Uri imageUri = Uri.parse(imageUriString);
			try {
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), imageUri);
				mBuilder.setLargeIcon(bitmap);
			} catch (FileNotFoundException e) {
				// do nothing
			} catch (IOException e) {
				// do nothing
			}		
		}
		
		Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.getPhoneNumber()));
		PendingIntent piCallIntent = PendingIntent.getActivity(mContext, 0, callIntent, 0);		
		mBuilder.addAction(R.drawable.ic_action_call_dark, getString(R.string.action_call), piCallIntent);
		
		Intent messageIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + contact.getPhoneNumber()));
		PendingIntent piMssgIntent = PendingIntent.getActivity(mContext, 0, messageIntent, 0);		
		mBuilder.addAction(R.drawable.ic_action_message_dark, "Message", piMssgIntent);

		mBuilder.setContentIntent(buildContentIntent());		
		
		return mBuilder.build();		
	}	
	
	private String getEventLabel(ContactEvent event) {
		String eventLabel = "";
		switch (event.getEventType()) {
		case Event.TYPE_BIRTHDAY:
			eventLabel = mContext.getResources().getString(R.string.event_type_birthday);
			break;
		case Event.TYPE_ANNIVERSARY:
			eventLabel = mContext.getResources().getString(R.string.event_type_anniversary);
			break;
		case Event.TYPE_OTHER:
			eventLabel = mContext.getResources().getString(R.string.event_type_other);
			break;
		case Event.TYPE_CUSTOM:
			eventLabel = event.getEventLabel();
			break;
		}
		return eventLabel;
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
