package com.kjcondron.barkeep;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

public class UseFragment extends DisplayFragment {
	
	final static String ITEM_TYPE = "ITEM_TYPE";
	
	private String mType;
	private Boolean bAll;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    bAll = true;
	    Bundle bdl = super.getArguments();
	    if(null != bdl && bdl.containsKey(ITEM_TYPE)) {
	    	mType = bdl.getString(ITEM_TYPE);
	    	bAll=false;
	    }
	}

	protected SimpleCursorAdapter getInvetory(DBHelper db, int itemLayoutID) throws Exception
	{
		String[] columnNames = new String[]{ "brand", "product_name", "size", "product_type" };
		Cursor c;
		if(bAll)
			c = db.getInventory(MainActivity.BARID);
		else
			c = db.getInventory(MainActivity.BARID, mType);
	    c.moveToFirst();
	    
	    InventoryAdapter invAdapter = new InventoryAdapter(
	    		this.getActivity(), 
	    		itemLayoutID,
	    		c, 
	    		columnNames,
	    		new int[] { R.id.textView1, R.id.textView2,R.id.textView3,R.id.itemImageView },
	    		CursorAdapter.NO_SELECTION,
	    		R.id.progressBar1);
	    
	   
	    return invAdapter;
	}
	
}
