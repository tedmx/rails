package com.android.Rails;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Tile {
	
	Rail r = new Rail();
	
	int groundColor =  Color.GRAY;
	boolean highlighted = false;
	
	public Tile(){
		float p = RailView.generator.nextFloat();
		groundColor =  Color.rgb(Math.round((Color.red(Cnt.tileStartColor)*p+Color.red(Cnt.tileEndColor)*(1-p))), 
								 Math.round(Color.green(Cnt.tileStartColor)*p+Color.green(Cnt.tileEndColor)*(1-p)),  
								 Math.round(Color.blue(Cnt.tileStartColor)*p+Color.blue(Cnt.tileEndColor)*(1-p)));
	}
	
	public void reset(){
		r = new Rail();
	}
	
	public Rail getRail(){
		return r;
	}
	
	public void onDraw(Canvas canvas, Paint paint){
		
		paint.setColor(groundColor);
		
		/*if(highlighted && CoreLogics.mode == Mode.BUILD){
			paint.setColor(Cnt.highlightedColor);
		} else {
			paint.setColor(groundColor);
		}*/
		
		canvas.drawRect(0, 0, Cnt.cellSize, Cnt.cellSize, paint);
		r.onDraw(canvas, paint);
	}
}
