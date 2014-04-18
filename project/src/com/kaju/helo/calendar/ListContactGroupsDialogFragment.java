package com.kaju.helo.calendar;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;

import com.kaju.helo.R;
import com.kaju.helo.groups.ContactGroup;
import com.kaju.helo.groups.PrefsDBHelper;

public class ListContactGroupsDialogFragment extends DialogFragment
											implements OnMultiChoiceClickListener {

	private boolean[] mSelectedGroups;
	private List<ContactGroup> mGroupList;
	
    public interface ListContactGroupsDialogListener {
        public void onDialogPositiveClick(ListContactGroupsDialogFragment dialog);
        public void onDialogNegativeClick(ListContactGroupsDialogFragment dialog);
    }
    
    // Use this instance of the interface to deliver action events
    ListContactGroupsDialogListener mListener;
    
    public void setClickListener(ListContactGroupsDialogListener listener) {
    	mListener = listener;
    }	
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {	
				
		PrefsDBHelper db = new PrefsDBHelper(getActivity());
		mGroupList = db.getAllContactGroups();
		
		String[] groupLabels = new String[mGroupList.size()];
		mSelectedGroups = new boolean[mGroupList.size()];
		
		for (int i = 0; i < mGroupList.size(); i++) {
			groupLabels[i] = mGroupList.get(i).getLabel();
			mSelectedGroups[i] = true; // by default, select all
		}
		
		// Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        builder.setTitle(R.string.list_contact_groups_dialog_title);
        builder.setMultiChoiceItems(groupLabels, mSelectedGroups, this);
        
        builder.setPositiveButton(R.string.import_btn_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            	if (mListener != null) {
		        	// Send the positive button event back to the host activity
		            mListener.onDialogPositiveClick(ListContactGroupsDialogFragment.this);
            	}
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	if (mListener != null) {
		        	// Send the negative button event back to the host activity
		            mListener.onDialogNegativeClick(ListContactGroupsDialogFragment.this);
            	}
            }
        });
        
        // Create the AlertDialog object and return it
        return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		mSelectedGroups[which] = isChecked;		
	}
	
	public List<ContactGroup> getSelectedGroups() {
		List<ContactGroup> selectedGroups = new ArrayList<ContactGroup>();
		for (int i = 0; i < mGroupList.size(); i++) {
			if (mSelectedGroups[i]) {
				selectedGroups.add(mGroupList.get(i));
			}
		}
		return selectedGroups;
	}
}
