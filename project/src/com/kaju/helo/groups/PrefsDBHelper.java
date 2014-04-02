package com.kaju.helo.groups;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.kaju.helo.R;

public class PrefsDBHelper extends SQLiteOpenHelper {	
	
	private final Context mCtx;
	
	// Database Version
    private static final int DATABASE_VERSION = 1;
    
    // Database Name
    private static final String DATABASE_NAME = "prefs";

    // Contact Groups table name
    private static final String TABLE_CONTACT_GROUPS = "contact_groups";
    
    // Contact Groups table column names
    private static final String COLUMN_CONTACT_GROUPS_ID = BaseColumns._ID;
    private static final String COLUMN_CONTACT_GROUPS_LABEL = "label";
    private static final String COLUMN_CONTACT_GROUPS_CALL_FREQ_VAL = "call_freq_val";
    private static final String COLUMN_CONTACT_GROUPS_CALL_FREQ_UNITS = "call_freq_units";
    
    // Contacts table name
    private static final String TABLE_CONTACTS = "contacts";
    
 // Contacts table column names
    private static final String COLUMN_CONTACT_LOOKUPKEY = "lookupKey";
    private static final String COLUMN_CONTACT_GROUP_ID = "groupId";
    
	public PrefsDBHelper(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		
		mCtx = ctx;
	}
	
	@Override
	public void onConfigure (SQLiteDatabase db) {
	    super.onConfigure(db);
	    if (!db.isReadOnly()) {
	        // Enable foreign key constraints
//	        db.execSQL("PRAGMA foreign_keys=ON;");
	        db.setForeignKeyConstraintsEnabled(true);
	    }
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACT_GROUPS_TABLE = "CREATE TABLE " + TABLE_CONTACT_GROUPS + "("
                + COLUMN_CONTACT_GROUPS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ COLUMN_CONTACT_GROUPS_LABEL + " TEXT UNIQUE,"
				+ COLUMN_CONTACT_GROUPS_CALL_FREQ_UNITS + " INTEGER,"
                + COLUMN_CONTACT_GROUPS_CALL_FREQ_VAL + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACT_GROUPS_TABLE);
        
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + COLUMN_CONTACT_LOOKUPKEY + " TEXT PRIMARY KEY,"				
                + COLUMN_CONTACT_GROUP_ID + " INTEGER,"
                + " FOREIGN KEY (" + COLUMN_CONTACT_GROUP_ID + ") REFERENCES " + TABLE_CONTACT_GROUPS + " ("+ COLUMN_CONTACT_GROUPS_ID +")  ON DELETE CASCADE)";
        db.execSQL(CREATE_CONTACTS_TABLE);
        
        String dailyStr = mCtx.getResources().getString(R.string.daily);
        String weeklyStr = mCtx.getResources().getString(R.string.weekly); 
        String monthlyStr = mCtx.getResources().getString(R.string.monthly);
        
        addContactGroup(dailyStr, new CallFrequency(1, CallFrequency.DAYS), db);
        addContactGroup(weeklyStr, new CallFrequency(1, CallFrequency.WEEKS), db);
        addContactGroup(monthlyStr, new CallFrequency(1, CallFrequency.MONTHS), db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT_GROUPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
 
        // Create tables again
        onCreate(db);
	}
	
	public List<String> getContacts(long groupId) {
		List<String> contacts = new ArrayList<String>();
		
		SQLiteDatabase db = this.getReadableDatabase();
	    Cursor cursor = db.query(TABLE_CONTACTS, 
	    		new String[] { COLUMN_CONTACT_LOOKUPKEY }, 
	    		COLUMN_CONTACT_GROUP_ID + "=?", new String[] { String.valueOf(groupId) }, 
	    		null, null, null, null);
	    
	    while (cursor.moveToNext()) {
	    	contacts.add(cursor.getString(0));
	    }
	    
	    db.close();
	    
		return contacts;
	}

	public List<String> getAllContacts() {
		List<String> contacts = new ArrayList<String>();
		
		SQLiteDatabase db = this.getReadableDatabase();
	    Cursor cursor = db.query(TABLE_CONTACTS, 
	    		new String[] { COLUMN_CONTACT_LOOKUPKEY }, 
	    		null, null, 
	    		null, null, null, null);
	    
	    while (cursor.moveToNext()) {
	    	contacts.add(cursor.getString(0));
	    }
	    
	    db.close();
	    
		return contacts;
	}
	

	public int addContact(String lookupKey, long groupId) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(COLUMN_CONTACT_LOOKUPKEY, lookupKey);
	    values.put(COLUMN_CONTACT_GROUP_ID, groupId);
	    
	    
	    long rowId = db.insert(TABLE_CONTACTS, null, values);
	    db.close();

