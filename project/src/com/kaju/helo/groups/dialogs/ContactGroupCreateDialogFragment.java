package com.kaju.helo.groups.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kaju.helo.R;

public class ContactGroupCreateDialogFragment extends
		ContactGroupInfoDialogFragment {
    
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {       
        
        View dialogView = super.buildDialogView();
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        builder.setView(dialogView)
        .setTitle(R.string.create_group_dialog_title)
        // Add action buttons
        .setPositiveButton(R.string.button_create_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            	if (mListener != null) {
		        	// Send the positive button event back to the host activity
		            mListener.onDialogPositiveClick(ContactGroupCreateDialogFragment.this);
            	}
            }
        })
        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	if (mListener != null) {
		        	// Send the negative button event back to the host activity
		            mListener.onDialogNegativeClick(ContactGroupCreateDialogFragment.this);
            	}
            }
        });      
 
        return builder.create();
    }
	
	@Override
	public void onStart() {
		super.onStart();
		Button okBtn = getButton(AlertDialog.BUTTON_POSITIVE);
		if (okBtn != null) {
			okBtn.setEnabled(false);
		}
	}
		
}
