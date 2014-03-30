package com.kaju.helo.notify;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.kaju.helo.ContactReminderActivity;
import com.kaju.helo.ContactScore;
import com.kaju.helo.R;

public class NotificationBuilder {
	
	private final Context mContext;
	
	public NotificationBuilder(Context ctx) {
		mContext = ctx;
	}
	
	public Notification build(List<ContactScore> contactScoreList) {
		int numItems = contactScoreList.size();
		switch (numItems) {
		case 0:
			return null;
		case 1:
			return buildForSingleContact(contactScoreList.get(0));
		default:
			return buildForMultipleContacts(contactScoreList);
		}
	}
	
	private Notification buildForSingleContact(ContactScore contactScore) {
		String contactName = contactScore.getDisplayName();
		String lastContactedLabel = mContext.getResources().getString(R.string.last_contacted);
		String lastContactedString = getFriendlyDateString(contactScore.getLastContacted());		
		
		Notification.Builder mBuilder = new Notification.Builder(mContext)
		.setSmallIcon(R.drawable.ic_hourglass_dark)
		.setContentTitle(contactName)
		.setContentText(lastContactedLabel + ": " + lastContactedString)
		.setAutoCancel(true);	
		
		String imageUriString = contactScore.getPhotoThumbnail();
		if (imageUriString != null) {
			Uri imageUri = Uri.parse(contactScore.getPhotoThumbnail());
			try {
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), imageUri);
				mBuilder.setLargeIcon(bitmap);
			} catch (FileNotFoundException e) {
				// do nothing
			} catch (IOException e) {
				// do nothing
			}		
		}
		
		Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactScore.getPhoneNumber()));
		PendingIntent piCallIntent = PendingIntent.getActivity(mContext, 0, callIntent, 0);		
		mBuilder.addAction(R.drawable.ic_action_call_dark, getString(R.string.action_call), piCallIntent);

		mBuilder.setContentIntent(buildContentIntent());		
		
		return mBuilder.build();		
	}
	
	private Notification buildForMultipleContacts(List<ContactScore> contactScoreList) {		
		String pendingCallCount = Integer.toString(contactScoreList.size()); 
		String title = pendingCallCount + " " + getString(R.string.pending_calls);
		
		Notification.InboxStyle notificationStyle = new Notification.InboxStyle();
		for (ContactScore  contact : contactScoreList) {
			notificationStyle.addLine(contact.getDisplayName());
		}		
		
		Notification.Builder mBuilder = new Notification.Builder(mContext)
		.setSmallIcon(R.drawable.ic_hourglass_dark)
		.setContentTitle(title)
		.setStyle(notificationStyle)
		.setAutoCancel(true);	
		
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
	
	private String getFriendlyDateString(Date lastContacted) {
		String friendlyDate;

		if (lastContacted.getTime() == 0) {
			friendlyDate = getString(R.string.never);
		} else {		
			long diff = new Date().getTime() - lastContacted.getTime();
			int diffDays = (int) Math.ceil((double) diff / 86400000L);
			
			Calendar nowCalendar = Calendar.getInstance();
			Calendar thenCalendar = Calendar.getInstance();
			thenCalendar.setTime(lastContacted);
			
			if (nowCalendar.get(Calendar.DAY_OF_MONTH) == thenCalendar.get(Calendar.DAY_OF_MONTH) &&
					nowCalendar.get(Calendar.MONTH) == thenCalendar.get(Calendar.MONTH) &&
					nowCalendar.get(Calendar.YEAR) == thenCalendar.get(Calendar.YEAR)) {
				friendlyDate = getString(R.string.today);
			} else if (diffDays == 1) {
				friendlyDate = getString(R.string.a_day_ago);
			} else {
				friendlyDate = Integer.toString(diffDays) + " " + getString(R.string.days_ago);			
			}
		}
		
		return friendlyDate;
	}	
	
	private String getString(int resId) {
		return mContext.getResources().getString(resId);
	}

}
