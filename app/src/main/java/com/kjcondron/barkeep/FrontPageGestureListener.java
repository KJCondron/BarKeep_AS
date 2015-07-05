/*package com.kjcondron.barkeep;

import android.content.Context;
import android.database.Cursor;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.TextSwitcher;
import android.widget.ViewAnimator;

public class FrontPageGestureListener extends SimpleOnGestureListener {
	
	private ViewAnimator mView;
	private Context mContext;
	private Cursor mBars;
	private boolean mNewBar;
	private TextSwitcher mts;
	
	public FrontPageGestureListener(
			Context ctxt,
			ViewAnimator view,
			Cursor bars,
			TextSwitcher ts)
	{
		mView = view;
		mContext = ctxt;
		mBars = bars;
		mts=ts;
		mNewBar = false;
	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY )
	{
		if(distanceX < 0)
		{
			mView.setInAnimation(mContext, android.R.anim.slide_in_left);
			mView.setOutAnimation(mContext, android.R.anim.slide_out_right);
		}
		else
		{
			mView.setInAnimation(mContext, android.R.anim.fade_in);
			mView.setOutAnimation(mContext, android.R.anim.fade_out);
		}
		
		if(mNewBar)
		{
			mts.setText( "New Bar" );
			mBars.moveToFirst();
			mNewBar = false;
			MainActivity.BARID=0; //TODO
		}
		else
		{
			mts.setText( mBars.getString(1) );
			MainActivity.BARID = mBars.getInt(0);
			if(!mBars.moveToNext())
				mNewBar = true;
		}
		
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		boolean val =  super.onDown(e);
		return true;
	}

}
*/