	    int rowsAffected = (rowId == -1 ? 0 : 1);
	    return rowsAffected;
	}
	
	public int updateContact(String lookupKey, long groupId) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(COLUMN_CONTACT_LOOKUPKEY, lookupKey);
	    values.put(COLUMN_CONTACT_GROUP_ID, groupId);
	    
	    int rowsAffected = db.update(TABLE_CONTACTS, values, COLUMN_CONTACT_LOOKUPKEY + " = ?", 
    			new String[] { String.valueOf(lookupKey) });
	    
	    db.close();
	    
	    return rowsAffected;
	}
	
	public void removeContact(String lookupKey) {
		SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_CONTACTS, COLUMN_CONTACT_LOOKUPKEY + " = ?",
	            new String[] { String.valueOf(lookupKey) });
	    db.close();		
	}
	
	// Returns ContactGroup to which the input contact belongs to
	public ContactGroup findGroup(String lookupKey) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_CONTACTS, new String[] { COLUMN_CONTACT_GROUP_ID }, 
				COLUMN_CONTACT_LOOKUPKEY + "=?", new String[] { String.valueOf(lookupKey) }, 
	    		null, null, null, null);
		
		if (cursor.moveToFirst()) {
			int groupId = cursor.getInt(0);
			return getContactGroup(groupId);
		} else {
			return null;
		}
	}
	
	// Adding new contact group
	public ContactGroup addContactGroup(String label, CallFrequency frequency) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
		ContactGroup newGroup = addContactGroup(label, frequency, db);
		
	    db.close(); // Closing database connection
	    
	    return newGroup;
	}
	
	private ContactGroup addContactGroup(String label, CallFrequency frequency, SQLiteDatabase db) {
	    ContentValues values = new ContentValues();
	    values.put(COLUMN_CONTACT_GROUPS_LABEL, label); // Contact Group label
	    values.put(COLUMN_CONTACT_GROUPS_CALL_FREQ_VAL, frequency.getValue()); // Contact Group call frequency
	    values.put(COLUMN_CONTACT_GROUPS_CALL_FREQ_UNITS, frequency.getUnits()); // Contact Group call frequency
	 
	    ContactGroup contactGroup = null;
	    
	    // Inserting Row
	    long rowID = db.insertOrThrow(TABLE_CONTACT_GROUPS, null, values);	    
	    if (rowID != -1) {
	    	contactGroup = ContactGroup.newInstance(rowID);
	    	contactGroup.setLabel(label);
	    	contactGroup.setCallFrequency(frequency);
	    } 
	    
	    return contactGroup;
	}	
	
	private ContactGroup parseContactGroup(Cursor cursor) {
		 ContactGroup contactGroup = null;
		 
		 if (cursor != null) {
			 long groupID = Long.parseLong(cursor.getString(0));
			 contactGroup = ContactGroup.newInstance(groupID);
			 contactGroup.setLabel(cursor.getString(1));
	        
	         CallFrequency freq = new CallFrequency(cursor.getInt(2), cursor.getInt(3));	        
	         contactGroup.setCallFrequency(freq);
		 }   
	     
		 return contactGroup;
	}	 	
	 
	// Getting single contact group
	public ContactGroup getContactGroup(long id) {
		SQLiteDatabase db = this.getReadableDatabase();		 
		 
	    Cursor cursor = db.query(TABLE_CONTACT_GROUPS, 
	    		new String[] { COLUMN_CONTACT_GROUPS_ID, COLUMN_CONTACT_GROUPS_LABEL, 
	    		COLUMN_CONTACT_GROUPS_CALL_FREQ_VAL, COLUMN_CONTACT_GROUPS_CALL_FREQ_UNITS}, 
	    		COLUMN_CONTACT_GROUPS_ID + "=?", new String[] { String.valueOf(id) }, 
	    		null, null, null, null);
	    
	    cursor.moveToFirst();
	    return parseContactGroup(cursor);
	}

	// Getting All Contacts
	public List<ContactGroup> getAllContactGroups() {
		List<ContactGroup> contactGroupList = new ArrayList<ContactGroup>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_CONTACT_GROUPS;
	 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	ContactGroup contactGroup = parseContactGroup(cursor);
	        	if (contactGroup != null)
	        		contactGroupList.add(contactGroup);
	        } while (cursor.moveToNext());
	    }
	 
	    // return contact group list
	    return contactGroupList;
	}
	 
	// Getting contact groups Count
	public int getContactGroupsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_CONTACT_GROUPS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
	 
        // return count
        return cursor.getCount();
	}
	
	// Updating single contact group
	public int updateContactGroup(ContactGroup contactGroup) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(COLUMN_CONTACT_GROUPS_LABEL, contactGroup.getLabel());
	    
	    CallFrequency frequency = contactGroup.getCallFrequency();
	    values.put(COLUMN_CONTACT_GROUPS_CALL_FREQ_VAL, frequency.getValue());
	    values.put(COLUMN_CONTACT_GROUPS_CALL_FREQ_UNITS, frequency.getUnits());
	 
	    // updating row
	    return db.update(TABLE_CONTACT_GROUPS, values, COLUMN_CONTACT_GROUPS_ID + " = ?",
	            new String[] { String.valueOf(contactGroup.getId()) });
	}
	 
	// Deleting single contact
	public int deleteContactGroup(ContactGroup contact) {
		SQLiteDatabase db = this.getWritableDatabase();
		int rowsDeleted = db.delete(TABLE_CONTACT_GROUPS, COLUMN_CONTACT_GROUPS_ID + " = ?",
	            						new String[] { String.valueOf(contact.getId()) });	
		
	    db.close();
	    
	    return rowsDeleted;
	}
}
