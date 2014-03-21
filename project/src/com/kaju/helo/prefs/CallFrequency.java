package com.kaju.helo.prefs;

public class CallFrequency {
	
	public static final int DAYS = 0;
	public static final int WEEKS = 1;
	public static final int MONTHS = 2;
	
	private final int mValue;
	
	private final int mUnits;
	
	public CallFrequency(int value, int units) {
		mValue = value;
		mUnits = units;
	}
	
	public int getValue() {
		return mValue;
	}
	
	public int getUnits() {
		return mUnits;
	}
}
