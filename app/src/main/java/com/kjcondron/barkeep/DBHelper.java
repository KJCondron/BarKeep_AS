package com.kjcondron.barkeep;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.MessageFormat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.kjcondron.barkeep.SearchActivity.SrchTyp;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


public class DBHelper extends SQLiteAssetHelper implements ActivityCompat.OnRequestPermissionsResultCallback  {

	private static final String DATABASE_NAME = "barkeep";
	private static final int DATABASE_VERSION = 1;
	public static final String NOT_FOUND = "NOT_FOUND";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 7;
    private boolean SAVE_DB=false;
    private final Context m_context;

	public DBHelper(Context ctxt) {
		super(ctxt, DATABASE_NAME, null, DATABASE_VERSION);
		m_context = ctxt;

        //Toast.makeText(ctxt, m_context.getApplicationInfo().dataDir + "/databases", Toast.LENGTH_LONG).show();
	}

	public Cursor getTables()
	{
		SQLiteDatabase db = getReadableDatabase();

		String sql = "SELECT name FROM sqlite_master";
        Cursor c2 = db.rawQuery(sql, null);

        c2.moveToFirst();
        return c2;
	}

	private String makeOptional(String name)
	{
		return "select distinct -1 as _id, ' Other' as " + name + " union ";
	}

	public Cursor getBrands( String type ) throws Exception
	{
		return getBrands(type,false);
	}

	public Boolean haveBar()
	{
		SQLiteDatabase db = getReadableDatabase();

		String sql = "select * from Bars";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        return c.getCount() > 0;
	}

	public Cursor getTypes() throws Exception
	{
		try{
			SQLiteDatabase db = getReadableDatabase();

			String sql ="select * from Types";

	        Cursor c2 = db.rawQuery(sql, null);

	        c2.moveToFirst();
	        return c2;
		}
		catch(Exception e)
		{
			MainActivity.log_exception(m_context, e, "getTypes");
			throw e;
		}
	}

	public Cursor getBrands( String type, Boolean includeOther ) throws Exception
	{
		try{
			SQLiteDatabase db = getReadableDatabase();

			String optionSql = includeOther ? makeOptional("brand") : "";

	        String sql = optionSql + "select _id, brand from vProducts " +
	        		"where product_type=\"" + type +
	        		"\" group by brand";

	        Cursor c2 = db.rawQuery(sql, null);

	        c2.moveToFirst();
	        return c2;
		}
		catch(Exception e)
		{
			MainActivity.log_exception(m_context,e, "getBrands");
			throw e;
		}
	}

	public Cursor getAllBrands() throws Exception
	{
		try{
			SQLiteDatabase db = getReadableDatabase();

			// don't want any brands that match the product types, because we are going
			// to use this set to find in a page full of data including product types
	        String sql = "select _id, brand from vProducts where brand not in " +
	        		"(select 'Liquer' as product_type union " +
	        		"select 'Liquor' as product_type union " +
	        		"select product_type from types) group by brand";

	        Cursor c2 = db.rawQuery(sql, null);

	        c2.moveToFirst();
	        return c2;
		}
		catch(Exception e)
		{
			MainActivity.log_exception(m_context,e, "getAllBrands");
			throw e;
		}
	}

	public Cursor getBrandsFilter( String type, CharSequence constraint ) throws Exception
	{
		try{
			SQLiteDatabase db = getReadableDatabase();

			String sql = "select _id, brand from vProducts where product_type=\"" + type
					+ "\" and brand like \"" + constraint + "%\" group by brand";

	        Cursor c2 = db.rawQuery(sql, null);

	        c2.moveToFirst();
	        return c2;
		}
		catch(Exception e)
		{
			MainActivity.log_exception(m_context,e, "getBrands");
			throw e;
		}
	}

	public Cursor getProducts( String tableName, String brand) throws Exception
	{
		return getProducts(tableName, brand, false);
	}

