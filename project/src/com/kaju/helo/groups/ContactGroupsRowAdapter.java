package com.kaju.helo.groups;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaju.helo.R;

public class ContactGroupsRowAdapter extends ArrayAdapter<String> {
	private final static int rowLayoutResource = R.layout.grid_item_contact;//R.layout.row_layout_contact_remove;
	
	private final Context context;
	private final List<String> values;
	private View.OnClickListener mRemoveBtnClickListener;
  
	public ContactGroupsRowAdapter(Context context, List<String> values) {
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

		Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_LOOKUP_URI, values.get(position));
		Cursor c = context.getContentResolver().query(contactUri, null, null, null, null);

		if (c.moveToNext()) {
			int displayNameColumn = c.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY);
			String displayName = c.getString(displayNameColumn);

			TextView textView = (TextView) rowView.findViewById(R.id.contactNameTextView);
			textView.setText(displayName);
			
			int pictureColumn = c.getColumnIndex(Contacts.PHOTO_URI);
			String pictureUriString = c.getString(pictureColumn);
			
			if (pictureUriString != null) {
				ImageView imgView = (ImageView) rowView.findViewById(R.id.contactPictureImageView);
				imgView.setImageURI(Uri.parse(pictureUriString));
			}
		}
		
		ImageButton removeBtn = (ImageButton) rowView.findViewById(R.id.contactRemoveBtn);
		removeBtn.setTag(R.id.list_row_position, position);
		removeBtn.setOnClickListener(this.mRemoveBtnClickListener);

		return rowView;
	}
}
