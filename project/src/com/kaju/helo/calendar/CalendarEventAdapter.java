package com.kaju.helo.calendar;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.kaju.helo.ContactInfo;
import com.kaju.helo.R;

public class CalendarEventAdapter extends ArrayAdapter<ContactInfo> {

	private final Context mContext;
	private final int mRowLayoutResource;
	private final List<ContactInfo> mContacts;
	
	private View.OnClickListener mRemoveBtnClickListener;
	
	public CalendarEventAdapter(Context context, int resource,
			List<ContactInfo> values) {
		super(context, resource, values);
	
		mContext = context;
		mRowLayoutResource = resource;
		mContacts = values; 
	}

	public void setRemoveButtonClickHandler(final View.OnClickListener onClickListener) {
		this.mRemoveBtnClickListener = onClickListener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(mRowLayoutResource, parent, false);	
		
		ContactInfo contact = this.mContacts.get(position);
		
		// Contact photo thumbnail
		QuickContactBadge contactBadge = (QuickContactBadge)rowView.findViewById(R.id.contactImageView);
		Uri contactUri = contact.getLookupUri();
		contactBadge.assignContactUri(contactUri);
		String thumbnail = contact.getPhotoThumbnail();
		if (thumbnail != null) {
			Uri thumbnailUri = Uri.parse(thumbnail);
			contactBadge.setImageURI(thumbnailUri);
		}
		
		// Contact name
		TextView nameLabel = (TextView)rowView.findViewById(R.id.contactNameTextView);
		String displayName = contact.getDisplayName();
		nameLabel.setText(displayName);		
	
		ImageButton removeBtn = (ImageButton) rowView.findViewById(R.id.contactRemoveBtn);
		removeBtn.setTag(R.id.list_row_position, position);
		removeBtn.setOnClickListener(this.mRemoveBtnClickListener);
		
		return rowView;
	}	

}
