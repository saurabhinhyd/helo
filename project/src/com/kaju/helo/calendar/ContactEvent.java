package com.kaju.helo.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.Data;

import com.kaju.helo.ContactInfo;

public class ContactEvent extends ContactInfo {	

	private final int mEventType;

	private Date mEventDate;

	public static boolean isToday(ContactEvent event) {
    	Date eventDate = event.getEventDate();
    	if (eventDate == null)
    		return false;
    	
    	Calendar eventCalendar = Calendar.getInstance();
    	eventCalendar.setTime(eventDate);
    	
    	Calendar today = Calendar.getInstance();
    	
    	return today.get(Calendar.DAY_OF_MONTH) == eventCalendar.get(Calendar.DAY_OF_MONTH) &&
    			today.get(Calendar.MONTH) == eventCalendar.get(Calendar.MONTH);		
	}
	
	public ContactEvent(String lookupKey, int eventType) {
		super(lookupKey);		
		mEventType = eventType;
	}
	
	public int getEventType() {
		return mEventType;
	}
	
	public Date getEventDate() {
		return mEventDate;
	}
	
	@Override
	public void populate(Context ctx) {
		super.populate(ctx);

		Cursor c = null;
		try {						
			c = ctx.getContentResolver().query(Data.CONTENT_URI,
					new String[] {Event.START_DATE},
					ContactsContract.Contacts.LOOKUP_KEY + "=?" + " AND " +
							Data.MIMETYPE + "='" + Event.CONTENT_ITEM_TYPE + "'" + " AND " +
							Event.TYPE + "=?" ,    	                  
							new String[] {getLookupKey(), String.valueOf(mEventType)}, 
							null);    	

			Date eventStartDate = null;
			while (c.moveToNext()) {
				int startDateColIndex = c.getColumnIndex(Event.START_DATE);    		
				String eventStartString = c.getString(startDateColIndex);    		
				if (eventStartString != null) {
					eventStartDate = parseEventDate(eventStartString);
					if (eventStartDate != null) {
						mEventDate = eventStartDate;
						break;
					}
				}
			}    	

			c.close();    	

		} finally {
			if (c != null)
				c.close();
		}

	}

	private final static SimpleDateFormat[] sEventDateFormats = new SimpleDateFormat[] {
		new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
		new SimpleDateFormat("--MM-dd", Locale.getDefault()),
		new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) //skype
	};    

	private static Date parseEventDate(String str) {
		Date retVal = null;
		for(SimpleDateFormat format : sEventDateFormats) {
			try {
				retVal = format.parse(str);  
				break;
			} catch (Exception e) {
				retVal = null;
			}
		}

		return retVal;
	}	   
}
