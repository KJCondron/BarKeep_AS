package com.kjcondron.barkeep;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ShopActivity extends Activity {

	private Boolean gridView = false;
	private AlertDialog.Builder mbuilder;
	private int mItemLayoutID = R.layout.layout_inv_item_for_list;
	
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
		super.onResume();
		setupView(gridView);
	}
	
	protected void setupView(Boolean gv)
	{
		AbsListView v = gv ? 
				getLGView(R.layout.layout_inventory_grid_view, R.id.gridview, R.layout.layout_inv_item_for_grid) :
				getLGView(R.layout.layout_inventory_list_view, R.id.listview, R.layout.layout_inv_item_for_list); 
		try
		{
			SimpleCursorAdapter invAdapter = getShoppingList();
			v.setAdapter(invAdapter);
		}
		catch(Exception e)
		{
			MainActivity.log_exception(this, e, "ShopActivity.setupView");
		}
	}
	
	
	protected AbsListView getLGView(int layoutId, int viewID, int itemLayoutID)
	{
		setContentView(layoutId);
		mItemLayoutID = itemLayoutID;
		final AbsListView view =  (AbsListView) findViewById(viewID);
		try{
		final Context c = this;
		view.setOnItemClickListener(new OnItemClickListener() {
	        
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				try
				{
		            ListView lv = (ListView)parent;
		            SimpleCursorAdapter Cu = (SimpleCursorAdapter)lv.getAdapter();
		            final Integer iid = Cu.getCursor().getInt(Cu.getCursor().getColumnIndex("_id"));
		            final Integer pid = mdb.getProdIdFromShoppingId(iid);
		            	DialogInterface.OnClickListener dcl = new DialogInterface.OnClickListener() {
	
							@Override
							public void onClick(DialogInterface dialog, int which) {
								switch(which){
									case DialogInterface.BUTTON_POSITIVE:
										mdb.writeInventory(MainActivity.BARID, pid, 1.0);
										mdb.removeFromShopping(iid);
										finish();
										break;
									case DialogInterface.BUTTON_NEUTRAL:
										mdb.removeFromShopping(iid);
										finish();
										break;
									case DialogInterface.BUTTON_NEGATIVE:
										setupView(gridView);
										break;
										
								}
							}
						};
						
						mbuilder.setMessage("Bought?").
							setPositiveButton("Yes", dcl).
								setNeutralButton("No", dcl).
									setNegativeButton("Cancel", dcl).
									setCancelable(true).
										show();
	                
				}
				catch(Exception e1)
				{
					MainActivity.log_exception(c, e1, "onItemClick");
				}
			}
		});}
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
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected SimpleCursorAdapter getShoppingList() throws Exception
	{
		String[] coulmnNames = new String[]{ "brand", "product_name", "size" };
	    Cursor c = mdb.getShoppingList(MainActivity.BARID);
	    c.moveToFirst();
	    
	    SimpleCursorAdapter shopAdapter = new SimpleCursorAdapter(
	    		this, 
	    		mItemLayoutID,
	    		c, 
	    		coulmnNames,
	    		new int[] { R.id.textView1, R.id.textView2,R.id.textView3 },
	    		CursorAdapter.NO_SELECTION);
	    
	    return shopAdapter;
	}
	

}
