package com.kjcondron.barkeep;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FilterQueryProvider;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ProductDetailActivity extends Activity {
	
	public final static String ADD_TO_DB = "com.kjcondron.barkeep.ADD_TO_DB";
	public final static String UPC = "com.kjcondron.barkeep.UPC";
	public final static Integer IDCOL = 0;
	public final static Integer TYPENAMECOL = 1;
	
	private DBHelper m_db;
    
	private Boolean mAddToDB = false;
	private Boolean mHaveUPC = false;
	
	private void createAllProducts()
	{		
	    ActionBar ab = getActionBar();
		ab.setTitle("Select Product");
		
	    setupListeners();
		
	    Intent intent = getIntent();
	    if(intent.hasExtra(AddActivity.PRODUCT_TYPE)){
	    	String type = intent.getStringExtra(AddActivity.PRODUCT_TYPE);
	    	setupTypeSpinner(type); // triggers other spinners
	    }
	    else{
	    	long typeid = intent.getLongExtra(AddActivity.PRODUCT_TYPEID, 0);
	    	setupTypeSpinner(typeid); // triggers other spinners
	    }
		
		findViewById(R.id.prodDetail_commitItem).setVisibility(View.VISIBLE);
		findViewById(R.id.prodDetail_commitItemAddToProducts).setVisibility(View.INVISIBLE);
	}
	
	private void createSingleProduct()
	{

		try{
			Spinner typeSpinner = (Spinner) findViewById(R.id.prodDetail_typeSpinner);
			Spinner brandSpinner = (Spinner) findViewById(R.id.prodDetail_brandSpinner);
			Spinner productSpinner = (Spinner) findViewById(R.id.prodDetail_prodSpinner);
			Spinner sizeSpinner = (Spinner) findViewById(R.id.prodDetail_sizeSpinner);
			
			Intent intent = getIntent();
			
			ArrayAdapter<CharSequence> adapter = 
		    		new ArrayAdapter<CharSequence>(
		    				this,
		    				android.R.layout.simple_spinner_item);
		    
	    	String upc = intent.getStringExtra(UPC);
	    	
	    	Cursor upcDeets = m_db.getFromUPC(upc); 
	    	
	    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    typeSpinner.setAdapter(adapter);
		    
		    setupSpinner(upcDeets, "product_type", typeSpinner); 
		    setupSpinner(upcDeets, "brand", brandSpinner);
		    setupSpinner(upcDeets, "product_name", productSpinner);
		    setupSpinner(upcDeets, "size", sizeSpinner);
		    
		    findViewById(R.id.prodDetail_commitItem).setVisibility(View.VISIBLE);
			findViewById(R.id.prodDetail_commitItemAddToProducts).setVisibility(View.INVISIBLE);
		
		    
		}
    	catch(Exception e)
    	{
    		MainActivity.log_exception(this, e, "ProductDetailActivity.onCreateAllSingleProduct");
    	}
    
	}
	
	// to-do fix this first
	private void createProductNotFound()
	{
		setContentView(R.layout.layout_add_product);
		ActionBar ab = getActionBar();
		ab.setTitle("Product Not Found");
		final boolean guessFromInternet = true;
		try
	    {
	    
			String type = "";
			String brand = "";
			if(guessFromInternet)
			{
				String upc = getIntent().getStringExtra(UPC);
				HttpHelper.Result res = new HttpHelper(this).getDetails(upc);
				type = res.type;
				brand = res.brand;
			}
			else
			{
				type = m_db.getTypes().getString(TYPENAMECOL);
			}
	    	
	    	setupTypeSpinner(type);
	    	setupTypeListener(true);
			setupBrandAutoComplete(brand);
			setupProductAutoComplete();
	    }
	    catch(Exception e)
	    {
	    	MainActivity.log_exception(this, e, "createProductNotFound");
	    	finish();
	    }
	
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_db = new DBHelper(this);
		setContentView(R.layout.activity_product_detail);
		
		// This activity can be use to display
		
		// a) All products in db, with first spinner set up to show current category
		// b) specific product found in db (1 per spinner / should be text boxes)
		// c) an not found upc with editable spinners to allow selection / entry of detail = ADD_TO_DB 
		
		Intent intent = getIntent();
	    mAddToDB = intent.getBooleanExtra(ADD_TO_DB, false);
	    mHaveUPC = intent.hasExtra(UPC);
	    
	    if(mHaveUPC)
	    	if(mAddToDB)
	    		createProductNotFound();
	    	else
	    		createSingleProduct();
	    else
	    	createAllProducts();
	    
	}
	
	private String getType()
	{
		Spinner typeSpinner = (Spinner) findViewById(R.id.prodDetail_typeSpinner);
		Cursor ctype = (Cursor)typeSpinner.getSelectedItem();
    	return ctype.getString(TYPENAMECOL);
	}
	
	private Integer getTypeId()
	{
		Spinner typeSpinner = (Spinner) findViewById(R.id.prodDetail_typeSpinner);
		Cursor ctype = (Cursor)typeSpinner.getSelectedItem();
    	return ctype.getInt(IDCOL);
	}
	
	private String getBrandAuto()
	{
		return ((AutoCompleteTextView) findViewById(
					R.id.prodDetail_brandACTV)).getText().toString();
	}
	
	private String getProductAuto()
	{
		return ((AutoCompleteTextView) findViewById(
					R.id.prodDetail_prodACTV)).getText().toString();
	}
	
	private String getSizeAuto()
	{
		return ((AutoCompleteTextView) findViewById(
					R.id.prodDetail_sizeACTV)).getText().toString();
	}
		
	protected void setupTypeListener(final Boolean addingToDB)
	{
		Spinner typeSpinner = (Spinner) findViewById(R.id.prodDetail_typeSpinner);
	    
		typeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	    @Override
	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	    	if(!addingToDB)
	    	{
		    	TextView tv = (TextView) selectedItemView;
		    	String type = tv.getText().toString();
		    	setupBrandSpinner(type);
	    	}
	    	else
	    	{
				AutoCompleteTextView actv = (AutoCompleteTextView)findViewById(R.id.prodDetail_brandACTV); 
				setupBrandAutoComplete(null);
				actv.requestFocus();
			}
	    }

	    @Override
	    public void onNothingSelected(AdapterView<?> parentView) {
	    }

		});
	}
	
	protected void setupListeners()
	{		
		setupTypeListener(false);
		Spinner brandSpinner = (Spinner) findViewById(R.id.prodDetail_brandSpinner);
		
		brandSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	if(selectedItemView != null)
		    	{
			    	TextView tv = (TextView) selectedItemView;
			    	String brand = tv.getText().toString();
			    	setupProdSpinner(getType(), brand);
		    	}
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

			});
		
		Spinner prodSpinner = (Spinner) findViewById(R.id.prodDetail_prodSpinner);
		
		prodSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	
		    	Spinner typeSpinner = (Spinner) findViewById(R.id.prodDetail_typeSpinner);
		    	Spinner bs = (Spinner) findViewById(R.id.prodDetail_brandSpinner);
		    	
		    	if(selectedItemView != null && 
		    			typeSpinner.getSelectedItem() != null &&
		    			bs.getSelectedItem() != null)
		    	{
			    	Cursor c = (Cursor)bs.getSelectedItem();
			    	String brand = c.getString(c.getColumnIndex("brand"));
			    	TextView tv = (TextView) selectedItemView;
			    	String prod= tv.getText().toString();
			    	setupSizeSpinner(getType(), brand, prod);
		    	}
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

			});
	    
	}
	
	protected void setupTypeSpinner(String type)
	{
		try
		{
			Spinner typeSpinner = (Spinner) findViewById(R.id.prodDetail_typeSpinner);

			Cursor c = m_db.getTypes();
		    
		    SimpleCursorAdapter adapter = new SimpleCursorAdapter(
		    		this, 
		    		android.R.layout.simple_spinner_dropdown_item,
		    		c, 
		    		new String[]{ "product_type" },
		    		new int[] { android.R.id.text1 },
		    		CursorAdapter.NO_SELECTION );
		 
		    typeSpinner.setAdapter(adapter);
		    
		    for(int i=0; i<adapter.getCount(); ++i)
		    {
		    	SQLiteCursor cur = (SQLiteCursor)adapter.getItem(i);
		    	String val = cur.getString(TYPENAMECOL);
		    	if(val.equals(type))
		    	{
		    		typeSpinner.setSelection(i);
		    		break;
		    	}
		    }
		    
		}
		catch(Exception e)
		{
			MainActivity.log_exception(this, e, "setupTypeSpinner");
		}
	    
	}
	
	protected void setupTypeSpinner(long typeid)
	{
		try
		{
			Spinner typeSpinner = (Spinner) findViewById(R.id.prodDetail_typeSpinner);

			Cursor c = m_db.getTypes();
		    
		    SimpleCursorAdapter adapter = new SimpleCursorAdapter(
		    		this, 
		    		android.R.layout.simple_spinner_dropdown_item,
		    		c, 
		    		new String[]{ "product_type" },
		    		new int[] { android.R.id.text1 },
		    		CursorAdapter.NO_SELECTION );
		 
		    typeSpinner.setAdapter(adapter);
		    for(int i=0; i<adapter.getCount(); ++i)
		    {
		    	SQLiteCursor cur = (SQLiteCursor)adapter.getItem(i);
		    	int val = cur.getInt(IDCOL);
		    	if(val == typeid)
		    	{
		    		typeSpinner.setSelection(i);
		    		break;
		    	}
		    }
		    
		}
		catch(Exception e)
		{
			MainActivity.log_exception(this, e, "setupTypeSpinner");
		}
	    
	}
		
	protected void setupBrandSpinner(String product)
	{
		try
		{
			Spinner brands = (Spinner) findViewById(R.id.prodDetail_brandSpinner);
			
		    Cursor c = m_db.getBrands(product, mAddToDB);
		    
		    setupSpinner(c, "brand", brands);
		}
		catch(Exception e)
		{
			MainActivity.log_exception(this, e, "setupBrandSpinner");
		}
	    
	}
	
	protected void setupBrandAutoComplete(String brand)
	{
		try
		{	 
			final String type = getType();
			AutoCompleteTextView brands = (AutoCompleteTextView) findViewById(R.id.prodDetail_brandACTV);
			String currBrandText = brands.getText().toString();
			Log.w("kjc", "currentBrand:" + currBrandText);
			brands.setThreshold(1);
			
		    Cursor c = m_db.getBrands(type, false);
		    
		    SimpleCursorAdapter adapter = new SimpleCursorAdapter(
		    		this, 
		    		android.R.layout.simple_dropdown_item_1line,
		    		c, 
		    		new String[]{ "brand" },
		    		new int[] { android.R.id.text1 },
		    		CursorAdapter.NO_SELECTION );
		    
		    adapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
				
				@Override
				public CharSequence convertToString(Cursor cursor) {
					return cursor.getString(cursor.getColumnIndex("brand"));
				}
			});
		    
		    final Context ctxt = this;
		    adapter.setFilterQueryProvider(new FilterQueryProvider() {
				
				@Override
				public Cursor runQuery(CharSequence constraint) {
					try
					{
						return m_db.getBrandsFilter(getType(), constraint);
					}
					catch(Exception e)
					{
						MainActivity.log_exception(ctxt, e, "setupProductAutoComplete.runQuery");
						return null;
					}
				}
			});
		    
		    brands.setAdapter(adapter);
		    
		    brands.setOnEditorActionListener(new OnEditorActionListener() {
				
		    	@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if(actionId == EditorInfo.IME_ACTION_NEXT)
					{
						findViewById(R.id.prodDetail_prodACTV).requestFocus();
						return true;
					}
					return false;
				}
			});
		    
		    brands.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AutoCompleteTextView av = (AutoCompleteTextView) v;
					av.showDropDown();
					return;
				}
			});
		    
		    brands.setOnItemClickListener(new OnItemClickListener() {
			
		    	@Override
		    	public void onItemClick(AdapterView<?> parent, View selected, int pos, long id)
		    	{
		    		return;
		    	}
		    });
		    
		    brands.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if(hasFocus)
					{
						AutoCompleteTextView av = (AutoCompleteTextView) v;
						//av.showDropDown();
					}
				}
			});
		    
		    if(brand != null)
		    {
		    	brands.setText(brand);
		    	Log.w("kjc", "setting brand: " + brand);
		    }
		    else
		    {
		    	brands.setText(currBrandText);
		    	Log.w("kjc", "re-setting brand: " + currBrandText);
		    }
		    	
			
		}
		catch(Exception e)
		{
			MainActivity.log_exception(this, e, "setupBrandAutoComplete");
		}
	    
	}
	
	protected void setupProductAutoComplete()
	{
		try
		{
			AutoCompleteTextView prod = (AutoCompleteTextView) findViewById(R.id.prodDetail_prodACTV);
			prod.setThreshold(1);
			
			// brand is going to come from autocomplete
		    Cursor c = m_db.getProducts(getType(), getBrandAuto());
		    
		    SimpleCursorAdapter adapter = new SimpleCursorAdapter(
		    		this, 
		    		android.R.layout.simple_dropdown_item_1line,
		    		c, 
		    		new String[]{ "product_name" },
		    		new int[] { android.R.id.text1 },
		    		CursorAdapter.NO_SELECTION );
		    
		    adapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
				
				@Override
				public CharSequence convertToString(Cursor cursor) {
					return cursor.getString(cursor.getColumnIndex("product_name"));
				}
			});
		    
		    final Context ctxt = this;
		    adapter.setFilterQueryProvider(new FilterQueryProvider() {
				
				@Override
				public Cursor runQuery(CharSequence constraint) {
					try
					{
						return m_db.getProductsFilter(getType(), getBrandAuto(), constraint);
					}
					catch(Exception e)
					{
						MainActivity.log_exception(ctxt, e, "setupProductAutoComplete.runQuery");
						return null;
					}
				}
			});
		    
		    prod.setAdapter(adapter);
		    
		    prod.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AutoCompleteTextView av = (AutoCompleteTextView) v;
					av.showDropDown();
					return;
				}
			});
		    
		    prod.setOnItemClickListener(new OnItemClickListener() {
			
		    	@Override
		    	public void onItemClick(AdapterView<?> parent, View selected, int pos, long id)
		    	{
		    		return;
		    	}
		    });
		    
		    prod.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if(hasFocus)
					{
						AutoCompleteTextView av = (AutoCompleteTextView) v;
						//av.showDropDown();
					}
				}
			});
		

		}
		catch(Exception e)
		{
			MainActivity.log_exception(this, e, "setupBrandAutoComplete");
		}
	    
	}
	
	protected void setupProdSpinner(String type, String brand)
	{
		try
		{
			Spinner prods = (Spinner) findViewById(R.id.prodDetail_prodSpinner);
		    
		    Cursor c = m_db.getProducts(type, brand, mAddToDB);
		    
		    setupSpinner(c, "product_name", prods);
		    	    	    
		}
		catch(Exception e){
			MainActivity.log_exception(this, e, "setupProdSpinner");
		}
	    
	}
	
	protected void setupSizeSpinner(String type, String brand, String product)
	{
		try
		{
			Spinner size = (Spinner) findViewById(R.id.prodDetail_sizeSpinner);
		    
		    Cursor c = m_db.getSizes(type, brand, product, mAddToDB);
		    	    	    
		    setupSpinner(c, "size", size);
		}
		catch(Exception e)
		{
			MainActivity.log_exception(this, e, "setupSizeSpinner");
		}
	    
	}
	
	protected void setupSpinner(Cursor details, String columnName, Spinner spinner)
	{
		try
		{
		    SimpleCursorAdapter adapter = new SimpleCursorAdapter(
		    		this, 
		    		android.R.layout.simple_spinner_dropdown_item,
		    		details, 
		    		new String[]{ columnName },
		    		new int[] { android.R.id.text1 },
		    		CursorAdapter.NO_SELECTION );
		    
		    spinner.setAdapter(adapter);
		}
		
		catch(Exception e)
		{
			MainActivity.log_exception(this, e, "setupSpinner");
		}
	    
	}
	
	public void commitItem(View view)
	{
		m_db.writeInventory(
				MainActivity.BARID,
				getSpinnerValueInt(R.id.prodDetail_sizeSpinner, "_id"),
				1.0);
		
		finish();    	
	}
	
	public void commitItemAddToProducts(View view)
	{
		try {
		String upc = getIntent().getStringExtra(UPC);
		int newProdId = m_db.writeProduct(
				getTypeId(),
				getBrandAuto(),
				getProductAuto(),
				getSizeAuto(),
				upc);
		
		m_db.writeInventory(
				MainActivity.BARID,
				newProdId,
				1.0);
		
		finish();
		}
		catch(Exception e)
		{
			MainActivity.log_exception(this, e, "commitItemAddToProducts");
		}
	}
	
	private Integer getSpinnerValueInt(int id, String columnName)
	{
		Spinner spin = (Spinner) findViewById(id);
    	Cursor c = (Cursor)spin.getSelectedItem();
	    return c.getInt(c.getColumnIndex(columnName));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.product_detail, menu);
		return true;
	}

}
