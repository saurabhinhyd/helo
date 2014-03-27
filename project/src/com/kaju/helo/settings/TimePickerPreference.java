package com.kaju.helo.settings;

import java.text.DateFormat;
import java.util.Calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import com.kaju.helo.R;

public class TimePickerPreference extends DialogPreference {
	
	static final int DEFAULT_VALUE = 900;
	
	private TimePicker mPicker;
	
	private Integer mInitialValue;
	
	private Integer mCurrentValue;

	public TimePickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
        
		setDialogLayoutResource(R.layout.timepicker_dialog);
		
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        
        setDialogIcon(null);
	}
	
	@Override
    public void onBindDialogView(View view){
		super.onBindDialogView(view);
		
		mPicker = (TimePicker)view.findViewById(R.id.timepicker);
		
		setCurrentTime(mInitialValue);
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult) {
	    // When the user selects "OK", persist the new value
	    if (positiveResult) {
			int time = getCurrentTime();
			persistInt(time);		
			setSummary(getSummaryFromTime(time));
	    }
	}
	
	protected int getCurrentTime() {
		if (mPicker != null) {
			int hour = mPicker.getCurrentHour();
			int minute = mPicker.getCurrentMinute();
			mCurrentValue = hour*100 + minute;
		}
		return mCurrentValue;
	}
	
	protected void setCurrentTime(int time) {
		mCurrentValue = time;
		if (mPicker != null) {
		    int hour = time/100;
		    int minute = time % 100;
		    mPicker.setCurrentHour(hour);
		    mPicker.setCurrentMinute(minute);
		}
	}
	
	public static String getSummaryFromTime(Integer time) {
		int hourOfDay = time / 100;
		int minute = time % 100;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);		
		
		return DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
	}
	
	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {

	    if (restorePersistedValue) {
	        // Restore existing state
	    	mInitialValue = this.getPersistedInt(DEFAULT_VALUE);
	    } else {
	        // Set default state from the XML attribute
	    	mInitialValue = (Integer) defaultValue;
	        persistInt(mInitialValue);
	    }    
	    
	    setSummary(getSummaryFromTime(mInitialValue));

	}
	
	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
	    return a.getInteger(index, DEFAULT_VALUE);
	}
	
	private static class SavedState extends BaseSavedState {
	    // Member that holds the setting's value
	    // Change this data type to match the type saved by your Preference
	    int value;

	    public SavedState(Parcelable superState) {
	        super(superState);
	    }

	    public SavedState(Parcel source) {
	        super(source);
	        // Get the current preference's value
	        value = source.readInt();  // Change this to read the appropriate data type
	    }

	    @Override
	    public void writeToParcel(Parcel dest, int flags) {
	        super.writeToParcel(dest, flags);
	        // Write the preference's value
	        dest.writeInt(value);  // Change this to write the appropriate data type
	    }

	    // Standard creator object using an instance of this class
	    public static final Parcelable.Creator<SavedState> CREATOR =
	            new Parcelable.Creator<SavedState>() {

	        public SavedState createFromParcel(Parcel in) {
	            return new SavedState(in);
	        }

	        public SavedState[] newArray(int size) {
	            return new SavedState[size];
	        }
	    };
	}
	
	@Override
	protected Parcelable onSaveInstanceState() {
	    final Parcelable superState = super.onSaveInstanceState();
	    // Check whether this Preference is persistent (continually saved)
	    if (isPersistent()) {
	        // No need to save instance state since it's persistent, use superclass state
	        return superState;
	    }

	    // Create instance of custom BaseSavedState
	    final SavedState myState = new SavedState(superState);
	    // Set the state's value with the class member that holds current setting value
	    myState.value = getCurrentTime();
	    return myState;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
	    // Check whether we saved the state in onSaveInstanceState
	    if (state == null || !state.getClass().equals(SavedState.class)) {
	        // Didn't save the state, so call superclass
	        super.onRestoreInstanceState(state);
	        return;
	    }

	    // Cast state to custom BaseSavedState and pass to superclass
	    SavedState myState = (SavedState) state;
	    super.onRestoreInstanceState(myState.getSuperState());
	    
	    // Set this Preference's widget to reflect the restored state
	    setCurrentTime(myState.value);
	}	

}
