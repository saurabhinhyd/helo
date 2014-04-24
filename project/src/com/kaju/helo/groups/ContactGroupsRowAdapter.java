package com.kaju.helo.groups;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaju.helo.ContactInfo;
import com.kaju.helo.R;

public class ContactGroupsRowAdapter extends ArrayAdapter<ContactInfo> {
	private final static int rowLayoutResource = R.layout.grid_item_contact;//R.layout.row_layout_contact_remove;
	
	private final Context context;
	private final List<ContactInfo> values;
	private View.OnClickListener mRemoveBtnClickListener;
  
	public ContactGroupsRowAdapter(Context context, List<ContactInfo> values) {
		super(context, rowLayoutResource, values);
	    this.context = context;
	    this.values = values;		
	}
	
	public void setRemoveButtonClickHandler(final View.OnClickListener onClickListener) {
		this.mRemoveBtnClickListener = onClickListener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(rowLayoutResource, parent, false);
		
		ContactInfo contact = values.get(position);		

		String displayName = contact.getDisplayName();
		TextView textView = (TextView) rowView.findViewById(R.id.contactNameTextView);
		textView.setText(displayName);
		
		String pictureUriString = contact.getPhoto();
		if (pictureUriString != null) {
			ImageView imgView = (ImageView) rowView.findViewById(R.id.contactPictureImageView);
			imgView.setImageURI(Uri.parse(pictureUriString));
		}

		ImageButton removeBtn = (ImageButton) rowView.findViewById(R.id.contactRemoveBtn);
		removeBtn.setTag(R.id.list_row_position, position);
		removeBtn.setOnClickListener(this.mRemoveBtnClickListener);

		return rowView;
	}
}
