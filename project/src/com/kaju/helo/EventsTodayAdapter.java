package com.kaju.helo;

import java.util.List;

import com.kaju.helo.calendar.ContactEvent;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.QuickContactBadge;
import android.widget.TextView;

public class EventsTodayAdapter extends ArrayAdapter<ContactEvent> {

	private final Context mContext;
	private final int mRowLayoutResource;
	private final List<ContactEvent> mContacts;
	private View.OnClickListener mDialBtnClickListener;

	public EventsTodayAdapter(Context context, int resource,
			List<ContactEvent> values) {
		super(context, resource, values);
	
		mContext = context;
		mRowLayoutResource = resource;
		mContacts = values; 
	}
	
	public void setDialButtonClickHandler(final View.OnClickListener onClickListener) {
		this.mDialBtnClickListener = onClickListener;
	}	
		
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(mRowLayoutResource, parent, false);	
		
		ContactEvent contactEvent = this.mContacts.get(position);
		
		// Contact photo thumbnail
		QuickContactBadge contactBadge = (QuickContactBadge)rowView.findViewById(R.id.contactImageView);
		Uri contactUri = contactEvent.getLookupUri();
		contactBadge.assignContactUri(contactUri);
		String thumbnail = contactEvent.getPhotoThumbnail();
		if (thumbnail != null) {
			Uri thumbnailUri = Uri.parse(thumbnail);
			contactBadge.setImageURI(thumbnailUri);
		}
		
		// Contact name
		TextView nameLabel = (TextView)rowView.findViewById(R.id.contactNameTextView);
		String displayName = contactEvent.getDisplayName();
		nameLabel.setText(displayName);		
		
		// Dial button
		ImageButton dialBtn = (ImageButton) rowView.findViewById(R.id.contactDialBtn);
		dialBtn.setTag(R.id.contact_phone_number, contactEvent.getPhoneNumber());
		dialBtn.setOnClickListener(this.mDialBtnClickListener);		
		
		return rowView;
	}
}
