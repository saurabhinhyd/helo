package com.kaju.helo.calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.Data;

import com.kaju.helo.ContactInfo;

public class ContactEvent {	

	private final ContactInfo mContactInfo;
	
	private int mEventType;

	private Date mEventDate;
	
	private String mEventLabel;
	
	public ContactEvent(ContactInfo contact) {
		mContactInfo = contact;
	}
	
	public ContactInfo getContact() {
		return mContactInfo;
	}
	
	public int getEventType() {
		return mEventType;
	}
	
	public Date getEventDate() {
		return mEventDate;
	}	
	
	public String getEventLabel() {
		return mEventLabel;
	}
	
	public static List<ContactEvent> getAllEventsForContact(Context ctx, String lookupKey) {
		Cursor c = null;
		List<ContactEvent> eventList = new ArrayList<ContactEvent>();
		
		ContactInfo contact = new ContactInfo(lookupKey);
		contact.populate(ctx);
		
		try {						
			c = ctx.getContentResolver().query(Data.CONTENT_URI,
					new String[] {Event.TYPE , Event.START_DATE, Event.LABEL},
					ContactsContract.Contacts.LOOKUP_KEY + "=?" + " AND " +
							Data.MIMETYPE + "='" + Event.CONTENT_ITEM_TYPE + "'",    	                  
					new String[] {lookupKey}, 
					null);
			
			int typeColIndex = c.getColumnIndex(Event.TYPE);
			int startDateColIndex = c.getColumnIndex(Event.START_DATE);
			int labelColIndex = c.getColumnIndex(Event.LABEL);			
			while (c.moveToNext()) {
				int eventType = c.getInt(typeColIndex);
				String eventDate = c.getString(startDateColIndex);
				String eventLabel = c.getString(labelColIndex);
				
				ContactEvent event = new ContactEvent(contact);
				event.mEventType = eventType;
				event.mEventDate = parseEventDate(eventDate);
				event.mEventLabel = eventLabel;
				
				eventList.add(event);
			}
		} finally {
			if (c != null)
				c.close();
		}
		
		return eventList;
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
	
	public static Comparator<ContactEvent> CompareContactName = 
			new Comparator<ContactEvent>() {

		@Override
		public int compare(ContactEvent lhs, ContactEvent rhs) {
			String lhsName = lhs.getContact().getDisplayName();
			String rhsName = rhs.getContact().getDisplayName();
			return lhsName.compareToIgnoreCase(rhsName);					
		}

	};	
	
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
}
