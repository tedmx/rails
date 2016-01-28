package com.android.Rails;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;

 class RailView extends SurfaceView implements SurfaceHolder.Callback  {
	 
	static Activity parentActivity;
	public RefresherThread surfaceThread;
	
	long tchTime, lastTchTime;
	static Random generator = new Random();
	static Interpolator decel = new DecelerateInterpolator();
	static Paint p = new Paint();
	
	// камера
	static float[] trans = new float[2];
	static float[] camStartPos = new float[2];
	static Matrix viewMat = new Matrix();
	static Matrix invMat = new Matrix();
	   
	static boolean antialias = true;
	
	static Canvas cacheCanvas;
	static Bitmap cacheBitmap;
	static private boolean redoCache = true;
	
	public RailView(Activity inpParentActivity) {
		
		super(inpParentActivity);
		parentActivity=inpParentActivity;
       
        CoreLogics.init();
       
        getHolder().addCallback(this);
        
	}
	
	public void draw(Canvas c, boolean drawGround, boolean drawTrain){
		
		p.setAntiAlias(antialias);
		
		if(drawGround)
			c.drawColor(Cnt.paddingColor);
		
		c.save();
		
		c.translate(CoreLogics.screenSize[0]/2.0f-CoreLogics.tilesSize[0]/2.0f+trans[0], 
					CoreLogics.screenSize[1]/2.0f-CoreLogics.tilesSize[1]/2.0f+trans[1]);
		
		c.scale(Scaler.currentScale, Scaler.currentScale, 
				CoreLogics.tilesSize[0]/2.0f-trans[0], 
				CoreLogics.tilesSize[1]/2.0f-trans[1]);
		
		viewMat = c.getMatrix();   
		viewMat.invert(invMat);
	
	
		float[] LUScreenCrn = CoreLogics.screenToMap(new float[2]);
		float[] RBScreenCrn = CoreLogics.screenToMap(CoreLogics.screenSize);
		
		if(drawGround)
			for(int i=0; i<Cnt.cellDim[0]; i++)
				for(int j=0; j<Cnt.cellDim[1]; j++){  
					
					if(LUScreenCrn[0]>Cnt.cellSize*(j+1) ||
					   LUScreenCrn[1]>Cnt.cellSize*(i+1) ||
					   RBScreenCrn[0]<Cnt.cellSize*(j) ||
					   RBScreenCrn[1]<Cnt.cellSize*(i))
							continue;
						
					c.save();
						c.translate(Cnt.cellSize*j, Cnt.cellSize*i);
						p.setStyle(Style.FILL);
						CoreLogics.tiles[i][j].onDraw(c,p);
					c.restore();
					 
					if(i!=0 && j!=0){
						p.setColor(Cnt.railColor);
						p.setStyle(Style.STROKE);
						p.setStrokeWidth(0.5f);
						c.drawRect(Cnt.cellSize*j-1, Cnt.cellSize*i-1, 
								   Cnt.cellSize*j+1, Cnt.cellSize*i+1, p);
						/*c.drawCircle(
								Cnt.cellSize*j,
								Cnt.cellSize*i,
								1, paint);*/
					}
				}
			
		if(drawTrain){
			CoreLogics.a.onDraw(c, p);
			CoreLogics.b.onDraw(c, p);
			ShotCollector.onDraw(c, p);
		}
		
		if(!drawGround)	
			Effector.onDraw(c);
	    c.restore(); 
	    
	    Msg.onDraw(c, p);
	    
	}
	
	

	
	@Override 
	public void onDraw(Canvas c){ 
		
		if(cacheCanvas==null || cacheBitmap==null) return;
			
		if(redoCache){
			
			draw(cacheCanvas, true, false);
			redoCache = false;
			
		}
		
		c.drawBitmap(cacheBitmap, 0, 0, p);
		
		draw(c, false, true);
		
	}

	@Override 
	public boolean onTouchEvent(MotionEvent event) {
		
		lastTchTime = tchTime;
		tchTime = System.currentTimeMillis();
		
		float deltaTime =  (tchTime-lastTchTime)/Cnt.DT;
		
		float[] eventCoords = { event.getX(),event.getY()};
		
		TouchMsgBuf.addMsg(event.getAction(), eventCoords, deltaTime);
					
		return true;

	}
	
	/** просьба перерисовать весь экран*/
	static public void redrawAllReq(){
		redoCache = true;
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		
		CoreLogics.screenSize[0] = width;
		CoreLogics.screenSize[1] = height;
		
		cacheBitmap = Bitmap.createBitmap(width,
										  height, 
										   Config.ARGB_8888);
		
		cacheCanvas = new Canvas(cacheBitmap);
		
		float heightScale = CoreLogics.screenSize[1]/(float)(CoreLogics.tiles.length*Cnt.cellSize+2*Cnt.padding);
		float widthScale = CoreLogics.screenSize[0]/(float)(CoreLogics.tiles[0].length*Cnt.cellSize+2*Cnt.padding);
		Scaler.outScale = Math.min(heightScale, widthScale);
		Scaler.currentScale = Scaler.outScale;
		Scaler.inScale = Cnt.zoomingFactor*Scaler.outScale;
	}
	
	public void surfaceCreated(SurfaceHolder holder) {
		
		surfaceThread = new RefresherThread(this,getHolder()); 
		surfaceThread.running=true;
		surfaceThread.start();
	}
	
	public void surfaceDestroyed(SurfaceHolder holder) {
		
		boolean retry = true;
		surfaceThread.running=false;
		while(retry){
			try {
				surfaceThread.join();
				 retry = false;
			} catch (InterruptedException e) {
				
			}
		}
					
	}
}
