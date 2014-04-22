package com.kaju.helo;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaju.helo.calendar.ContactEvent;

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
		
		switch (getCount()) {
		case 1:
			rowView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
															ViewGroup.LayoutParams.MATCH_PARENT));
			break;
		default:
			int gridWidth = getPx(160);
			rowView.setLayoutParams(new LinearLayout.LayoutParams(gridWidth,
					ViewGroup.LayoutParams.MATCH_PARENT));
		}
		
		ContactEvent contactEvent = this.mContacts.get(position);
		
		String displayName = contactEvent.getDisplayName();
		TextView textView = (TextView) rowView.findViewById(R.id.contactNameTextView);
		textView.setText(displayName);
		
		ImageView imgView = (ImageView) rowView.findViewById(R.id.contactPictureImageView);
		String pictureUriString = contactEvent.getPhoto();
		if (pictureUriString != null) {			
			imgView.setImageURI(Uri.parse(pictureUriString));
		}
		imgView.setTag(R.id.contact_phone_number, contactEvent.getPhoneNumber());
		imgView.setOnClickListener(this.mDialBtnClickListener);	
		
		return rowView;
	}
	
	public int getPx(int dimensionDp) {
	    float density = mContext.getResources().getDisplayMetrics().density;
	    return (int) (dimensionDp * density + 0.5f);
	}	
}
