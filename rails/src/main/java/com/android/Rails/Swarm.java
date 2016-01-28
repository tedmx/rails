package com.android.Rails;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class Swarm {
	
	float crds[] = {20,20};
	
	int count = 5;
	final int radius = 20;
	final int dotRadius = 2;
	float subCrds[][] = new float[count][2];
	float polarVects[] = new float[count];
	float dotSpeed = 1;
	long lastShotTime = -1;
	final int shotPause = 1000;
	final float sight = Shot.dist*1.2f;
	
	/**
	 * Подверженность произвольным отклонениям направления движения точки.
	 */
	final float randomFactor = 0.4f;
	/**
	 * Расстояние в долях радиуса от центра, после которого точка испытывает
	 * возвращательное воздействие.
	 */
	final float wanderingBorder = 0.9f;
	/**
	 * Консервативность направления движения.
	 */
	final float pastFactor = 1f;
	
	public Swarm() {
		for(int i=0; i<count; i++){
			subCrds[i][0] = (float) (Math.random()*2-1)*radius;
			subCrds[i][1] = (float) (Math.random()*2-1)*radius;
		}
	}
	
	public void progress(){
		
		
		
		crds[0]= 100+80*(float)Math.sin(Math.toRadians(RefresherThread.time*.03));
		for(int i=0; i<count; i++){
		
			float polars[] = {(float) Math.atan2(-subCrds[i][1], subCrds[i][0]),
							  (float) Math.sqrt(subCrds[i][0]*subCrds[i][0]+subCrds[i][1]*subCrds[i][1])};
			
			
			float randomAngle = (float) (Math.random()*Math.PI*2);
			
			
			float centerAngle = polars[0]+(float) Math.PI;
			
			float centerFactor = polars[1]/(float)radius<wanderingBorder? 0 : polars[1]/(float)radius;
			
			float compositeVect[] = {(float) (Math.cos(polarVects[i])*pastFactor
									+Math.cos(randomAngle)*randomFactor
									+Math.cos(centerAngle)*centerFactor),
									(float) (Math.sin(polarVects[i])*pastFactor
									+Math.sin(randomAngle)*randomFactor
									+Math.sin(centerAngle)*centerFactor)};
			
			polarVects[i] = (float)Math.atan2(compositeVect[1],compositeVect[0]);
			
			subCrds[i][0]+=Math.cos(polarVects[i])*dotSpeed;
			subCrds[i][1]-=Math.sin(polarVects[i])*dotSpeed;
			
		}
		
		if(RefresherThread.time>lastShotTime+shotPause){
			
			boolean trainInsight = false;
			float train[] = {0,0};
			for(int i=0; i<CoreLogics.a.railcarNumber; i++){
				train[0] = CoreLogics.a.crds[i][0];
				train[1] = CoreLogics.a.crds[i][1];
				if(Math.sqrt((train[1]-crds[1])*(train[1]-crds[1])+(train[0]-crds[0])*(train[0]-crds[0]))<sight){
					trainInsight = true;  
					break;
				}
			}
			if(trainInsight){
				int shooter = (int) Math.floor(Math.random()*count);
				float angle = (float) Math.atan2(crds[1]+subCrds[shooter][1]-train[1], train[0]-crds[0]-subCrds[shooter][0]);
				ShotCollector.add(crds[0]+subCrds[shooter][0], crds[1]+subCrds[shooter][1], angle, Team.PLAYER);
				lastShotTime = RefresherThread.time;
			}
		}
	}
	
	public void onDraw(Canvas c, Paint p){
		p.setColor(Cnt.enemyColor); 
		for(int i=0; i<count; i++){
			c.drawCircle(crds[0]+subCrds[i][0], 
						 crds[1]+subCrds[i][1], 
					     dotRadius, p);
		}
		p.setStyle(Style.STROKE);
		p.setStrokeWidth(1.5f);
		//c.drawCircle(crds[0], crds[1], radius, p);
	}
	
	
}
