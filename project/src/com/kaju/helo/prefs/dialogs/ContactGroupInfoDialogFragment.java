package com.kaju.helo.prefs.dialogs;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.kaju.helo.R;
import com.kaju.helo.prefs.CallFrequency;

public class ContactGroupInfoDialogFragment extends DialogFragment{
	protected EditText mEditTextGroupLabel;
	
	protected EditText mEditTextCallFreqValue;

	protected Spinner mSpinnerCallFreqUnits;
	
	protected AlertDialog mDialog;
	
	public String getGroupLabel() {
		return mEditTextGroupLabel.getText().toString();
	}	

	private boolean isValidCallFreq(Editable s) {
		return (s.length() > 0);			
	}
	
	private boolean isValidGroupName(Editable s) {
		return (s.length() > 0);	
	}
	
	public Integer getCallFrequencyValue() {
		return Integer.parseInt(mEditTextCallFreqValue.getText().toString());
	}

	public int getCallFrequencyUnits() {
		String units = (String)mSpinnerCallFreqUnits.getSelectedItem();
		Resources res = getActivity().getResources();
		if (res.getString(R.string.days).equals(units)) {
			return CallFrequency.DAYS;
		} else if (res.getString(R.string.weeks).equals(units)) {
			return CallFrequency.WEEKS;
		} else if (res.getString(R.string.months).equals(units)) {
			return CallFrequency.MONTHS;
		} else {
			return -1;
		}		
	}	
	
	protected View buildDialogView() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        // This fragment is a child of activity ContactGroupsActivity, which 
        // uses a custom theme AppBaseTheme.WhiteTextSpinner. For the spinner 
        // in this fragment we do NOT wish to use that theme. Hence, create 
        // ContextThemeWrapper from the original Activity Context with the base
        // Holo theme.
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Light_DarkActionBar);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        
        View dialogView = localInflater.inflate(R.layout.dialog_create_group, null);
        mEditTextGroupLabel = (EditText)dialogView.findViewById(R.id.editTextGroupLabel);
        mEditTextCallFreqValue = (EditText)dialogView.findViewById(R.id.editTextCallFrequencyValue);
        mSpinnerCallFreqUnits = (Spinner)dialogView.findViewById(R.id.spinnerCallFrequencyUnits);
	
        mEditTextGroupLabel.addTextChangedListener(mGroupLabelWatcher);
        mEditTextCallFreqValue.addTextChangedListener(mCallFreqValWatcher);
        
        return dialogView;
	}	
	
	protected Button getButton(int whichButton) {
		AlertDialog dialog = (AlertDialog)getDialog();
		if (dialog != null) 
			return dialog.getButton(whichButton);
		else
			return null;
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		if (mListener != null) {
			mListener.onDialogNegativeClick(ContactGroupInfoDialogFragment.this);
		}
	}
	
    public interface ContactGroupInfoDialogListener {
        public void onDialogPositiveClick(ContactGroupInfoDialogFragment dialog);
        public void onDialogNegativeClick(ContactGroupInfoDialogFragment dialog);
    }
    
    // Use this instance of the interface to deliver action events
    ContactGroupInfoDialogListener mListener;
    
    public void setClickListener(ContactGroupInfoDialogListener listener) {
    	mListener = listener;
    }
    
    TextWatcher mGroupLabelWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			Button positiveButton = getButton(AlertDialog.BUTTON_POSITIVE);			
			if (positiveButton != null) {
				positiveButton.setEnabled(isValidGroupName(s));
			}			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			
		}
    	
    };
    
    TextWatcher mCallFreqValWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			Button positiveButton = getButton(AlertDialog.BUTTON_POSITIVE);
			if (positiveButton != null)
				positiveButton.setEnabled(isValidCallFreq(s));
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			
		}
    	
    };
}
