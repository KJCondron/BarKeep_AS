package com.kjcondron.barkeep;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BarScrollFragment extends Fragment {
	
	String mText;
	
	// as per android warning use default constructors plus setters!
	public void setText( String text ) { mText = text; }
	
	public View onCreateView(LayoutInflater infalter, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) infalter.inflate(R.layout.layout_bar_scroll, container, false);
		
		TextView tv = (TextView) rootView.findViewById(R.id.text_view_bar_name);
		tv.setText(mText);
		
		return rootView;
	}

}
