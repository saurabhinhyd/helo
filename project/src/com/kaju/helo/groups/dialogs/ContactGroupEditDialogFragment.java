package com.kaju.helo.groups.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.kaju.helo.R;
import com.kaju.helo.groups.CallFrequency;
import com.kaju.helo.groups.ContactGroup;
import com.kaju.helo.groups.PrefsDBHelper;

public class ContactGroupEditDialogFragment extends
		ContactGroupInfoDialogFragment {
	
	private long mGroupID;
	
	private static final String ARG_GROUP_ID = "groupID";
	
    public static ContactGroupEditDialogFragment newInstance(long groupID) {
    	ContactGroupEditDialogFragment f = new ContactGroupEditDialogFragment();

        Bundle args = new Bundle();
        args.putLong(ARG_GROUP_ID, groupID);
        f.setArguments(args);

        return f;
    }	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGroupID = getArguments().getLong(ARG_GROUP_ID);
    }
    
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {       
        
        View dialogView = super.buildDialogView();
        
        //TODO - Use PrefsDBHelper to get group details and populate dialog edit fields
        PrefsDBHelper prefs = new PrefsDBHelper(getActivity());
        ContactGroup contactGroup = prefs.getContactGroup(mGroupID);
        if (contactGroup != null) {
        	mEditTextGroupLabel.setText(contactGroup.getLabel());
        	CallFrequency callFreq = contactGroup.getCallFrequency();
        	mEditTextCallFreqValue.setText(String.valueOf(callFreq.getValue()));
        	mSpinnerCallFreqUnits.setSelection(callFreq.getUnits());
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        builder.setView(dialogView)
        .setTitle(R.string.edit_group_dialog_title)
        // Add action buttons
        .setPositiveButton(R.string.button_update_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            	if (mListener != null) {
		        	// Send the positive button event back to the host activity
		            mListener.onDialogPositiveClick(ContactGroupEditDialogFragment.this);
            	}
            }
        })
        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	if (mListener != null) {
		        	// Send the negative button event back to the host activity
		            mListener.onDialogNegativeClick(ContactGroupEditDialogFragment.this);
            	}
            }
        });      
 
        return builder.create();
    }   	
}
