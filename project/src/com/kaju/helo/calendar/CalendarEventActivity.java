package com.kaju.helo.calendar;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.kaju.helo.ContactInfo;
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

	private PrefsDBHelper mDBHelper;
	
	private CalendarEventAdapter mAdapter;
	
	private ArrayList<ContactInfo> mContacts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);				
		
		mDBHelper = new PrefsDBHelper(this);
		
		mContacts = new ArrayList<ContactInfo>();
		
		setContentView(R.layout.activity_layout_calendar);
		
		mAdapter = new CalendarEventAdapter(this, R.layout.row_layout_calendar_event, mContacts);

		mAdapter.setRemoveButtonClickHandler(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ImageButton removeBtn = (ImageButton)v;
				int position = (Integer) removeBtn.getTag(R.id.list_row_position);
				ContactInfo removeContact = mAdapter.getItem(position);
				mDBHelper.removeContactFromEvents(removeContact.getLookupKey());
				mAdapter.remove(removeContact);
			}
		});		
		
		setListAdapter(mAdapter);
	}	
	
	@Override
	protected void onStart() {
	    super.onStart();	    
	    
	    mAdapter.clear();	    
	    	    
	    for (String lookupKey : mDBHelper.getAllContactsFromEvents()) {
	    	ContactInfo contact = new ContactInfo(lookupKey);
	    	contact.populate(this);	    	 
	    	mAdapter.add(contact);
	    }  
	    mAdapter.sort(ContactInfo.CompareName);

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
	    	ContactInfo contact = new ContactInfo(lookupKey);	    	
	    	contact.populate(this);
	    	
	    	mDBHelper.addContactToEvents(lookupKey);
	    	mAdapter.add(contact);
	    	mAdapter.sort(ContactInfo.CompareName);
	    }
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
