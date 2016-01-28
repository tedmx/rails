package com.android.Rails;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

enum Team {
	ENEMY, PLAYER;
}

public class Shot {
	Team target;
	float crds[] = {0,0};
	float angle;
	static final int dur = 600;
	long startTime=-dur;
	static final float dist = 100;
	static final float length = 10;
	static final float hurtDist = 10;
	static final DecelerateInterpolator a = new  DecelerateInterpolator(0.7f);
	
	
	public Shot(){
		
	}
	
	boolean alive(){
		return (RefresherThread.time-startTime)/(float)dur<1;
	}
	
	void init(float srX, float srY, float angle, Team target){
		crds[0] = srX;
		crds[1] = srY;
		this.angle = angle;
		this.target = target;
		startTime = RefresherThread.time;
	}
	
	public Shot(float srX, float srY, float angle, Team target) {
		init(srX, srY, angle, target);
	}
	
	
	
	void onDraw(Canvas c, Paint p){
		
		
		float partDone = (RefresherThread.time-startTime)/(float)dur;
		if(partDone>1){ return;}
		
		
		float[] currentCrds = {crds[0]+a.getInterpolation(partDone)*dist*(float)Math.cos(angle),
							crds[1]-a.getInterpolation(partDone)*dist*(float)Math.sin(angle)};
		
		for(int i=0; i<CoreLogics.a.railcarNumber; i++){
			if(Math.sqrt((CoreLogics.a.crds[i][1]-currentCrds[1])*
							(CoreLogics.a.crds[i][1]-currentCrds[1])
							+(CoreLogics.a.crds[i][0]-currentCrds[0])*
							(CoreLogics.a.crds[i][0]-currentCrds[0]))<hurtDist){
				startTime=-dur;
				CoreLogics.a.hurt();
				return;
			}
		}
		
		p.setStrokeWidth(1.0f);
		p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
		p.setColor(Cnt.shotColor);
		
		c.drawLine(currentCrds[0], 
				   currentCrds[1], 
				   currentCrds[0] +length*(float)Math.cos(angle),
				   currentCrds[1]-length*(float)Math.sin(angle), p);
		p.reset();
		
		
	}
}
