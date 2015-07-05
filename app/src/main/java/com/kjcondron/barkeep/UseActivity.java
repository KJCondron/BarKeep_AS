package com.kjcondron.barkeep;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

public class UseActivity extends DisplayActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);   
	}

	protected SimpleCursorAdapter getInvetory(DBHelper db, int itemLayoutID) throws Exception
	{
		String[] coulmnNames = new String[]{ "brand", "product_name", "size", "product_type" };
	    Cursor c = db.getInventory(MainActivity.BARID);
	    c.moveToFirst();
	    
	    InventoryAdapter invAdapter = new InventoryAdapter(
	    		this, 
	    		itemLayoutID,
	    		c, 
	    		coulmnNames,
	    		new int[] { R.id.textView1, R.id.textView2,R.id.textView3,R.id.itemImageView },
	    		CursorAdapter.NO_SELECTION,
	    		R.id.progressBar1);
	    
	   
	    return invAdapter;
	}
	
}
