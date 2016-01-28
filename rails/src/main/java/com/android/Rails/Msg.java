package com.android.Rails;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.widget.Toast;

 public class Msg {
	
	static String message = new String();
	static String toastMessage = new String();
	
	static void clear(){
		message = "";
	}
	
	static void set(String input){
		message = input;
	}
	
	static void logcat(String input){
		Log.i(Cnt.LOG,input);
	}
	
    static void toast(String text){
	    
    	toastMessage = text;
		RailView.parentActivity.runOnUiThread(action);
	}

    // http://developer.android.com/reference/android/app/Activity.html#runOnUiThread(java.lang.Runnable)
	static private  Runnable action = new Runnable() {
        public void run() {
        	
        	Context context = RailView.parentActivity;
    		int duration = Toast.LENGTH_SHORT;
    		Toast toast = Toast.makeText(context, toastMessage, duration);
    		toast.show();
        }
    };
	
	static void onDraw(Canvas canvas, Paint paint){
		
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		paint.setTextSize(20);

		float xLeftOffset = 20,
			  xRightOffset = xLeftOffset;
		
		int remainingChars = message.length(),
			index=0,
			stroke=0;
		
		while(remainingChars>0){
			
			int charsToDraw = paint.breakText(message.substring(index, index+remainingChars), 
												true, 
												canvas.getWidth()-(xLeftOffset+xRightOffset), 
												null);
			canvas.drawText(message.substring(index, index+charsToDraw), 
							xLeftOffset, 
							30+stroke*paint.getTextSize(), 
							paint);
			
			index+=charsToDraw;
			remainingChars -= charsToDraw;
			stroke++;
			
		}
		
	}
	
}
