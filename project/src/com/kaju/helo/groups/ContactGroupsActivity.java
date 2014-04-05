package com.kaju.helo.groups;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.util.LongSparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.kaju.helo.R;
import com.kaju.helo.groups.dialogs.ContactGroupCreateDialogFragment;
import com.kaju.helo.groups.dialogs.ContactGroupDeleteDialogFragment;
import com.kaju.helo.groups.dialogs.ContactGroupEditDialogFragment;
import com.kaju.helo.groups.dialogs.ContactGroupInfoDialogFragment;
import com.kaju.helo.groups.dialogs.ContactGroupInfoDialogFragment.ContactGroupInfoDialogListener;

public class ContactGroupsActivity extends Activity 
									implements ActionBar.OnNavigationListener									
{
	
	static final int PICK_CONTACT_REQUEST = 0;

	List<ContactGroup> mGroupList;
	
	LongSparseArray<ContactGroupFragment> mFragmentInstanceMap;
	
	ContactGroupsSpinnerAdapter mSpinnerAdapter;
	
	static final String STATE_NAV_INDEX = "nav_index";
	
	int mPrevNavIndex;
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    // Save the user's current game state
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			int index = actionBar.getSelectedNavigationIndex();
			savedInstanceState.putInt(STATE_NAV_INDEX, index);
		}		

		// Always call the superclass so it can save the view hierarchy state
	    super.onSaveInstanceState(savedInstanceState);
	}	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			PrefsDBHelper prefs = new PrefsDBHelper(this);
			
			mGroupList = prefs.getAllContactGroups();			
			
			mFragmentInstanceMap = new LongSparseArray<ContactGroupFragment>();		
			
			mSpinnerAdapter = new ContactGroupsSpinnerAdapter(this, mGroupList);
		
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setDisplayShowHomeEnabled(false);
//			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);		
			actionBar.setListNavigationCallbacks(mSpinnerAdapter, this);
			
			mPrevNavIndex = 0;
			
			// Check whether we're recreating a previously destroyed instance
		    if (savedInstanceState != null) {
		        // Restore value of members from saved state
		        int index = savedInstanceState.getInt(STATE_NAV_INDEX);
		        actionBar.setSelectedNavigationItem(index);
		        
		        if (index < mGroupList.size()) {
		        	mPrevNavIndex = index;
		        }
		    } 			
		}
    }


	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		if (itemPosition < mGroupList.size()) {			
			ContactGroup contactGroup = mGroupList.get(itemPosition);		
			showContactGroupFragment(contactGroup);
		    mPrevNavIndex = itemPosition;
		} else {
			showCreateGroupDialog();
		}
	    return true;
	}
		
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actions_contact_groups, menu);
        return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
        case R.id.action_add_contact:
        	actionPickContact();
        	return true;
        case R.id.action_edit_group:
        	actionEditGroup();
        	return true;
        case R.id.action_delete_group:
        	actionDeleteGroup();
        	return true;
        default:
        	return super.onOptionsItemSelected(item);
        }
    }    
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
            	Uri contactUri = data.getData(); 
            	addContact(contactUri);
            }
        }
    }
    
    protected void actionPickContact() {
    	Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
    	startActivityForResult(intent, PICK_CONTACT_REQUEST);    
    }    
    
    protected void actionEditGroup() {
    	showEditGroupDialog();
    }
    
    protected void actionDeleteGroup() {
    	showDeleteGroupDialog();    	
    }
    
	ContactGroupInfoDialogListener mCreateGroupListener = new ContactGroupInfoDialogListener() {
		
		@Override
		public void onDialogPositiveClick(ContactGroupInfoDialogFragment dialog) {
			String groupLabel = dialog.getGroupLabel().trim();
			CallFrequency freq = new CallFrequency(dialog.getCallFrequencyValue(), dialog.getCallFrequencyUnits());		
			
			PrefsDBHelper prefs = new PrefsDBHelper(ContactGroupsActivity.this);
			try {
				ContactGroup newGroup = prefs.addContactGroup(groupLabel, freq); 
				mSpinnerAdapter.add(newGroup);
				
				int index = mGroupList.size() - 1;
				getActionBar().setSelectedNavigationItem(index);
				onNavigationItemSelected(index, 0);
				
			} catch (Exception e) {				
				Toast.makeText(ContactGroupsActivity.this, R.string.create_group_failed, Toast.LENGTH_LONG).show();
				getActionBar().setSelectedNavigationItem(mPrevNavIndex);
			}
			
		}
		
		@Override
		public void onDialogNegativeClick(ContactGroupInfoDialogFragment dialog) {
			getActionBar().setSelectedNavigationItem(mPrevNavIndex);			
		}
	};	 
	
	ContactGroupInfoDialogListener mEditGroupListener = new ContactGroupInfoDialogListener() {
		
		@Override
		public void onDialogPositiveClick(ContactGroupInfoDialogFragment dialog) {
			String groupLabel = dialog.getGroupLabel();
			CallFrequency freq = new CallFrequency(dialog.getCallFrequencyValue(), dialog.getCallFrequencyUnits());			
			
			ContactGroup currentGroup = getCurrentContactGroup();
			currentGroup.setLabel(groupLabel);
			currentGroup.setCallFrequency(freq);
			
			PrefsDBHelper prefs = new PrefsDBHelper(ContactGroupsActivity.this);
			prefs.updateContactGroup(currentGroup);
			
			mSpinnerAdapter.notifyDataSetChanged();
		}
		
		@Override
		public void onDialogNegativeClick(ContactGroupInfoDialogFragment dialog) {
			// do nothing			
		}
	};
	
	ContactGroupDeleteDialogFragment.DialogListener mDeleteGroupListener = new ContactGroupDeleteDialogFragment.DialogListener() {

		@Override
		public void onDialogPositiveClick(
				ContactGroupDeleteDialogFragment dialog) {

			ContactGroup currentGroup = getCurrentContactGroup();
	    	
			PrefsDBHelper prefs = new PrefsDBHelper(ContactGroupsActivity.this);
	    	if (prefs.deleteContactGroup(currentGroup) == 1) {    		
    			mSpinnerAdapter.remove(currentGroup);
    			
    	    	int index = getActionBar().getSelectedNavigationIndex();	    	
    	    	if (index == mGroupList.size()) { // if index -> "New group..."
    	    		index--;
    	    		getActionBar().setSelectedNavigationItem(index);
    	    	}
    	    	
    	    	onNavigationItemSelected(index, 0);		
    			
    		} else {
    			System.out.println("Failed to delete group");
    		}    		
		}

		@Override
		public void onDialogNegativeClick(
				ContactGroupDeleteDialogFragment dialog) {
			// do nothing			
		}		
	};
    
    private void addContact(Uri contactUri) {
    	Cursor c = getContentResolver().query(contactUri, null, null, null, null);
    	 
	    if (c.moveToNext()) {		    	 
    	int lookupKeyColumn = c.getColumnIndex(Contacts.LOOKUP_KEY);
	    	String lookupKey = c.getString(lookupKeyColumn);
	    	 
	    	ContactGroupFragment fragment = getCurrentFragment();
	    	if (fragment != null)
	    		fragment.addContact(lookupKey);
	    } 
	    c.close();
    }
    
    private void showContactGroupFragment (ContactGroup contactGroup) {
		long contactGroupId = contactGroup.getId();		
		ContactGroupFragment f = mFragmentInstanceMap.get(contactGroupId);
		if (f == null) { 
			f =  ContactGroupFragment.newInstance(contactGroupId);
			mFragmentInstanceMap.put(contactGroupId, f);
		}

		FragmentTransaction ft = getFragmentManager().beginTransaction();
	    ft.replace(android.R.id.content, f);
	    ft.commit();	    	
    }
    
    private void showCreateGroupDialog() {
	    FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("create_group_dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);
	    
	    ContactGroupCreateDialogFragment dialogFragment = new ContactGroupCreateDialogFragment();
	    dialogFragment.setClickListener(mCreateGroupListener);
		dialogFragment.show(ft, "create_group_dialog");	    	
    }
    
    private void showEditGroupDialog() {
	    FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("edit_group_dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);
	    
	    long currentGroupID = getCurrentFragment().getContactGroupId();
	    ContactGroupEditDialogFragment dialogFragment = ContactGroupEditDialogFragment.newInstance(currentGroupID);
	    dialogFragment.setClickListener(mEditGroupListener);
		dialogFragment.show(ft, "edit_group_dialog");    	
    }
    
    private void showDeleteGroupDialog() {
	    FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("delete_group_dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);
	    
	    ContactGroupDeleteDialogFragment dialogFragment = new ContactGroupDeleteDialogFragment();
	    dialogFragment.setClickListener(mDeleteGroupListener);
		dialogFragment.show(ft, "delete_group_dialog");	    	
    }
    
    private ContactGroup getCurrentContactGroup() {
    	ContactGroup currentGroup = null; 
    	int index = getActionBar().getSelectedNavigationIndex();
    	if (index < mGroupList.size()) {
    		currentGroup = mGroupList.get(index);
    	}
    	return currentGroup;
    }
    
    private long getCurrentContactGroupID() {
    	long groupID = -1;
    	ContactGroup currentGroup = getCurrentContactGroup();
    	if (currentGroup != null) {
    		groupID = currentGroup.getId();
    	}
    	return groupID;
    }
    
    private ContactGroupFragment getCurrentFragment() {
    	long currentGroupID = getCurrentContactGroupID();
    	if (currentGroupID != -1) {
    		return mFragmentInstanceMap.get(currentGroupID);
    	} else {
    		return null;
    	}
    }
}
