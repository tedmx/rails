package com.android.Rails;

import java.util.Iterator;

import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

class RefresherThread extends Thread {
	
		private SurfaceHolder threadSurfaceHolder;
    	private RailView threadView;
    	boolean running=false;
    	long counter = Long.MIN_VALUE;
    	
    	/** SystemClock.uptimeMillis() **/
    	static long time = 0;
    	static long lastFrameBeginning=0;
    	
    	public RefresherThread(RailView inputThreadPanel, SurfaceHolder inputHolder){
    		threadSurfaceHolder = inputHolder;
    		threadView = inputThreadPanel;
    	}
    	    
		@Override
		public void run (){
    		
    		Canvas tempCanvas;
    		
    		while(running){
    			
    			tempCanvas = null;
    			
    			if((time=SystemClock.uptimeMillis())-lastFrameBeginning>Cnt.DT){
    				
    				lastFrameBeginning=time;
    				
    				TouchMsgBuf.execStack();
    				
    				CoreLogics.progress();
    				
	    			try{
	    				tempCanvas = threadSurfaceHolder.lockCanvas();
		    			synchronized(threadSurfaceHolder) { 
				    		threadView.onDraw(tempCanvas);
				    	}
	    			} finally {
	    				if (tempCanvas != null) {
	    					threadSurfaceHolder.unlockCanvasAndPost(tempCanvas);
	                    }
	    			}
	    			
	    			counter++;
	    			
		    		
    			}
		    		
    		}
    		
    	}
    }