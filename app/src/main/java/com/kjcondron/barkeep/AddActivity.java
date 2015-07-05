package com.kjcondron.barkeep;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class AddActivity extends Activity {
	
	public final static String PRODUCT_TYPE = "com.kjcondron.barkeep.PRODUCT_TYPE";
	public final static String PRODUCT_TYPEID = "com.kjcondron.barkeep.PRODUCT_TYPEID";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_add);
		setContentView(R.layout.layout_types_grid);
		// Show the Up button in the action bar.
		setupActionBar();
		setupGridView();
	}
	
	protected void setupGridView()
	{
		GridView gv = (GridView) findViewById(R.id.typesgridview);  
		gv.setOnItemClickListener(new OnItemClickListener() {  
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				GridView gv2 = (GridView)(parent);
				TypesAdapter adapter = (TypesAdapter) gv2.getAdapter();
				Cursor cc = adapter.getCursor();
				if(position == cc.getCount())
					startScan();
				else
				{
					long pid = cc.getInt(0);
					addProduct(pid);
				}
			}} );
	    
		try
		{
			SimpleCursorAdapter invAdapter = getTypes();
			gv.setAdapter(invAdapter);
		}
		catch(Exception e)
		{
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	protected SimpleCursorAdapter getTypes() throws Exception
	{
		DBHelper db = new DBHelper(this);
		Cursor c = db.getTypes();
	    c.moveToFirst();
	    
	    TypesAdapter invAdapter = new TypesAdapter(
	    		this, 
	    		R.layout.layout_types_item,
	    		c, 
	    		new String[]{"product_type", "image_path"},
	    		new int[] { R.id.typeText, R.id.typeImage },
	    		CursorAdapter.NO_SELECTION);
	    
	    return invAdapter;
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
		getMenuInflater().inflate(R.menu.add, menu);
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
		case R.id.menu_barcode:
			startScan();
			return true;
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void addProduct(String s) {
		Intent intent = new Intent(this, ProductDetailActivity.class);
	    intent.putExtra(PRODUCT_TYPE, s);
	    addProduct(intent);
	}
	
	private void addProduct(long id) {
		Intent intent = new Intent(this, ProductDetailActivity.class);
	    intent.putExtra(PRODUCT_TYPEID, id);
	    addProduct(intent);
	}
	
	private void addProduct(Intent intent) {
		startActivity(intent);
	    finish();
	}
		
	
	public void addWhisky(View view) { addProduct("Whisky"); }
	public void addRum(View view) { addProduct("Rum"); }
	public void addVodka(View view) { addProduct("Vodka"); }
	public void addGin(View view) { addProduct("Gin"); }
	public void addTequila(View view) { addProduct("Tequila"); }
	public void addOther(View view) { addProduct("Other"); }
	public void addLiqueur(View view) { addProduct("Liqueur"); }
	
	public void startScan(){
    	IntentIntegrator.initiateScan(this);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode) {
    		case IntentIntegrator.REQUEST_CODE: {
    			if (resultCode != RESULT_CANCELED) {
    				IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
    				if (scanResult != null) {
    					try
    					{
	    					String upc = scanResult.getContents();
	    	 
	    					Intent intent = new Intent(this, ProductDetailActivity.class);
	    					intent.putExtra(
									ProductDetailActivity.UPC,
									upc);
	    					
	    					DBHelper db = new DBHelper(this);
	    					if( !db.UPCExsits(upc) )
	    						intent.putExtra(ProductDetailActivity.ADD_TO_DB, true);	
	    					
	    					startActivity(intent);
	    					finish();
    					}
    					catch(Exception e)
    					{
    						MainActivity.log_exception(this, e, "MainActivity.onActivityResult");
    					}
    				}
    			}
    			break;
    		}
    	}
    }    


}
