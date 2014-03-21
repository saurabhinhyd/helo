package com.kaju.helo.prefs;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;

import com.kaju.helo.R;

public class ContactGroupFragment extends Fragment { //ListFragment {	
	
	private List<String> mContactLookupList;
	
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
		if (!mContactLookupList.contains(lookupKey)) {	
			long groupId = getContactGroupId();
			if (mPrefs.addContact(lookupKey, groupId) == 1) { // try adding user to group 
				mContactLookupList.add(lookupKey);
				mAdapter.notifyDataSetChanged();
			} else if (mPrefs.updateContact(lookupKey, groupId) == 1) { // maybe user is already part of some other group. in that case, do an update
				mContactLookupList.add(lookupKey);
				mAdapter.notifyDataSetChanged();			
			} else { // if both add and update failed, do nothing 
				System.out.println("Could not add contact to group");
			}
		}
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
		if (mContactLookupList == null) {
			mContactLookupList = new ArrayList<String>();
		} else { 
			mContactLookupList.clear();
		}        
        
		mPrefs = new PrefsDBHelper(getActivity());
		
		mContactLookupList = mPrefs.getContacts(getContactGroupId());
                
        mAdapter = new ContactGroupsRowAdapter(getActivity(), mContactLookupList);
        
        mAdapter.setRemoveButtonClickHandler(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ImageButton removeBtn = (ImageButton)v;
				int position = (Integer) removeBtn.getTag(R.id.list_row_position);
				String key = mContactLookupList.remove(position);
				mPrefs.removeContact(key);
				mAdapter.notifyDataSetChanged();
			}
		});
        
//        setListAdapter(mAdapter);
        
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
