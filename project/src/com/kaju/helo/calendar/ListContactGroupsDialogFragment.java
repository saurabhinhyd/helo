package com.kaju.helo.calendar;

import java.util.List;

import com.kaju.helo.R;
import com.kaju.helo.groups.ContactGroup;
import com.kaju.helo.groups.PrefsDBHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class ListContactGroupsDialogFragment extends DialogFragment {

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {	
				
		PrefsDBHelper db = new PrefsDBHelper(getActivity());
		List<ContactGroup> listGroups = db.getAllContactGroups();
		
		String[] groupLabels = new String[listGroups.size()];
		boolean[] groupSelected = new boolean[listGroups.size()];
		
		for (int i = 0; i < listGroups.size(); i++) {
			groupLabels[i] = listGroups.get(i).getLabel();
			groupSelected[i] = true; // by default, select all
		}
		
		// Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        builder.setTitle(R.string.list_contact_groups_dialog_title);
        builder.setMultiChoiceItems(groupLabels, groupSelected, null);
        
        builder.setPositiveButton(R.string.import_btn_label, null);
        builder.setNegativeButton(android.R.string.cancel, null);
        
        // Create the AlertDialog object and return it
        return builder.create();
	}
}
