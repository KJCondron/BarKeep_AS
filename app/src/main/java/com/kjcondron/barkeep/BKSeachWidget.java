package com.kjcondron.barkeep;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;

public class BKSeachWidget extends SearchView {
	
	private static final String TAG = BKSeachWidget.class.getSimpleName();

	public BKSeachWidget(Context context){
		this(context,null);
	}
	
	public BKSeachWidget(Context context, AttributeSet attrs){
		super(context, attrs);
	
		
		LinearLayout ll = (LinearLayout) getChildAt(0);	

		Button adv = new Button(context, attrs);
		adv.setText("adv");
		ll.addView(adv);
		
		final Context c = context;
		adv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(c, AdVSrchActivity.class);
		    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    	c.startActivity(intent);
		    	
			}
		});
	}
}
