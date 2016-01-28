package com.android.Rails;

import android.graphics.Canvas;
import android.graphics.Paint;

public  class ShotCollector {
	static final int count = 10;
	static Shot[] a = new Shot[count];

	
	static void init(){
		for (int i=0; i<count; i++)
			a[i] = new Shot();
	}
	  
	static void onDraw(Canvas c, Paint p){
		for (int i=0; i<count; i++)
			a[i].onDraw(c, p);
	}
	
	static void add(float srX, float srY, float angle, Team target){
		for (int i=0; i<count; i++)
			if(!a[i].alive()){
				a[i].init(srX, srY, angle, target); return;
			}
	}
}
