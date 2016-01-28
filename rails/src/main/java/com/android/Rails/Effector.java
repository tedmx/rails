package com.android.Rails;

import android.R.interpolator;
import android.graphics.Canvas;
import android.graphics.Paint.Style;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class Effector {
	
	static int[] location = {0,0};
	static boolean endless = false;
	static int progress = 100;
	static Ph type = Ph.LEFBO;
	static int speed = 3;
	
	
	static void onDraw(Canvas c){
		
		RailView.p.setStyle(Style.STROKE);
		RailView.p.setStrokeWidth(1);
		c.save();
		RailView.p.setColor(Cnt.railToSwitchColor);
		RailView.p.setAlpha(Math.round((255*Math.abs(100-progress)/100f)));
		
		c.translate(location[1]*Cnt.cellSize,location[0]*Cnt.cellSize);
		
		Rail.drawPath(c, RailView.p, type, RailView.decel.getInterpolation(Math.abs(progress)/100f)+1);
		
		c.restore();
		RailView.p.reset();
		
		if(progress+speed>100)
			if(endless)
				progress = (progress+speed)%100;
			else
				progress = 100;
		else
			progress+=speed;
		
	}
	
	static void deploy(int i, int j, Ph inType){
		location[0] = i;
		location[1] = j;
		progress = 0;
		type = inType;
	}
	
}
