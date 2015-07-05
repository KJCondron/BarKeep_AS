package com.kjcondron.barkeep;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

public class FullScreenPagerActivity extends FragmentActivity {
	
	private Boolean mGridView = false;
	private AlertDialog.Builder mbuilder; 
	private int mItemLayoutID = R.layout.layout_inv_item_for_list;
	private MenuItem mSearchMenuItem;
	private DBHelper mdb;
	ViewPager mPager; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_fullscreen_pager);
		setupActionBar();
		// Show the Up button in the action bar.
		mdb = new DBHelper(this);
		mbuilder = new AlertDialog.Builder(this); 
		
		setupActionBar();
		
		mPager = (ViewPager) findViewById(R.id.full_screen_pager);
		final TypesScrollAdapter pagerAdapter = new TypesScrollAdapter(getSupportFragmentManager());
		mPager.setAdapter(pagerAdapter);
		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				ActionBar bar = getActionBar();
				bar.setTitle(pagerAdapter.getPageTitle(arg0));
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		ActionBar bar = getActionBar();
		bar.setTitle("Inventory");
		bar.setDisplayHomeAsUpEnabled(true);

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
			mGridView = !mGridView;
			item.setTitle(mGridView ? "List View" : "Grid View");
			mPager.getAdapter().notifyDataSetChanged();
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

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.full_screen_scroll, menu);
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
		}
		return super.onOptionsItemSelected(item);
	}*/
	
	private class TypesScrollAdapter extends FragmentStatePagerAdapter {
		
		private int mCount; 
		
		public TypesScrollAdapter(FragmentManager fm) {
			super(fm);
			try{
				Cursor c = mdb.getTypes();
				// 1 more fragment than types for the 'all types' front page
				mCount = c.getCount() + 1;
			}
			catch(Exception e){
				mCount = 0;
			}
		}
		
		@Override
		public Fragment getItem(int pos) {
			if(pos > mCount) MainActivity.log_message(FullScreenPagerActivity.this, "pos greater than count", "TypesScrollAdapter.getItem");
			
			DisplayFragment frag = new UseFragment();
			Bundle bdl = new Bundle();
			bdl.putBoolean(DisplayFragment.VIEW_TYPE, mGridView);
			if(0 != pos)
			{
				try
				{
					bdl.putString(UseFragment.ITEM_TYPE, getPageTitle(pos));
								
				}
				catch(Exception e)
				{
				}
			}
			frag.setArguments(bdl);
			return frag;
		}
		
		@Override
		public String getPageTitle(int pos) {
			String title = "Inventory";
			if(0 != pos)
			{
				try
				{
					Cursor c = mdb.getTypes();
					c.move(pos-1);
					title =  c.getString(c.getColumnIndex("product_type")) ;			
				}
				catch(Exception e)
				{
				}
			}
			
			return title;
		}
		
		@Override
		public int getItemPosition(Object item){
			return POSITION_NONE;
		}
	
		@Override
		public int getCount() {
			return mCount;
		}
	}
}
