package com.kaju.helo;

import java.util.Comparator;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.Contacts;

import com.kaju.helo.groups.CallFrequency;
import com.kaju.helo.groups.ContactGroup;
import com.kaju.helo.groups.PrefsDBHelper;

public class ContactScore extends ContactInfo {
	
	private ContactGroup mContactGroup;	
	
	private double mScore;
	
	private Date mLastContacted;
	
	public static Comparator<ContactScore> ContactScoreDesc = new Comparator<ContactScore>() {
		
		@Override
		public int compare(ContactScore o1, ContactScore o2) {
			return Double.compare(o2.getScore(), o1.getScore());
		}			
	};
	
	public ContactScore(String lookupKey) {
		super(lookupKey);				
	}
	
	public ContactGroup getContactGroup() {
		return mContactGroup;
	}
	
	public double getScore() {
		return mScore;
	}
	
	public Date getLastContacted() {
		return mLastContacted;
	}	
	
	@Override
	public void populate(Context ctx) {
		super.populate(ctx);
		
		Cursor c = null;
		try {
			c = ctx.getContentResolver().query(getLookupUri(), null, null, null, null);
			if (c.moveToFirst()) {
				int lastContactedColIndex = c.getColumnIndex(Contacts.LAST_TIME_CONTACTED);				
				mLastContacted = new Date(c.getLong(lastContactedColIndex));
			}
			
		} finally {
			if (c != null)
				c.close();
		}
		
		PrefsDBHelper db = new PrefsDBHelper(ctx);		
		mContactGroup = db.findGroup(getLookupKey());
		
		computeScore();
	}
	
	private void computeScore() {
		if (mLastContacted.getTime() == 0) {
			mScore = java.lang.Double.MAX_VALUE;
		} else {		
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
	}
}