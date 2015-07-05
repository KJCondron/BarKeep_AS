package com.kjcondron.barkeep;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

public class HttpHelper {
	
	public class Result
	{
		public String type;
		public String brand;
		public String product;
		public String size;
	}
	
	DBHelper mdb;
	Context mc;
	
	HttpHelper(Context c) { mc = c; mdb = new DBHelper(mc); } 
	
	//final String PREFIX = "http://www.google.com/search?q=upc+";
	final String PREFIX = "http://www.google.com/search?q=";
	
	
	class HttpTask extends AsyncTask<String, Void, HttpHelper.Result> {
			
		protected Result doInBackground(String... upcs)
		{
			Result res = new Result();
			try{
			String query = PREFIX + upcs[0];
			
			HttpClient httpclient = new DefaultHttpClient();

	        HttpGet request = new HttpGet();
	        URI website = new URI(query);
	        request.setURI(website);
	        HttpResponse response = httpclient.execute(request);
	        
	        BufferedReader in = new BufferedReader(new InputStreamReader(
	                response.getEntity().getContent()));
	        
	        // dump header
	        String line = in.readLine();
	        Pattern ph = Pattern.compile("/head");
    		Matcher mh = ph.matcher(line);
	        while( !mh.find() )
	        	mh=ph.matcher(in.readLine());
	        
	        Cursor ts = mdb.getTypes();
	        Cursor bs = mdb.getAllBrands();
	        int[] bcount = new int[bs.getCount()];
	        
	        int[] tcount = new int[ts.getCount()];
	        int tpos = 0;
	        int bpos = 0;
	        line = in.readLine();
	        String allRes = "";
	        String brand = "";
	        boolean brandFound = false;
	        int bestCount = 0;
	        while( line != null )
	        {
	        	allRes += line;
	        	tpos = 0;
	        	ts.moveToFirst();
	        	do
	        	{
	        		Pattern p = Pattern.compile(ts.getString(1));
	        		Matcher m = p.matcher(line);
	        		int findCount = 0;
	        		while(m.find())
	        			findCount +=1;
	        		tcount[tpos] += findCount;
	        		tpos+=1;
	        	}	
	        	while(ts.moveToNext());
	        	
	        	
	        	if(! brandFound )
	        	{
		        	bpos = 0;
		        	bs.moveToFirst();
		        	do
		        	{
		        		String testBrand = bs.getString(1);
		        		String s = testBrand.split("'")[0]; //simple throw away of ' not found in html 
		        		if( !reserved(s) ) {
			        		Pattern p1 = Pattern.compile(s);
			        		Matcher m1 = p1.matcher(line);
			        		int brandCount = 0;
			        		while(m1.find())
			        			brandCount +=1;
			        		bcount[bpos] += brandCount;
			        		if( bcount[bpos] > 10 ) // good enough
			        		{
			        			brand = bs.getString(1);
			        			brandFound = true;
			        		}
			        		
			        		if( bcount[bpos] > bestCount )
			        		{
			        			bestCount = bcount[bpos];
			        			brand = bs.getString(1);
			        		}
			        		bpos +=1;
		        		}
		        	} while( !brandFound && bs.moveToNext() );
	        	}
	        	
	        	line = in.readLine();
	        }
	        
	        int c = 0;
	        ts.moveToFirst();
	        for( int x : tcount )
	        {
	        	if(x>c)
	        		res.type = ts.getString(1);
	        	ts.moveToNext();
	        }
	        
			res.brand = brand;
			
			}
			catch(Exception e)
			{
				MainActivity.log_exception(mc, e, "HttpTask");
			}
			
			return res;
		}
		
	}
	
	private boolean reserved(String s)
	{
		String sl = s.toLowerCase(Locale.ENGLISH);
		final String[] reservedWords = {"liqueur", "liqour"};
		for(String rw : reservedWords)
			if(sl.equals(rw))
				return true;
		
		return false;
	}
	
	public Result getDetails( String upc )
	{
		Result res = null;
		try{
			res = new HttpTask().execute(upc).get();
			
			Toast.makeText(mc, res.brand, Toast.LENGTH_LONG).show();
		}
		catch(Exception e)
		{
			MainActivity.log_exception(mc, e, "HttpTask");
		}
		return res;
		
	}
}
