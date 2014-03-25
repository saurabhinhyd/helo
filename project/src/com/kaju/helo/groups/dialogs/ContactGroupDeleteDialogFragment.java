package com.kaju.helo.groups.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.kaju.helo.R;

public class ContactGroupDeleteDialogFragment 
						extends DialogFragment {
	
    public interface DialogListener {
        public void onDialogPositiveClick(ContactGroupDeleteDialogFragment dialog);
        public void onDialogNegativeClick(ContactGroupDeleteDialogFragment dialog);
    }	

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.delete_group_dialog_title)
        		.setMessage(R.string.delete_group_dialog_message)
        		.setPositiveButton(R.string.button_delete_label, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                   	if (mListener != null) {
    		        	// Send the positive button event back to the host activity
    		            mListener.onDialogPositiveClick(ContactGroupDeleteDialogFragment.this);
                	}
                   }
               })
               .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                      	if (mListener != null) {
        		        	// Send the positive button event back to the host activity
        		            mListener.onDialogNegativeClick(ContactGroupDeleteDialogFragment.this);
                    	}
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
	
    // Use this instance of the interface to deliver action events
	DialogListener mListener;
    
    public void setClickListener(DialogListener listener) {
    	mListener = listener;
    }		
}
