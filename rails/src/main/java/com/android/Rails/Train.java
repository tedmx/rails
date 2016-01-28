package com.android.Rails;

import java.util.Iterator;
import java.util.LinkedList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Train extends RailVect {
	
	static Iterator<Ph> historyI;
	static RailVect traverser;
	
	float currentSpd;
	final int railcarNumber;
	/**
	 * Память; хранит все подряд пройденные локомотивом пути.
	 */
	LinkedList<Ph> history;
	int[][] occupiedIJ;
	float[][] crds;
	boolean moving;
	final int hurtTimeDuration = 300;
	final int hurtTimePeriod = 60;
	long hurtStartTime = -hurtTimeDuration;
	
	public Train(int length, int gridI, int gridJ, Lrtb direction) {
		
		moving = true;
		currentSpd = 0;
		railcarNumber = length;
		history = new LinkedList<Ph>();
		
		i = gridI;
		j = gridJ;
		choosePathAndMvDir(direction);
		progress = fwbc==FwBc.FWD? path.maxProgress() : path.minProgress();
		occupiedIJ = new int[railcarNumber][2];
		crds = new float[railcarNumber][2];
	}
	
	public void firstRailcar(){
		traverser = RailVect.getWorker(this);
		historyI = history.iterator();
		traverser.readBackShift(Cnt.railcarLength/2, history,historyI);
		traverser.fwbc = traverser.fwbc.reverse();
	}
	
	public void nextRailCar(){
		traverser.readBackShift(Cnt.railcarLength+Cnt.railcarGap, history, historyI);
		traverser.fwbc = traverser.fwbc.reverse();  
	}
	
	public boolean tileOccupied(int i, int j){
		
		for(int[] tile : occupiedIJ)
			if(i==tile[0] && j==tile[1]){
				return true;
			}
		return false;
	}
	
	public void progress(){
		
		if(!moving) return;

		writeForwardShift(currentSpd,history);
		while(history.size()>railcarNumber)  
			history.removeLast();
		
		boolean turning = false;
		firstRailcar();
		
		for(int i=0; i<railcarNumber; i++){
		
			if(traverser.path!=Ph.HOR 
			   && traverser.path!=Ph.VER)
				turning = true;
			
			occupiedIJ[i][0] = traverser.i;
			occupiedIJ[i][1] = traverser.j;
			crds[i] = traverser.getMapPos();
			nextRailCar();
		}
		
		if(currentSpd>Cnt.maxSpeed) 
			currentSpd-=Cnt.spdIncr;
		else if(currentSpd>Cnt.maxRotSpd && turning) 
			currentSpd-=Cnt.spdIncr;
		else
			currentSpd+=Cnt.spdIncr;
		
	}
	
	public void hurt(){
		hurtStartTime = RefresherThread.time;
	}
	
	public void onDraw(Canvas c, Paint p){
		
		firstRailcar();
		for(int i=0; i<railcarNumber; i++){
			if(RefresherThread.time-hurtStartTime>hurtTimeDuration || 
				Math.round((RefresherThread.time-hurtStartTime)/hurtTimePeriod)%2==0){
				p.setColor(Cnt.railcarColor);
			} else {
				p.setColor(Cnt.railToDemolColor);
			}
			traverser.drawRailCar(c, p);
			nextRailCar();     
		}
	}
	
	
}
