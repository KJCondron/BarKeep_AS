package com.kjcondron.barkeep;

import java.text.DecimalFormat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	
	public final static String APP = "BARKEEP";
	
	private class BarScrollAdapter extends FragmentStatePagerAdapter {
		
		private String[] mBars;
		private int[] mIds;
		private int mCount;
		
		public BarScrollAdapter(FragmentManager fm, Cursor bars) {
			super(fm);
			int pos = 0;
			mCount = bars.getCount() + 1;
			mBars = new String[mCount];
			mIds = new int[mCount];
			bars.moveToFirst();
			do
			{
				mIds[pos] = bars.getInt(0);
				mBars[pos++] = bars.getString(1);
			}
			while(bars.moveToNext());
			
			mBars[pos] = "New Bar";
		}
		
		@Override
		public Fragment getItem(int pos) {
			if(pos > mCount) MainActivity.log_message(MainActivity.this, "pos greater than count", "BarScrollAdapter.getItem");
			BarScrollFragment bsf=  new BarScrollFragment();
			bsf.setText(mBars[pos]);
			return bsf;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mCount;
		}
		
		public int getId(int pos)
		{
			return (pos == mCount-1) ? -1 : mIds[pos]; 
		}
		
	}
	
	public final static String NEWBAR = "com.kjcondron.barkeep.NEWBAR"; 
	
	public static int BARID;	
	boolean mMakeNewBar;
	
	private ViewPager mPager;
	private BarScrollAdapter mPagerAdapter;
		
	/* Checks if external storage is available for read and write */
	public static boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	public static void logHeap(Class clazz) {
		
		final String app_package = "com.kjcondron.barkeep.";
	    Double allocated = new Double(Debug.getNativeHeapAllocatedSize())/new Double((1048576));
	    Double available = new Double(Debug.getNativeHeapSize())/1048576.0;
	    Double free = new Double(Debug.getNativeHeapFreeSize())/1048576.0;
	    DecimalFormat df = new DecimalFormat();
	    df.setMaximumFractionDigits(2);
	    df.setMinimumFractionDigits(2);

	    Log.d(APP + " MEM", "debug. =================================");
	    Log.d(APP + " MEM", "debug.heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free) in [" + clazz.getName().replaceAll(app_package,"") + "]");
	    Log.d(APP + " MEM", "debug.memory: allocated: " + df.format(new Double(Runtime.getRuntime().totalMemory()/1048576)) + "MB of " + df.format(new Double(Runtime.getRuntime().maxMemory()/1048576))+ "MB (" + df.format(new Double(Runtime.getRuntime().freeMemory()/1048576)) +"MB free)");
	    System.gc();
	    System.gc();

	    // don't need to add the following lines, it's just an app specific handling in my app        
	    //if (allocated>=(new Double(Runtime.getRuntime().maxMemory())/new Double((1048576))-MEMORY_BUFFER_LIMIT_FOR_RESTART)) {
	      //  android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	public static void log_exception(Context c, Exception e, String fromWhere)
	{
		log_message(c, e.getMessage(), fromWhere);
		/*// to-do add logging code
		if(isExternalStorageWritable())
		{
			
		}
		else
		{
			
			try	
			{
				File file = new File(App.context.getFilesDir(), "err_log");
				FileOutputStream stream = new FileOutputStream(file);
				stream.write(fromWhere.getBytes());
				stream.write(e.getMessage().getBytes());	
				stream.close();
			}
			catch(Exception o){}
				
		}*/
	}
	
	public static void log_message(Context c, String s, String fromWhere)
	{
		if( s != null ){
			Log.e("barkeep", s);
		if( c!=null )
			Toast.makeText(c, s, Toast.LENGTH_LONG).show();
		}
	}
	
	public static void log_info(Context c, String s, String fromWhere)
	{
		if( s != null ){
			Log.i("barkeep", s);
		if( c!=null )
			Toast.makeText(c, s, Toast.LENGTH_LONG).show();
		}
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final DBHelper db = new DBHelper(this);
        setContentView(R.layout.layout_splash);
        boolean mMakeNewBar = getIntent().getBooleanExtra(NEWBAR, false);
        
        if( !mMakeNewBar && db.haveBar() )
        {
        	try
        	{
        		final Cursor barCursor = db.getBars();
				mPager = (ViewPager) findViewById(R.id.pager);
				mPagerAdapter = new BarScrollAdapter(getSupportFragmentManager(), barCursor);
				mPager.setAdapter(mPagerAdapter);  	
				BARID = mPagerAdapter.getId(0); // start of with item 0 shown
				mPager.setOnPageChangeListener(new OnPageChangeListener() {
					
					@Override
					public void onPageSelected(int arg0) {
						BARID = mPagerAdapter.getId(arg0);
					}
					
					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {

					}
					
					@Override
					public void onPageScrollStateChanged(int arg0) {
					}
				});
			}
        	catch(Exception e)
        	{
        		BARID=1;
        		startMyActvity(UseActivity.class);
            	finish();
            	return;
        	}
        }
        else
        	makeNewBar();
    	
    }
    
    public void makeNewBar()
    {
    	setContentView(R.layout.layout_splash);
    	final DBHelper db = new DBHelper(this);
    	final EditText edtBarName = (EditText)findViewById(R.id.edtBarName);
    	edtBarName.setVisibility(View.VISIBLE);
    	edtBarName.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE)
				{
					BARID = db.newBar(v.getText().toString());
					startMyActvity(UseActivity.class);
					startMyActvity(AddActivity.class);
					finish();
					edtBarName.setVisibility(View.GONE);
					return true;
				}
				return false;
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    private void startMyActvity(Class<?> cl) {
    	Intent intent = new Intent(this, cl);
    	startActivity(intent);
    }
    
    public void startAdd(View view) {
    	startMyActvity(AddActivity.class);
    }
    
    public void startUse(View view) {
    	if(mMakeNewBar || BARID == -1)
    		makeNewBar();
    	else
    		startMyActvity(FullScreenPagerActivity.class);
    		//startMyActvity(UseActivity.class);
    }
    
    public void startShop(View view) {
    	startMyActvity(ShopActivity.class);
    }
    	
}
