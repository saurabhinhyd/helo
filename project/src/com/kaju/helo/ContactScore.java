package com.kaju.helo;

import java.util.Date;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;

import com.kaju.helo.groups.CallFrequency;
import com.kaju.helo.groups.ContactGroup;
import com.kaju.helo.groups.PrefsDBHelper;

public class ContactScore {
	
	private final String mLookupKey;	

	private ContactGroup mContactGroup;
	
	private String mDisplayName;
	
	private String mContactId;
	
	private double mScore;
	
	private Date mLastContacted;
	
	private String mPrimaryPhone;
	
	private String mThumbnail;
	
	
	public ContactScore(String lookupKey) {
		mLookupKey = lookupKey;		
	}
	
	public void populate(Context ctx) {
		fetchDetails(ctx);
		if (mLastContacted.getTime() == 0) {
			mScore = java.lang.Double.MAX_VALUE;
		} else {
			computeScore();
		}
	}
	
	private void fetchDetails(Context ctx) {
		PrefsDBHelper db = new PrefsDBHelper(ctx);
		
		mContactGroup = db.findGroup(mLookupKey);
		
		Uri contactUri = getLookupUri();
		
		ContentResolver contentResolver = ctx.getContentResolver();
		
		Cursor c = null;
		try {
			c = contentResolver.query(contactUri, null, null, null, null);
			if (c.moveToFirst()) {
				int displayNameColIndex = c.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY);
				int lastContactedColIndex = c.getColumnIndex(Contacts.LAST_TIME_CONTACTED);
				int photoThumbnailColIndex = c.getColumnIndex(Contacts.PHOTO_THUMBNAIL_URI);
				int idColIndex = c.getColumnIndex(BaseColumns._ID);
				
				mDisplayName = c.getString(displayNameColIndex);
				mContactId = c.getString(idColIndex);
				mLastContacted = new Date(c.getLong(lastContactedColIndex));
				mThumbnail = c.getString(photoThumbnailColIndex);
			}
			
			String queryString = Data.CONTACT_ID + "=?" + " AND "
	                  + Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "'";
	                  
			c = contentResolver.query(Data.CONTENT_URI,
					new String[] {BaseColumns._ID, Phone.NUMBER, Phone.IS_PRIMARY},
					queryString,
			          new String[] {String.valueOf(mContactId)}, null);
			
			while (c.moveToNext()) {
				int phoneNumberColIndex = c.getColumnIndex(Phone.NUMBER);
				int isPrimaryColIndex = c.getColumnIndex(Phone.IS_PRIMARY);
				mPrimaryPhone = c.getString(phoneNumberColIndex);
				int isPrimary = c.getInt(isPrimaryColIndex);
				if (isPrimary != 0) break;
			}
			
		} finally {
			if (c != null)
				c.close();
		}
	}
	
	private void computeScore() {
		Date now = new Date();		 
		long diffMs = now.getTime() - mLastContacted.getTime();
		
		if (mContactGroup != null) {
			double freqMs = 0;
			CallFrequency freq = mContactGroup.getCallFrequency();
			switch (freq.getUnits()) {
			case CallFrequency.DAYS:
				freqMs = freq.getValue() * 86400000L;
				break;
			case CallFrequency.WEEKS:
				freqMs = freq.getValue() * 604800000L;
				break;
			case CallFrequency.MONTHS:
				freqMs = freq.getValue() * 2592000000L;
				break;
			}
			mScore = diffMs / freqMs;
		}
	}
	
	public double getScore() {
		return mScore;
	}
	
	public Date getLastContacted() {
		return mLastContacted;
	}
	
	public String getDisplayName() {
		return mDisplayName;
	}
	
	public Uri getLookupUri() {
		return Uri.withAppendedPath(Contacts.CONTENT_LOOKUP_URI, mLookupKey);
	}
	
	public String getPhoneNumber() {
		return mPrimaryPhone;
	}
	
	public String getPhotoThumbnail() {
		return mThumbnail;
	}
	
	public String getTargetLabel() {
		if (mContactGroup != null) {
			return mContactGroup.getLabel();
		} else {
			return null;
		}
	}
}