package com.kaju.helo;

import java.util.ArrayList;
import java.util.Collections;

public class SortedContactList extends ArrayList<String> {
	
	private ArrayList<String> mContactNames;
	
	public SortedContactList() {
		mContactNames = new ArrayList<String>();
	}
	
	public void insertSorted(String lookupKey, String displayName) {
		if (!this.contains(lookupKey)) {
			this.add(lookupKey);
			mContactNames.add(displayName);		
			
	        for (int i = size()-1; i > 0 && displayName.compareToIgnoreCase(mContactNames.get(i-1)) < 0; i--) {
	            Collections.swap(this, i, i-1);
	            Collections.swap(mContactNames, i, i-1);
	        }
		}
	}
	
	public String removeSorted(int position) {
		mContactNames.remove(position);
		return this.remove(position);
	}

	public void clearSorted() {
		mContactNames.clear();
		this.clear();
	}
}
