package com.kaju.helo;

import java.util.Comparator;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;

public class ContactInfo {
	private final String mLookupKey;	

	private String mDisplayName;
	
	private String mThumbnail;
	
	private String mPhotoUri;
	
	private String mPhoneNumber;
	
	public ContactInfo(String lookupKey) {
		mLookupKey = lookupKey;		
	}
	
	public String getLookupKey() {
		return mLookupKey;
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
				
			c.close();    				

		} finally {
			if (c != null)
				c.close();
		}

	}	

	public static Comparator<ContactInfo> CompareName = 
			new Comparator<ContactInfo>() {

		@Override
		public int compare(ContactInfo lhs, ContactInfo rhs) {
			return lhs.getDisplayName().compareToIgnoreCase(rhs.getDisplayName());					
		}

	};	
}