	public Cursor getProducts( String type, String brand, Boolean includeOther  ) throws Exception
	{
		try{
			SQLiteDatabase db = getReadableDatabase();

			String optionSql = includeOther ? makeOptional("product_name") : "";

	        String sql = optionSql + "select _id, product_name from vProducts " +
	        		"where product_type=\"" + type +
	        		"\" and brand=\"" + brand + "\" group by product_name";

		    Cursor c = db.rawQuery(sql, null);

		    c.moveToFirst();
		    return c;
		}
		catch(Exception e)
		{
			MainActivity.log_exception(m_context,e, "getProducts");
			throw e;
		}
	}

	public Cursor getProductsFilter( String type, String brand, CharSequence constraint  ) throws Exception
	{
		try{
			SQLiteDatabase db = getReadableDatabase();

			String sql = "select _id, product_name from vProducts where product_type=\"" + type
					+ "\" and brand=\"" + brand
					+ "\" and product_name like \"" + constraint + "%\" group by product_name";

	        Cursor c = db.rawQuery(sql, null);

		    c.moveToFirst();
		    return c;
		}
		catch(Exception e)
		{
			MainActivity.log_exception(m_context,e, "getProductsFilter");
			throw e;
		}
	}

	public Cursor getSizes( String tableName, String brand, String product) throws Exception
	{
		return getSizes(tableName, brand, product, false);
	}

	public Cursor getSizes( String type, String brand, String product, Boolean includeOther  ) throws Exception
	{
		try{
			SQLiteDatabase db = getReadableDatabase();
			String optionSql = includeOther ? makeOptional("size") : "";
	        String sql =  optionSql + "select _id, size from vProducts " +
					"where product_type=\"" + type +
					"\" and brand=\"" + brand + "\" and product_name=\"" + product +
					"\" group by size";

	        Cursor c = db.rawQuery(sql, null);
	        c.moveToFirst();
	        return c;
		}
		catch(Exception e)
		{
			MainActivity.log_exception(m_context,e, "getSizes");
			throw e;
		}
	}

	public Cursor getInventory( int barID ) throws Exception
	{
		try{
			SQLiteDatabase db = getReadableDatabase();
	        String sql = "select * from vInventory where bar_id=" + barID +" order by _id desc";
	        Cursor c = db.rawQuery(sql, null);
	        c.moveToFirst();
	        return c;
		}
		catch(Exception e)
		{
			MainActivity.log_exception(m_context, e, "getInventory");
			throw e;
		}
	}

	public Cursor getInventory( int barID, String type ) throws Exception
	{
		try{
			SQLiteDatabase db = getReadableDatabase();
	        String sql = "select * from vInventory where bar_id=" + barID +
	        		" and product_type='" + type + "' order by _id desc";
	        Cursor c = db.rawQuery(sql, null);
	        c.moveToFirst();
	        return c;
		}
		catch(Exception e)
		{
			MainActivity.log_exception(m_context, e, "getInventory");
			throw e;
		}
	}

	public Cursor getShoppingList( int barID ) throws Exception
	{
		try{
			SQLiteDatabase db = getReadableDatabase();
	        String sql = "select * from vShoppingList where bar_id=" + barID;
	        Cursor c = db.rawQuery(sql, null);
	        c.moveToFirst();
	        return c;
		}
		catch(Exception e)
		{
			MainActivity.log_exception(m_context, e, "getShoppingList");
			throw e;
		}
	}

	public int getProdIdFromShoppingId( int shopping_id ) throws Exception
	{
		try{
			SQLiteDatabase db = getReadableDatabase();
	        String sql = "select * from ShoppingList where _id=" + shopping_id;
	        Cursor c = db.rawQuery(sql, null);
	        c.moveToFirst();
	        return c.getInt(c.getColumnIndex("product_id"));
		}
		catch(Exception e)
		{
			MainActivity.log_exception(m_context, e, "getShoppingList");
			throw e;
		}
	}

