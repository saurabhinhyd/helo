package com.kaju.helo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

//import com.kaju.helo.notify.TestNotifications;
import com.kaju.helo.prefs.ContactGroupsActivity;
import com.kaju.helo.prefs.PrefsDBHelper;

public class ContactReminderActivity extends ListActivity {

	
	class ContactScoreDesc implements Comparator<ContactScore> {
		@Override
		public int compare(ContactScore o1, ContactScore o2) {
			return Double.compare(o2.getScore(), o1.getScore());
		}			
	}
	
	ArrayList<ContactScore> mContactList;
	
	ContactScoreRowAdapter mAdapter; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_layout_contact_reminder);
		
		mContactList = new ArrayList<ContactScore>();
				
		mAdapter = new ContactScoreRowAdapter(this, mContactList);
		mAdapter.setDialButtonClickHandler(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ImageButton dialBtn = (ImageButton)v;
				String phoneNumber = (String) dialBtn.getTag(R.id.contact_phone_number);
				
		    	Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
		    	startActivity(intent);    				
			}
		});
		
		setListAdapter(mAdapter);
		
//		TestNotifications.fireNotification(this);
	}
	
	@Override
	protected void onStart() {
	    super.onStart(); 
	    
	    mContactList.clear();
	    
		PrefsDBHelper db = new PrefsDBHelper(this);
		for (String lookupKey : db.getAllContacts()) {
			ContactScore contactScore = new ContactScore(lookupKey);
			contactScore.populate(this);
			mContactList.add(contactScore);
		}
		
		Collections.sort(mContactList, new ContactScoreDesc());	    
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.actions_contact_reminder, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
        case R.id.action_settings:
        	startPrefActivity();
        	return true;
        default:
        	return super.onOptionsItemSelected(item);
        }
    }
    
    private void startPrefActivity() {
    	Intent intent = new Intent(this, ContactGroupsActivity.class);
    	try {
    		startActivity(intent);
    	} catch(Exception e) {
    		System.out.println(e);
    	}
    }
}
