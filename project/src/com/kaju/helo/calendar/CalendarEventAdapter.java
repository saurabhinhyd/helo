package com.kaju.helo.calendar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import android.widget.ImageButton;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.kaju.helo.R;

public class CalendarEventAdapter extends ArrayAdapter<String> {

	private final Context mContext;
	private final int mRowLayoutResource;
	private final int mEventType;
	private final List<String> mLookupKeys;
	
	private View.OnClickListener mRemoveBtnClickListener;
	
	public CalendarEventAdapter(Context context, int resource,
			List<String> lookupKeys, int eventType) {
		super(context, resource, lookupKeys);
	
		mContext = context;
		mRowLayoutResource = resource;
		mLookupKeys = lookupKeys;
		mEventType = eventType;
	}

	public void setRemoveButtonClickHandler(final View.OnClickListener onClickListener) {
		this.mRemoveBtnClickListener = onClickListener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(mRowLayoutResource, parent, false);	
		
		String lookupKey = this.mLookupKeys.get(position);
		
		// Contact photo thumbnail
		QuickContactBadge contactBadge = (QuickContactBadge)rowView.findViewById(R.id.contactImageView);
		Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_LOOKUP_URI, lookupKey);
		contactBadge.assignContactUri(contactUri);
		String thumbnail = getContactThumbnail(lookupKey);
		if (thumbnail != null) {
			Uri thumbnailUri = Uri.parse(thumbnail);
			contactBadge.setImageURI(thumbnailUri);
		}
		
		// Contact name
		TextView nameLabel = (TextView)rowView.findViewById(R.id.contactNameTextView);
		String displayName = getContactName(lookupKey);
		nameLabel.setText(displayName);		
		
		// Event start date
		TextView eventDateTextView = (TextView) rowView.findViewById(R.id.eventDateTextView);
		Date eventStartDate = getEventStartDate(lookupKey);
		if (eventStartDate != null)
			eventDateTextView.setText(formatEventDate(eventStartDate));
	
		ImageButton removeBtn = (ImageButton) rowView.findViewById(R.id.contactRemoveBtn);
		removeBtn.setTag(R.id.list_row_position, position);
		removeBtn.setOnClickListener(this.mRemoveBtnClickListener);
		
		return rowView;
	}	
	
	private String getContactName(String lookupKey) {
		String retVal = null;
		
		Cursor c = null;
		
		ContentResolver contentResolver = mContext.getContentResolver();
		try {
			Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_LOOKUP_URI, lookupKey);
			c = contentResolver.query(contactUri, 
									new String[] {Contacts.DISPLAY_NAME_PRIMARY, Contacts.PHOTO_THUMBNAIL_URI}, 
									null, null, null);
			if (c.moveToFirst()) {
				int displayNameColIndex = c.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY);
				
				retVal = c.getString(displayNameColIndex);
			}
		} finally {
			if (c != null)
				c.close();
		}
		
		return retVal;
	}
	
	private String getContactThumbnail(String lookupKey) {
		String retVal = null;
		
		Cursor c = null;
		
		ContentResolver contentResolver = mContext.getContentResolver();
		try {
			Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_LOOKUP_URI, lookupKey);
			c = contentResolver.query(contactUri, 
									new String[] {Contacts.DISPLAY_NAME_PRIMARY, Contacts.PHOTO_THUMBNAIL_URI}, 
									null, null, null);
			if (c.moveToFirst()) {
				int photoThumbnailColIndex = c.getColumnIndex(Contacts.PHOTO_THUMBNAIL_URI);
				retVal = c.getString(photoThumbnailColIndex);
			}
		} finally {
			if (c != null)
				c.close();
		}		  		
		
		return retVal;
	}
	
    private Date getEventStartDate(String lookupKey) {
       	Cursor c = mContext.getContentResolver().query(Data.CONTENT_URI,
    			new String[] {Event.START_DATE},
    			ContactsContract.Contacts.LOOKUP_KEY + "=?" + " AND " +
    			Data.MIMETYPE + "='" + Event.CONTENT_ITEM_TYPE + "'" + " AND " +
    			Event.TYPE + "=?" ,    	                  
    	        new String[] {lookupKey, String.valueOf(mEventType)}, 
    	        null);    	
	
       	Date eventStartDate = null;
       	while (c.moveToNext()) {
    		int startDateColIndex = c.getColumnIndex(Event.START_DATE);    		
    		String eventStartString = c.getString(startDateColIndex);    		
    		if (eventStartString != null) {
    			eventStartDate = parseEventDate(eventStartString);
    			if (eventStartDate != null)
    				break;
    		}
    	}    	
    	c.close();    	

    	return eventStartDate;
    }    
    
    private static String formatEventDate(Date eventDate) {
    	SimpleDateFormat sdf = new SimpleDateFormat("MMMM d", Locale.getDefault());
    	return sdf.format(eventDate);
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
