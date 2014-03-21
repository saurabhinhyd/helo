package com.kaju.helo.prefs;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kaju.helo.R;

class ContactGroupsSpinnerAdapter extends ArrayAdapter<ContactGroup> {
	private final static int rowLayoutResource = android.R.layout.simple_spinner_dropdown_item;
	
	private final Context context;
	private final List<ContactGroup> values;

	public ContactGroupsSpinnerAdapter(Context context, List<ContactGroup> contactGroups) {
		super(context, rowLayoutResource, contactGroups);
		this.context = context;
		this.values = contactGroups;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(rowLayoutResource, parent, false);
		TextView labelView = (TextView) rowView.findViewById(android.R.id.text1);
		String label;
		if (position == getCount() - 1) {
			label = this.context.getResources().getString(R.string.create_group);
		} else {
			ContactGroup contactGroup = values.get(position);		
			label = contactGroup.getLabel();			
		}
		
		labelView.setText(label);
		return rowView;
	}
	
	@Override
	public View getDropDownView (int position, View convertView, ViewGroup parent) {
		return getView(position, convertView, parent);
	}
	
	@Override
	public int getCount() {
		return 1 + super.getCount();
	}
}
