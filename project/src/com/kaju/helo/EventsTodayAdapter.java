package com.kaju.helo;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
		mDialBtnClickListener = null;
	}
	
	public void setDialButtonClickHandler(final View.OnClickListener onClickListener) {
		this.mDialBtnClickListener = onClickListener;
	}	
			
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(mRowLayoutResource, parent, false);	
		
		int gridWidth = ViewGroup.LayoutParams.MATCH_PARENT;
		int gridHeight = ViewGroup.LayoutParams.MATCH_PARENT;
		
		if (getCount() > 1) {
			gridWidth = getPx(160);
		}
		
		rowView.setLayoutParams(new LinearLayout.LayoutParams(gridWidth, gridHeight));
		
		ContactEvent contactEvent = this.mContacts.get(position);
		
		String eventLabel = getEventLabel(contactEvent);
		TextView eventTypeTextView = (TextView) rowView.findViewById(R.id.eventNameTextView);
		eventTypeTextView.setText(eventLabel);
		
		String displayName = contactEvent.getContact().getDisplayName();
		TextView textView = (TextView) rowView.findViewById(R.id.contactNameTextView);
		textView.setText(displayName);
		
		ImageView imgView = (ImageView) rowView.findViewById(R.id.contactPictureImageView);
		String pictureUriString = contactEvent.getContact().getPhoto();
		if (pictureUriString != null) {			
			imgView.setImageURI(Uri.parse(pictureUriString));
		}
		imgView.setTag(R.id.contact_phone_number, contactEvent.getContact().getPhoneNumber());
		imgView.setOnClickListener(this.mDialBtnClickListener);	
		
		return rowView;
	}
	
	public int getPx(int dimensionDp) {
	    float density = mContext.getResources().getDisplayMetrics().density;
	    return (int) (dimensionDp * density + 0.5f);
	}	
	
	public String getEventLabel(ContactEvent event) {
		String eventLabel = "";
		switch (event.getEventType()) {
		case Event.TYPE_BIRTHDAY:
			eventLabel = mContext.getResources().getString(R.string.event_type_birthday);
			break;
		case Event.TYPE_ANNIVERSARY:
			eventLabel = mContext.getResources().getString(R.string.event_type_anniversary);
			break;
		case Event.TYPE_OTHER:
			eventLabel = mContext.getResources().getString(R.string.event_type_other);
			break;
		case Event.TYPE_CUSTOM:
			eventLabel = event.getEventLabel();
			break;
		}
		return eventLabel;
	}
}
