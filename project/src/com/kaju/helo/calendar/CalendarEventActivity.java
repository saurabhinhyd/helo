package com.kaju.helo.calendar;

import java.util.List;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kaju.helo.R;
import com.kaju.helo.groups.ContactGroup;
import com.kaju.helo.groups.PrefsDBHelper;

public class CalendarEventActivity extends ListActivity {
	
	static final int PICK_CONTACT_REQUEST = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_layout_calendar);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actions_calendar, menu);
		return true;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.calendar_add_contact:
        	actionPickContact();
        	return true;
        case R.id.calendar_import_groups:
        	showListContactGroupsDialog();
        	return true;        
        default:
        	return super.onOptionsItemSelected(item);
        }        
    }
    
    public void importFromContactGroups(View view) {
    	showListContactGroupsDialog();
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
            	Uri contactUri = data.getData(); 
            	doImportFromContact(contactUri);
            }
        }
    }
    
    protected void actionPickContact() {
    	Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
    	startActivityForResult(intent, PICK_CONTACT_REQUEST);    
    }      
    
    private void showListContactGroupsDialog() {
	    FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("list_groups_dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);
	    
	    ListContactGroupsDialogFragment dialogFragment = new ListContactGroupsDialogFragment();	    
		dialogFragment.show(ft, "list_groups_dialog");      	
    }
    
    private void doImportFromGroup(ContactGroup group) {
    	PrefsDBHelper db = new PrefsDBHelper(this);
    	List<String> contactList = db.getContacts(group.getId());
    	
    	for (String contactLookupKey : contactList) {
    		Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_LOOKUP_URI, contactLookupKey);
    		doImportFromContact(contactUri);
    	}
    }
    
    private void doImportFromContact(Uri contactUri) {
    	String lookupKey = null;    	
    	Cursor c = getContentResolver().query(contactUri, null, null, null, null);      	 
	    if (c.moveToNext()) {		    	 
	    	int lookupKeyColumn = c.getColumnIndex(Contacts.LOOKUP_KEY);
	    	lookupKey = c.getString(lookupKeyColumn);
	    }
	    c.close();
    	
	    if (lookupKey != null) {
	    	String dateBirthday = retrieveEventStartDate(lookupKey, Event.TYPE_BIRTHDAY);
	    }
    }
    
    private String retrieveEventStartDate(String lookupKey, int eventType) {
       	Cursor c = getContentResolver().query(Data.CONTENT_URI,
    			new String[] {Event.START_DATE},
    			ContactsContract.Contacts.LOOKUP_KEY + "=?" + " AND " +
    			Data.MIMETYPE + "='" + Event.CONTENT_ITEM_TYPE + "'" + " AND " +
    			Event.TYPE + "=?" ,    	                  
    	        new String[] {lookupKey, String.valueOf(eventType)}, 
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