	public Cursor getFromUPC( String upc ) throws Exception
	{
		// look up product by UPC in global product tables
		try
		{
			SQLiteDatabase db = getReadableDatabase();

			// data is currently stored in multiple tables.
			// and the have to match the categories hard coded.
			// to-do fix that maybe.

			String sql = MessageFormat.format("Select * from vProducts where upc=\"{0}\" ", upc);
			Cursor c = db.rawQuery(sql, null);
			if(c.getCount() > 0)
				return c;

		}
		catch(Exception e)
		{
			MainActivity.log_exception(m_context, e, "getFromUPC");
			throw e;
		}

		throw new Exception(NOT_FOUND);
	}

	public Cursor getAllAndFromUPC( String upc ) throws Exception
	{
		// look up product by UPC in global product tables
		try
		{
			SQLiteDatabase db = getReadableDatabase();

			// data is currently stored in multiple tables.
			// and the have to match the categories hard coded.
			// to-do fix that maybe.

            String columns = "_id, product_type, brand, product_name, size";
			String sql1 = MessageFormat.format("Select {1}, 0 as sort from vProducts where upc=\"{0}\" ", upc, columns);
			String sql2 = MessageFormat.format("Select {1}, 1 as sort from vProducts where upc<>\"{0}\" ", upc, columns);

			String sql = sql1 + " UNION " + sql2 + " order by sort asc";

			Cursor c = db.rawQuery(sql, null);

			if(c.getCount() > 0)
				return c;
		}
		catch(Exception e)
		{
			MainActivity.log_exception(m_context, e, "getFromUPC");
			throw e;
		}

		throw new Exception(NOT_FOUND);
	}

	// helper here as we don't have idiomatic use of Option[..]
	// to keep this code together
	public Boolean UPCExsits( String upc ) throws Exception
	{
		try{
			getFromUPC(upc);
		}
		catch(Exception e){
			return false;
		}
		return true;
	}

	public void writeInventory(
			Integer barId,
			Integer prodId,
			double quantity)
	{
		SQLiteDatabase dbw = getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("bar_id", barId);
		values.put("product_id", prodId);
		values.put("quantity", quantity);

		dbw.insert("Inventory", "product_name", values);

	}

	public int writeProduct(
			Integer typeId,
			String brand,
			String name,
			String size,
			String UPC ) throws Exception
	{
		SQLiteDatabase dbw = getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("product_key", typeId);
		values.put("product_name", name);
		values.put("brand", brand);
		values.put("upc", UPC);
		values.put("ean", "1111");
		values.put("size", size);

		if(UPCExsits(UPC))
			dbw.update("Products", values, "upc=?", new String[]{UPC});
		else
			dbw.insert("Products", "", values);

			Cursor c=getFromUPC(UPC);
			c.moveToFirst();
			return c.getInt(c.getColumnIndex("_id"));
	}

	public void deleteBar(int barID)
    {
        SQLiteDatabase dbw = getWritableDatabase();
        dbw.delete("Bars", "_id="+barID, null);
    }

	public int updateQuantity(Integer invId)
	{
		String sql = "select * from vInventory where _id=" + invId;
		SQLiteDatabase db = getReadableDatabase();

		Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();

        Double quantity = c.getDouble(c.getColumnIndex("quantity"));

        SQLiteDatabase dbw = getWritableDatabase();

        ContentValues values = new ContentValues();
        double newQ = quantity-0.25;
        values.put("quantity", newQ);

        int prodId = -1;
        try
        {
        	if( newQ <= 0.0 )
        	{
        		String sql2 = "select product_id from Inventory where _id=" + invId;
        		Cursor c2 = db.rawQuery(sql2, null);
                c2.moveToFirst();
                prodId = c2.getInt(0);
                dbw.delete("Inventory", "_id="+invId, null);
        	}
        	else
        		dbw.update("Inventory", values, "_id="+invId, null);

        	return prodId;
        }
        catch(Exception e)
        {
        	MainActivity.log_exception(m_context, e, "updateQuantity");
        	return -1; // TODO - fix me
        }
	}

	public void addToShopping(int prodID, int barId)
	{
		ContentValues values = new ContentValues();
	    values.put("bar_id", barId);
	    values.put("product_id", prodID);

	    SQLiteDatabase dbw = getWritableDatabase();
		dbw.insert("ShoppingList", "", values);
	}

