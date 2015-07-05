package com.kjcondron.barkeep;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class InventoryAdapter extends SimpleCursorAdapter {
	
	private int mQuantityId;
	protected int[] mTo;

	protected int brandColId;
	protected int prodColId;
	protected int sizeColId;
	protected int typeColId;
	
	private Map<String, Bitmap> bmCache;
	
	public InventoryAdapter(
			Context ctxt,
			int layout,
			Cursor cursor,
			String[] colNames,
			int[] to,
			int flag,
			int quantityTo
			)
	{
		super(ctxt, layout, cursor, colNames, to, flag);
		// TODO assert to.length == 3
		// TODO assert colNames.length == 3
		mTo = to;
		brandColId = cursor.getColumnIndexOrThrow(colNames[0]);
		prodColId  = cursor.getColumnIndexOrThrow(colNames[1]);
		sizeColId  = cursor.getColumnIndexOrThrow(colNames[2]);
		typeColId  = cursor.getColumnIndexOrThrow(colNames[3]);
		mQuantityId = quantityTo;
		
		bmCache = new HashMap<String, Bitmap>();
	}
		
	public void bindView(View view, Context context, Cursor cursor)
	{
		sbindView(view, context, cursor);
		ProgressBar progQ =(ProgressBar)view.findViewById(mQuantityId);
		
		int qIdx = cursor.getColumnIndexOrThrow("quantity");
		int q = (int)(cursor.getDouble(qIdx) * 100);
        
		progQ.setProgress(q);
	}
	
	public void sbindView(View view, Context context, Cursor cursor) {
		final Paint p = new Paint();
		final int[] to = mTo;
		
		final TextView bv = (TextView)view.findViewById(to[0]);
		final TextView pv = (TextView)view.findViewById(to[1]);
		final TextView sv = (TextView)view.findViewById(to[2]);
		final ImageView iv = (ImageView)view.findViewById(to[3]);
		
		String bText = cursor.getString(brandColId);
		String pText = cursor.getString(prodColId);
		String sText = cursor.getString(sizeColId);
		String tText = cursor.getString(typeColId);
		
		//String imagePath = "/storage/emulated/0/images/" + tText + ".jpg";
		                    
		float h = p.measureText(bText + pText);
		setViewText(bv, bText);
		setViewText(pv, pText);
		setViewText(sv, sText);
		safeSetImage(context, iv, tText);
	}
	
	protected void safeSetImage(Context context, ImageView iv, String path)
	{
		// TODO use bitmap decoder to size image and set it, or 
		// maybe a LRU cache if we are going to allow image per
		// item. Also make it a singleton
		if(null != iv)
		{
		
			if(!bmCache.containsKey(path))
			{
				Bitmap bm = null;
				try
				{
					//bm = decodeSampledBitmapFromPath(path, iv.getDrawable().getIntrinsicWidth(), iv.getDrawable().getIntrinsicHeight());
					bm = decodeSampledBitmapFromAssets(context, path, iv.getDrawable().getIntrinsicWidth(), iv.getDrawable().getIntrinsicHeight());
				}
				catch(OutOfMemoryError e)
				{
					MainActivity.log_message(null, path, "InventoryAdapter");
					MainActivity.logHeap(getClass());
				}
		
				bmCache.put(path, bm);
			}
			
			iv.setImageBitmap(bmCache.get(path));
		}
			
	}
	
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
	
    	return inSampleSize;
	}
	
	public static Bitmap decodeSampledBitmapFromPath(String path, 
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(path, options);
	}
	
	public static Bitmap decodeSampledBitmapFromAssets(Context ctxt, String name, 
	        int reqWidth, int reqHeight) {
		try{
		AssetManager assetMan = ctxt.getAssets( );
		InputStream istr  = assetMan.open("images/" + name.toLowerCase() + ".jpg");
        
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeStream(istr, null, options);
		
	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    InputStream istr2  = assetMan.open("images/" + name.toLowerCase() + ".jpg");
        return BitmapFactory.decodeStream(istr2, null, options);
		}
		catch(Exception e)
		{
			return null;
		}
	}

}
