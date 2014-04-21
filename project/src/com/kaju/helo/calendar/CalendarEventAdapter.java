package com.kaju.helo.calendar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.kaju.helo.R;

public class CalendarEventAdapter extends ArrayAdapter<ContactEvent> {

	private final Context mContext;
	private final int mRowLayoutResource;
	private final List<ContactEvent> mContacts;
	
	private View.OnClickListener mRemoveBtnClickListener;
	
	public CalendarEventAdapter(Context context, int resource,
			List<ContactEvent> values) {
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
		
		// Event start date
		TextView eventDateTextView = (TextView) rowView.findViewById(R.id.eventDateTextView);
		Date eventStartDate = contactEvent.getEventDate();
		if (eventStartDate != null)
			eventDateTextView.setText(formatEventDate(eventStartDate));
	
		ImageButton removeBtn = (ImageButton) rowView.findViewById(R.id.contactRemoveBtn);
		removeBtn.setTag(R.id.list_row_position, position);
		removeBtn.setOnClickListener(this.mRemoveBtnClickListener);
		
		return rowView;
	}	
    
    private static String formatEventDate(Date eventDate) {
    	SimpleDateFormat sdf = new SimpleDateFormat("MMMM d", Locale.getDefault());
    	return sdf.format(eventDate);
    }	
}
