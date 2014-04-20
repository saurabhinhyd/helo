package com.kaju.helo.calendar;

import java.util.List;

import com.kaju.helo.R;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.QuickContactBadge;
import android.widget.TextView;

public class CalendarEventAdapter extends ArrayAdapter<String> {

	private final Context mContext;
	private final int mRowLayoutResource;
	private final int mEventType;
	private final List<String> mLookupKeys;
	
	private String mDisplayName;
	private String mThumbnail;
	private String mEventStartDate;
	
	public CalendarEventAdapter(Context context, int resource,
			List<String> lookupKeys, int eventType) {
		super(context, resource, lookupKeys);
	
		mContext = context;
		mRowLayoutResource = resource;
		mLookupKeys = lookupKeys;
		mEventType = eventType;
		
		mDisplayName = null;
		mThumbnail = null;
		mEventStartDate = null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(mRowLayoutResource, parent, false);	
		
		String lookupKey = this.mLookupKeys.get(position);
		fetchContactDetails(lookupKey);		
		
		// Contact photo thumbnail
		QuickContactBadge contactBadge = (QuickContactBadge)rowView.findViewById(R.id.contactImageView);
		Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_LOOKUP_URI, lookupKey);
		contactBadge.assignContactUri(contactUri);
		if (mThumbnail != null) {
			Uri thumbnailUri = Uri.parse(mThumbnail);
			contactBadge.setImageURI(thumbnailUri);
		}
		
		// Contact name
		TextView nameLabel = (TextView)rowView.findViewById(R.id.contactNameTextView);
		nameLabel.setText(mDisplayName);		
		
		// Event start date
		TextView eventDateTextView = (TextView) rowView.findViewById(R.id.eventDateTextView);
		mEventStartDate = retrieveEventStartDate(lookupKey);
		if (mEventStartDate != null)
			eventDateTextView.setText(mEventStartDate);
		
		return rowView;
	}	
	
	private void fetchContactDetails(String lookupKey) {
		Cursor c = null;
		
		ContentResolver contentResolver = mContext.getContentResolver();
		try {
			Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_LOOKUP_URI, lookupKey);
			c = contentResolver.query(contactUri, 
									new String[] {Contacts.DISPLAY_NAME_PRIMARY, Contacts.PHOTO_THUMBNAIL_URI}, 
									null, null, null);
			if (c.moveToFirst()) {
				int displayNameColIndex = c.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY);
				int photoThumbnailColIndex = c.getColumnIndex(Contacts.PHOTO_THUMBNAIL_URI);
				
				mDisplayName = c.getString(displayNameColIndex);
				mThumbnail = c.getString(photoThumbnailColIndex);
			}
		} finally {
			if (c != null)
				c.close();
		}		    	
	}
	
    private String retrieveEventStartDate(String lookupKey) {
       	Cursor c = mContext.getContentResolver().query(Data.CONTENT_URI,
    			new String[] {Event.START_DATE},
    			ContactsContract.Contacts.LOOKUP_KEY + "=?" + " AND " +
    			Data.MIMETYPE + "='" + Event.CONTENT_ITEM_TYPE + "'" + " AND " +
    			Event.TYPE + "=?" ,    	                  
    	        new String[] {lookupKey, String.valueOf(mEventType)}, 
    	        null);    	
	
       	String eventStartDate = null;
    	if (c.moveToNext()) {
    		int startDateColIndex = c.getColumnIndex(Event.START_DATE);    		
    		eventStartDate = c.getString(startDateColIndex);
    	}    	
    	c.close();
    	
    	return eventStartDate;
    }    
	
}
