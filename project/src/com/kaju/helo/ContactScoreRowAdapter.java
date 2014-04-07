package com.kaju.helo;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

class ContactScoreRowAdapter extends ArrayAdapter<ContactScore> {
	private final static int rowLayoutResource = R.layout.row_layout_contact_dial;
	private final Context context;
	private final List<ContactScore> values;
	private View.OnClickListener mDialBtnClickListener;
	
	public ContactScoreRowAdapter(Context ctx, List<ContactScore> scores) {
		super(ctx, rowLayoutResource, scores);
		
		this.context = ctx;
		this.values = scores;
	}
	
	public void setDialButtonClickHandler(final View.OnClickListener onClickListener) {
		this.mDialBtnClickListener = onClickListener;
	}	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(rowLayoutResource, parent, false);	
		
		ContactScore contactScore = this.values.get(position);
		
		// Contact photo thumbnail
		QuickContactBadge contactBadge = (QuickContactBadge)rowView.findViewById(R.id.contactImageView);
		contactBadge.assignContactUri(contactScore.getLookupUri());
		if (contactScore.getPhotoThumbnail() != null) {
			Uri thumbnailUri = Uri.parse(contactScore.getPhotoThumbnail());
			contactBadge.setImageURI(thumbnailUri);
		}
		
		// Contact name
		TextView nameLabel = (TextView)rowView.findViewById(R.id.contactNameTextView);
		nameLabel.setText(contactScore.getDisplayName());
		
		// Target frequency
		TextView targetLabel = (TextView) rowView.findViewById(R.id.targetFreqTextView);
		targetLabel.setText("[" + contactScore.getTargetLabel() + "]");
		
		// Score Image
		ImageView scoreImage = (ImageView)rowView.findViewById(R.id.scoreImageView);
		double score = contactScore.getScore();
		scoreImage.setImageResource(getImageForScore(score));
		
		// Last contacted
		TextView lastContactLabel = (TextView)rowView.findViewById(R.id.lastContactedTextView);
		String lastContactedString = getFriendlyDateString(contactScore.getLastContacted());
		lastContactLabel.setText(lastContactedString);
		
		// Dial button
		ImageButton dialBtn = (ImageButton) rowView.findViewById(R.id.contactDialBtn);
		dialBtn.setTag(R.id.contact_phone_number, contactScore.getPhoneNumber());
		dialBtn.setOnClickListener(this.mDialBtnClickListener);		
		
		return rowView;
	}
	
	private String getFriendlyDateString(Date lastContacted) {
		String friendlyDate;

		if (lastContacted.getTime() == 0) {
			friendlyDate = this.context.getResources().getString(R.string.never);
		} else {	
			Calendar nowCalendar = Calendar.getInstance();
			Calendar thenCalendar = Calendar.getInstance();
			thenCalendar.setTime(lastContacted);

			int diffDays = 0;
			if (nowCalendar.get(Calendar.YEAR) == thenCalendar.get(Calendar.YEAR)) {
				diffDays = nowCalendar.get(Calendar.DAY_OF_YEAR) - thenCalendar.get(Calendar.DAY_OF_YEAR);
			} else {
				long diff = new Date().getTime() - lastContacted.getTime();
				diffDays = (int) Math.ceil((double) diff / 86400000L);
			}
			
			if (diffDays == 0) {
				friendlyDate = this.context.getResources().getString(R.string.today);
			} else if (diffDays == 1) {
				friendlyDate = this.context.getResources().getString(R.string.a_day_ago);
			} else {
				friendlyDate = Integer.toString(diffDays) + " " 
						+ this.context.getResources().getString(R.string.days_ago);			
			}
		}
		
		return friendlyDate;
	}
	
	private int getImageForScore(double score) {
		int imageResId;
		if (score > 2) {
			imageResId = R.drawable.ic_hourglass_empty;
		} else if (score >= 1) {
			imageResId = R.drawable.ic_hourglass_half;
		} else {
			imageResId = R.drawable.ic_hourglass_full;
		}
		return imageResId;
	}
}