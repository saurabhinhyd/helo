package com.kaju.helo.calendar;

import java.util.List;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.Contacts;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.kaju.helo.R;
import com.kaju.helo.SortedContactList;
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

	private PrefsDBHelper mDBHelper;
	
	private CalendarEventAdapter mAdapter;
	
	private SortedContactList mSortedLookupKeys;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);				
		
		mDBHelper = new PrefsDBHelper(this);
		
		mSortedLookupKeys = new SortedContactList();
		
		setContentView(R.layout.activity_layout_calendar);
		
		mAdapter = new CalendarEventAdapter(this, R.layout.row_layout_calendar_event, 
													mSortedLookupKeys, Event.TYPE_BIRTHDAY);

		mAdapter.setRemoveButtonClickHandler(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ImageButton removeBtn = (ImageButton)v;
				int position = (Integer) removeBtn.getTag(R.id.list_row_position);
				String lookupKey = mSortedLookupKeys.removeSorted(position);
				mDBHelper.removeContactEvents(lookupKey);
				mAdapter.notifyDataSetChanged();
			}
		});		
		
		setListAdapter(mAdapter);
	}	
	
	@Override
	protected void onStart() {
	    super.onStart();	    
	    
	    mSortedLookupKeys.clearSorted();
	    
	    List<String> lookupKeys = mDBHelper.getAllContactEvents();
	    
	    for (String lookupKey: lookupKeys) {
	    	String displayName = getDisplayName(lookupKey);
	    	mSortedLookupKeys.insertSorted(lookupKey, displayName);
	    }	    
	    
	    mAdapter.notifyDataSetChanged();
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
    	String lookupKey = getLookupKeyFromUri(contactUri);
	    if (lookupKey != null) {
	    	mDBHelper.addContactEvents(lookupKey);

	    	String displayName = getDisplayName(lookupKey);
	    	mSortedLookupKeys.insertSorted(lookupKey, displayName);
	    	
	    	mAdapter.notifyDataSetChanged();
	    }
    }
    
    private String getDisplayName(String lookupKey) {
    	
    	String displayName = null;    	
		Cursor c = null;
		
		ContentResolver contentResolver = getContentResolver();
		try {
			Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_LOOKUP_URI, lookupKey);
			c = contentResolver.query(contactUri, null, null, null, null);
			if (c.moveToFirst()) {
				int displayNameColIndex = c.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY);
				displayName = c.getString(displayNameColIndex);				
			}
		} finally {
			if (c != null)
				c.close();
		}

		return displayName;    	
    }    
    
    private String getLookupKeyFromUri(Uri contactUri) {
    	String lookupKey = null;    	
    	Cursor c = getContentResolver().query(contactUri, null, null, null, null);      	 
	    if (c.moveToNext()) {		    	 
	    	int lookupKeyColumn = c.getColumnIndex(Contacts.LOOKUP_KEY);
	    	lookupKey = c.getString(lookupKeyColumn);
	    }
	    c.close();
	    
	    return lookupKey;
    }
}
