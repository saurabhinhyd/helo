package com.kaju.helo.groups;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.kaju.helo.ContactInfo;
import com.kaju.helo.R;

public class ContactGroupFragment extends Fragment { 	
	
	private List<ContactInfo> mContactList;
	
	private ContactGroupsRowAdapter mAdapter;
	
	private GridView mGrid;
	
	private PrefsDBHelper mPrefs;
	
	private static String ARG = "contactGroupId";

	public long getContactGroupId() {
		return getArguments().getLong(ARG);
	}
	
	public static ContactGroupFragment newInstance(long contactGroupId) {
		ContactGroupFragment f = new ContactGroupFragment();
		
		Bundle args = new Bundle();
		args.putLong(ARG, contactGroupId);
		f.setArguments(args);
		
		return f;
	}
	
	
	public void addContact(String lookupKey) {
		ContactInfo contact = new ContactInfo(lookupKey);
		long groupId = getContactGroupId();
		
		boolean bAdded = false, bUpdated = false;
		
		if (mPrefs.addContact(lookupKey, groupId) == 1) { // try adding user to group
			bAdded = true;
		}
		
		if (!bAdded && mPrefs.updateContact(lookupKey, groupId) == 1) {
			bUpdated = true;
		}
		
		if (bAdded || bUpdated) {
			contact.populate(getActivity());
			mAdapter.add(contact);
			mAdapter.sort(ContactInfo.CompareName);			
		} else {
			Toast.makeText(getActivity(), R.string.add_contact_failed, Toast.LENGTH_SHORT).show();
		}		
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
		if (mContactList == null) {
			mContactList = new ArrayList<ContactInfo>();
		} else { 
			mContactList.clear();
		}        
        
		mPrefs = new PrefsDBHelper(getActivity());
		
		for (String lookupKey : mPrefs.getContacts(getContactGroupId())) {
			ContactInfo contact = new ContactInfo(lookupKey);		
			contact.populate(getActivity());
			mContactList.add(contact);
		}		
                
        mAdapter = new ContactGroupsRowAdapter(getActivity(), mContactList);
        mAdapter.sort(ContactInfo.CompareName);
        
        mAdapter.setRemoveButtonClickHandler(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ImageButton removeBtn = (ImageButton)v;
				int position = (Integer) removeBtn.getTag(R.id.list_row_position);
				ContactInfo removeContact = mAdapter.getItem(position);
				mAdapter.remove(removeContact);				
				mPrefs.removeContact(removeContact.getLookupKey());				
			}
		});
        
        mGrid.setAdapter(mAdapter);        
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_layout_contact_group, container, false);
        
        mGrid = (GridView)rootView.findViewById(R.id.gridview);
        mGrid.setEmptyView(rootView.findViewById(R.id.empty_grid_view));
        
        return rootView;
    }	
}