	public void removeFromShopping(int iid)
	{
		SQLiteDatabase dbw = getWritableDatabase();
		dbw.delete("ShoppingList", "_id=" + iid, null);
	}


	public int newBar(String barName)
	{
		ContentValues values = new ContentValues();
	    values.put("name", barName);

	    SQLiteDatabase dbw = getWritableDatabase();
		dbw.insert("Bars", "", values);

		return getBarId(barName);
	}

	public void removeBar(String barName)
	{
		SQLiteDatabase dbw = getWritableDatabase();
		dbw.delete("ShoppingList", "name="+barName, null);
	}

	public int getBarId(String barName)
	{
		SQLiteDatabase db = getReadableDatabase();
		String sql = MessageFormat.format("Select * from Bars where name=\"{0}\" ", barName);
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		return c.getInt(0);
	}

	public Cursor getBars() throws Exception
	{
		try{
			SQLiteDatabase db = getReadableDatabase();

			String sql ="select * from Bars";
	        Cursor c2 = db.rawQuery(sql, null);

	        c2.moveToFirst();
	        return c2;
		}
		catch(Exception e)
		{
			MainActivity.log_exception(m_context, e, "getBars");
			throw e;
		}
	}

	public Cursor searchProds( String term, SrchTyp type )
	{
		final String sBrands = String.format(
				"select * from vProducts where brand like \"%%%s%%\"", term);

		final String sProduct = String.format(
				"select * from vProducts where product_name like \"%%%s%%\"", term);

		return search( sBrands, sProduct, type);
	}


	public Cursor searchBars( String term, SrchTyp type )
	{
		final String sBrands = String.format(
				"select * from vInventory where brand like \"%%%s%%\"", term);

		final String sProduct = String.format(
				"select * from vInventory where product_name like \"%%%s%%\"", term);

		return search( sBrands, sProduct, type);
	}

	public Cursor search( String term, SrchTyp type, int barId )
	{
		final String sBrands = String.format(
				"select * from vInventory where bar_id=%d and brand like \"%%%s%%\"",
				barId, term);

		final String sProduct = String.format(
				"select * from vInventory where bar_id=%d and product_name like \"%%%s%%\"",
				barId, term);

		return search( sBrands, sProduct, type);
	}

	public Cursor search( String bSQL, String pSQL, SrchTyp type )
	{
		SQLiteDatabase db = getReadableDatabase();
		final String sAll = bSQL + " union " + pSQL;

		String sql = "";
		switch(type){
		case ALL:
			sql = sAll;
			break;
		case PRODUCTS:
			sql = pSQL;
			break;
		case BRANDS:
			sql = bSQL;
			break;
		}

		Cursor c2 = db.rawQuery(sql, null);
		c2.moveToFirst();
        return c2;

	}

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    SAVE_DB =true;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    SAVE_DB =false;
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

	public String saveDB(Context context)
	{
		try{
            close();
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                // need to request permission to write external storage
                ActivityCompat.requestPermissions((Activity)context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE);

                // permission handled by callback, check result
                if(SAVE_DB)
                    return saveDB2(context);
                else
                {
                    Toast.makeText(context,"save permission denied", Toast.LENGTH_LONG).show();
                    return "NOT SAVED";
                }
            }
            else
                // already had permission, just save
                return saveDB2(context);
        }
		catch(Exception e)
		{
			MainActivity.log_exception(context, e, "saveDB");
			return "EXCEPTION";
		}
	}

    public String saveDB2(Context context) throws java.io.IOException
    {
        String dir = context.getApplicationInfo().dataDir + "/databases";
        String path = dir +"/" + DATABASE_NAME;
        File f2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "new_barkeep_db");
        Toast.makeText(context,"dest:"+f2.getAbsolutePath(), Toast.LENGTH_LONG).show();
        FileInputStream dbs = new FileInputStream(path);
        FileOutputStream ds = new FileOutputStream(f2);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = dbs.read(buffer))>0){
            ds.write(buffer, 0, length);
        }
        ds.flush();
        ds.close();
        dbs.close();
        return f2.getAbsolutePath();
    }

}
