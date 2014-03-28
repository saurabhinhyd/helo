package com.kaju.helo.notify;

import android.app.Notification;
import android.content.Context;

import com.kaju.helo.R;

public class NotificationBuilder {
	
	private final Context mContext;
	
	public NotificationBuilder(Context ctx) {
		mContext = ctx;
	}
	
	public Notification build() {
		Notification.Builder mBuilder = new Notification.Builder(mContext)
		.setSmallIcon(R.drawable.ic_hourglass_dark)
		.setContentTitle("My notification")
		.setContentText("Hello World!");		
		
		return mBuilder.build();
	}

}
