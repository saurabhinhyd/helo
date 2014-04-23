package com.kaju.helo.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;

public class ContactEvent {

	private final String mLookupKey;

	private final int mEventType;

	private String mDisplayName;
	private Date mEventDate;
	private String mThumbnail;
	private String mPhotoUri;
	private String mPhoneNumber;
	
	public static Comparator<ContactEvent> CompareName = 
			new Comparator<ContactEvent>() {

				@Override
				public int compare(ContactEvent lhs, ContactEvent rhs) {
					return lhs.getDisplayName().compareToIgnoreCase(rhs.getDisplayName());					
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
	
	public ContactEvent(String lookupKey, int eventType) {
		mLookupKey = lookupKey;
		mEventType = eventType;
	}
	
	public String getLookupKey() {
		return mLookupKey;
	}
	
	public int getEventType() {
		return mEventType;
	}

	public Uri getLookupUri() {
		return Uri.withAppendedPath(Contacts.CONTENT_LOOKUP_URI, mLookupKey);
	}
	
	public String getDisplayName() {
		return mDisplayName;
	}

	public String getPhoneNumber() {
		return mPhoneNumber;
	}

	public String getPhotoThumbnail() {
		return mThumbnail;
	}	

	public String getPhoto() {
		return mPhotoUri;
	}
	
	public Date getEventDate() {
		return mEventDate;
	}
	
	public void populate(Context ctx) {
		Uri lookupUri = getLookupUri();

		ContentResolver contentResolver = ctx.getContentResolver();

		Cursor c = null;
		try {
			String contactId = null;
			c = contentResolver.query(lookupUri, null, null, null, null);
			if (c.moveToFirst()) {
				int displayNameColIndex = c.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY);				
				int photoThumbnailColIndex = c.getColumnIndex(Contacts.PHOTO_THUMBNAIL_URI);
				int photoUriColIndex = c.getColumnIndex(Contacts.PHOTO_URI);				
				int idColIndex = c.getColumnIndex(BaseColumns._ID);

				mDisplayName = c.getString(displayNameColIndex);
				contactId = c.getString(idColIndex);
				mThumbnail = c.getString(photoThumbnailColIndex);
				mPhotoUri = c.getString(photoUriColIndex);
			}

			String queryString = Data.CONTACT_ID + "=?" + " AND "
					+ Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "'";

			c = contentResolver.query(Data.CONTENT_URI,
					new String[] {BaseColumns._ID, Phone.NUMBER, Phone.IS_PRIMARY},
					queryString,
					new String[] {String.valueOf(contactId)}, null);

			while (c.moveToNext()) {
				int phoneNumberColIndex = c.getColumnIndex(Phone.NUMBER);
				int isPrimaryColIndex = c.getColumnIndex(Phone.IS_PRIMARY);
				mPhoneNumber = c.getString(phoneNumberColIndex);
				int isPrimary = c.getInt(isPrimaryColIndex);
				if (isPrimary != 0) 
					break;
			}
			
			c = ctx.getContentResolver().query(Data.CONTENT_URI,
					new String[] {Event.START_DATE},
					ContactsContract.Contacts.LOOKUP_KEY + "=?" + " AND " +
							Data.MIMETYPE + "='" + Event.CONTENT_ITEM_TYPE + "'" + " AND " +
							Event.TYPE + "=?" ,    	                  
							new String[] {mLookupKey, String.valueOf(mEventType)}, 
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
