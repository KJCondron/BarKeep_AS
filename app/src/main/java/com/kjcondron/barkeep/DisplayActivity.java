package com.kjcondron.barkeep;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public abstract class DisplayActivity extends Activity {

	private Boolean gridView = false;
	private AlertDialog.Builder mbuilder; 
	MenuItem mSearchMenuItem;
	
	private DBHelper mdb; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mdb = new DBHelper(this);
		mbuilder = new AlertDialog.Builder(this); 

		// Show the Up button in the action bar.
		setupActionBar();
		setupView(gridView);
	}
	
	@Override
	protected void onResume()
	{
		//Toast.makeText(this, "resume", Toast.LENGTH_LONG).show();
		if(mSearchMenuItem != null)
			MenuItemCompat.collapseActionView(mSearchMenuItem);
		super.onResume();
		setupView(gridView);
	}
	
	protected void setupView(Boolean gv)
	{
		int layout = gv ? R.layout.layout_inventory_grid_view : R.layout.layout_inventory_list_view;
		int viewId = gv ? R.id.gridview : R.id.listview;
		int itemLayout = gv ? R.layout.layout_inv_item_for_grid : R.layout.layout_inv_item_for_list;
		
		AbsListView v = getLGView(layout, viewId, itemLayout); 
		
		try
		{
			v.setAdapter(getInvetory(mdb, itemLayout));
		}
		catch(Exception e)
		{
			MainActivity.log_exception(this, e, "UseActivity.setupView");
		}
	}
	
	
	protected AbsListView getLGView(int layoutId, int viewID, int itemLayoutID)
	{
		setContentView(layoutId);
		final AbsListView view = (AbsListView) findViewById(viewID);
		try{
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
	            setupView(gridView);
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
			MainActivity.log_exception(this, e, "ShopActicity.getLGView");
		}
	        
	    return view;
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.use, menu);
		mSearchMenuItem = menu.findItem(R.id.search);
		
		// Associate searchable configuration with the SearchView
	    SearchManager searchManager =
	           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView =
	            (SearchView) mSearchMenuItem.getActionView();
	    searchView.setSearchableInfo(
	            searchManager.getSearchableInfo(getComponentName()));

	    
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.menugridview:
			gridView = !gridView;
			setupView(gridView);
			item.setTitle(gridView ? "List View" : "Grid View");
			return true;
		case R.id.menuadd:
			Intent aIntent = new Intent(this, AddActivity.class);
	    	startActivity(aIntent);
			return true;
		case R.id.menushopping:
			Intent sIntent = new Intent(this, ShopActivity.class);
	    	startActivity(sIntent);
			return true;
		case R.id.savedb:
			(new DBHelper(this)).saveDB(this);
			Toast.makeText(this, "saved db", Toast.LENGTH_LONG).show();
			return true;
		case R.id.new_bar:
			Intent nintent = new Intent(this, MainActivity.class);
			nintent.putExtra(MainActivity.NEWBAR, true);
			startActivity(nintent);
			return true;
		case R.id.change_bar:
			Intent cintent = new Intent(this, MainActivity.class);
			startActivity(cintent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected abstract SimpleCursorAdapter getInvetory(DBHelper db, int itemLayoutID) throws Exception;
}
