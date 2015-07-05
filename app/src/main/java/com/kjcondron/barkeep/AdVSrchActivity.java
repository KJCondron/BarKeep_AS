package com.kjcondron.barkeep;

import android.os.Bundle;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class AdVSrchActivity extends Activity {
	
	private CheckBox mAllBars;
	private CheckBox mAllProds;
	private Spinner  mSpnType;
	
	protected class AdvSrchActListener implements TextView.OnEditorActionListener {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_SEARCH) {
				Intent intent = new Intent(AdVSrchActivity.this, SearchActivity.class);
				if(mAllBars.isChecked()) {
					intent.putExtra(SearchActivity.ALL_BARS, true);
				}
				
				if(mAllProds.isChecked()) {
					intent.putExtra(SearchActivity.ALL_PRODS, true);
				}
				
				int id = mSpnType.getSelectedItemPosition();
				intent.putExtra(SearchActivity.SEARCH_TYPE, id);
				
				String term = v.getText().toString();
				intent.putExtra(SearchManager.QUERY, term);
				startActivity(intent);
	            return true;
	        }
			return false;
		}
	}
	
	protected class CheckBoxListener implements View.OnClickListener {
		
		CheckBox mOtherBox;
		public CheckBoxListener( CheckBox otherBox ) {
			mOtherBox = otherBox;
		}
		
		@Override
		public void onClick(View v) {
			mOtherBox.setChecked(false);
		}
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		WindowManager.LayoutParams wmlp = getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP;
        
		setContentView(R.layout.layout_adv_search);
		
		mSpnType = (Spinner) findViewById(R.id.searchTypes);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.search_types_array, android.R.layout.simple_spinner_item);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		mSpnType.setAdapter(adapter);
		
		EditText searchTerm = (EditText) findViewById(R.id.searchTerm);
		searchTerm.setOnEditorActionListener(new AdvSrchActListener());
		
		mAllBars = (CheckBox) findViewById(R.id.cbxAllBars);
		mAllProds = (CheckBox) findViewById(R.id.cbxAllProds);
		
		mAllBars.setOnClickListener( new CheckBoxListener(mAllProds) );
		mAllProds.setOnClickListener( new CheckBoxListener(mAllBars) );
				
		}
	}
