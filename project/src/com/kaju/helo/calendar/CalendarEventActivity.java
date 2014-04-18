package com.kaju.helo.calendar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import com.kaju.helo.R;
import com.kaju.helo.groups.ContactGroup;
import com.kaju.helo.groups.PrefsDBHelper;

public class CalendarEventActivity extends ListActivity {
	
	static final int PICK_CONTACT_REQUEST = 0;
	
	private ListContactGroupsDialogFragment.ListContactGroupsDialogListener mListGroupsListener = 
			new ListContactGroupsDialogFragment.ListContactGroupsDialogListener() {

		@Override
		public void onDialogPositiveClick(ListContactGroupsDialogFragment dialog) {
			List<ContactGroup> selectedGroups = dialog.getSelectedGroups();
			for (ContactGroup group : selectedGroups) {
				doImportFromGroup(group);
			}
		}

		@Override
		public void onDialogNegativeClick(ListContactGroupsDialogFragment dialog) {
			// do nothing			
		}
		
	};

	
	private ExpandableListAdapter mAdapter;
	
	private ExpandableListView mExpListView;
	
	static final int[] monthNameIds = { R.string.month_jan,  R.string.month_feb, R.string.month_mar,
									R.string.month_apr, R.string.month_may, R.string.month_jun,
									R.string.month_july, R.string.month_aug, R.string.month_sep,
									R.string.month_oct, R.string.month_nov, R.string.month_dec
								};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);				
		
		// Expandable list - group data
		List<HashMap<String, String>> groupData = new ArrayList<HashMap<String, String>>(monthNameIds.length);
		for (int monthIndex = 0; monthIndex < monthNameIds.length; monthIndex++) {
			HashMap<String, String> monthData = new HashMap<String, String>();
			monthData.put("month_name", getResources().getString(monthNameIds[monthIndex]));
			groupData.add(monthData);
		}
		
		// Expandable list - child data
		String[] contacts = {   "a1", "a2", //jan
								"b1", "b2",	//feb			
								"c1", "c2", //mar
								"d1", "d2", //apr
								"e1", "e2", //may								
								"f1", "f2", //june
								"g1", "g2", //july
								"h1", "h2", //aug
								"i1", "i2", //sep
								"j1", "j2", //oct
								"k1", "k2", //nov
								"l1", "l2"  //dec
							};
		
		List<ArrayList<HashMap<String, String>>> childData = new ArrayList<ArrayList<HashMap<String, String>>>(monthNameIds.length);
		for (int monthIndex = 0; monthIndex < monthNameIds.length; monthIndex++) {
			HashMap<String, String> monthChild1 = new HashMap<String, String>();
			monthChild1.put("contact_name", contacts[2*monthIndex]);
			HashMap<String, String> monthChild2 = new HashMap<String, String>();
			monthChild2.put("contact_name", contacts[2*monthIndex + 1]);
			
			ArrayList<HashMap<String, String>> monthChildren = new ArrayList<HashMap<String, String>>(2);
			monthChildren.add(monthChild1);
			monthChildren.add(monthChild2);
			
			childData.add(monthChildren);
		}
		
		mAdapter = new SimpleExpandableListAdapter(this, 
				groupData, R.layout.calendar_group_item, new String[] {"month_name"}, new int[] {android.R.id.text1},
				childData, R.layout.calendar_child_item, new String[] {"contact_name"}, new int[] {android.R.id.text1});
		
		setContentView(R.layout.activity_layout_calendar);
		mExpListView = (ExpandableListView)findViewById(android.R.id.list);
		mExpListView.setAdapter(mAdapter);
	}	
	
	@Override
	protected void onStart() {
	    super.onStart();
	    
	    
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
	    dialogFragment.setClickListener(mListGroupsListener);
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
