package com.kjcondron.barkeep;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public abstract class DisplayFragment extends Fragment {
	
	final static String VIEW_TYPE = "VIEW_TYPE";
	private Boolean gridView = false;
	private DBHelper mdb; 
	private AlertDialog.Builder mbuilder; 
	
	private ViewPager pager;
	
	String mText;
	
	// as per android warning use default constructors plus setters!
	public void setText( String text ) { mText = text; }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mdb = new DBHelper(this.getActivity());
		mbuilder = new AlertDialog.Builder(this.getActivity());
		pager = (ViewPager)container;
		
		Bundle bdl = super.getArguments();
	    if(null != bdl)
	    	gridView = bdl.getBoolean(VIEW_TYPE, false);
		
		try
		{	
			View v = setupView(inflater, gridView); 
			return v;
		}
		catch(Exception e)
		{
			Toast.makeText(this.getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
			MainActivity.log_exception(this.getActivity(), e, "DisplayFragment.onCreateView");
			Toast.makeText(this.getActivity(), "exception", Toast.LENGTH_LONG).show();
		}
		
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.layout_bar_scroll, container, false);
		
		TextView tv = (TextView) rootView.findViewById(R.id.text_view_bar_name);
		tv.setText("bob");
		
		return rootView;
		
		//ViewGroup rootView = (ViewGroup) infalter.inflate(R.layout.layout_bar_scroll, container, false);
		
		//TextView tv = (TextView) rootView.findViewById(R.id.text_view_bar_name);
		//tv.setText(mText);
		
		//return rootView;
	}
	
	protected View setupView(LayoutInflater inflater, Boolean gv) throws Exception
	{
		int layout = gv ? R.layout.layout_inventory_grid_view : R.layout.layout_inventory_list_view;
		int viewId = gv ? R.id.gridview : R.id.listview;
		int itemLayout = gv ? R.layout.layout_inv_item_for_grid : R.layout.layout_inv_item_for_list;
		
		try
		{
			return getLGView(inflater, layout, viewId, itemLayout, getInvetory(mdb, itemLayout));
		}
		catch(Exception e)
		{
			MainActivity.log_exception(this.getActivity(), e, "DisplayFragment.setupView");
			throw e;
			//return new TextView(this.getActivity());
		}
	}
	
	protected View getLGView(
			final LayoutInflater inflater,
			int layoutId,
			int viewID,
			int itemLayoutID,
			SimpleCursorAdapter adapter)
	{
		ViewGroup rootView = (ViewGroup) inflater.inflate(layoutId, pager, false);
		final AbsListView view = (AbsListView) rootView.findViewById(viewID);
		view.setAdapter(adapter);
		try{
			final Context ctxt = getActivity(); 
			view.setOnItemClickListener(new OnItemClickListener() {
	        
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            SimpleCursorAdapter Cu = (SimpleCursorAdapter)parent.getAdapter();
	            Integer iid = Cu.getCursor().getInt(Cu.getCursor().getColumnIndex("_id"));
	            final int prodId = mdb.updateQuantity(iid);
	            if( prodId != -1 )
	            {
	            	DialogInterface.OnClickListener dcl = new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch(which){
								case DialogInterface.BUTTON_POSITIVE:
									mdb.addToShopping(prodId, MainActivity.BARID);
								case DialogInterface.BUTTON_NEGATIVE:
							}
						
						}
					};
					
					mbuilder.setMessage("Add To Shopping List?").setPositiveButton("Yes", dcl).setNegativeButton("No", dcl).show();
	            }
	            
	            int idx = parent.getFirstVisiblePosition();
	            View top = parent.getChildAt(0);
	            int offset = (top == null) ? 0 : v.getTop();
	            try
	            {
	            	setupView(inflater, gridView);
	            }
	            catch(Exception e)
				{
					MainActivity.log_exception(ctxt, e, "Failed to update view");
				}
				pager.getAdapter().notifyDataSetChanged();
	            
	            // restore position
	            if(!gridView)
	            {
	            	ListView lv2 = (ListView) parent;
	            	lv2.setSelectionFromTop(idx, offset);
	            }
	            
	        }
		});
		
		view.setOnTouchListener( new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {					
				return false;
			}
		});
		}
		catch(Exception e)
		{
			MainActivity.log_exception(this.getActivity(), e, "ShopActicity.getLGView");
		}
	        
	    return rootView;
	}
	
	protected abstract SimpleCursorAdapter getInvetory(DBHelper db, int itemLayoutID) throws Exception;

}
