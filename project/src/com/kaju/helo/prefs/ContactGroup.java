package com.kaju.helo.prefs;

public class ContactGroup {

	// Group sql row id
	private long mId;
	
	// Group label
	private String mLabel;
	
	// Group call frequency
	private CallFrequency mCallFrequency;
	
	private ContactGroup() {
		
	}
	
	public static ContactGroup newInstance(long id) {
		ContactGroup newGroup = new ContactGroup();
		newGroup.mId = id;
		return newGroup;
	}
	
	public long getId() {
		return mId;
	}
	
	public String getLabel() {
		return mLabel;
	}
	
	public void setLabel(String label) {
		mLabel = label;
	}
	
	public CallFrequency getCallFrequency() {
		return mCallFrequency;
	}
	
	public void setCallFrequency(CallFrequency callFreq) {
		mCallFrequency = callFreq;
	}
}
