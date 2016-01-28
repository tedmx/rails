package com.android.Rails;

import java.util.Iterator;
import java.util.LinkedList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class RailVect {
	
	static RailVect worker;
	
	int i;
	int j;
	Ph path;
	FwBc fwbc;
	float progress;
	
	
	public RailVect() {
		// TODO Auto-generated constructor stub
	}
	
	RailVect(int i, int j, Ph path, FwBc dir, float prg){

		this.i = i;
		this.j = j;
		this.path = path;
		this.fwbc = dir;
		this.progress = prg;
	}
	
	static public RailVect getWorker(){
		if(worker==null){
			worker = new RailVect(0,0,Ph.BOTRI,FwBc.BCK,0);
		}
		return worker;
	}
	
	static public RailVect getWorker(RailVect copyFrom){
		if(worker==null){
			worker = new RailVect(0,0,Ph.BOTRI,FwBc.BCK,0);
		}
		worker.i = copyFrom.i;
		worker.j = copyFrom.j;
		worker.fwbc = copyFrom.fwbc;
		worker.path = copyFrom.path;
		worker.progress = copyFrom.progress;
		return worker;
	}
	
	/** Интерфейс shift(), выбирающий первое попавшееся продолжение пути на новых тайлах.*/
	public boolean fluffyShift(float amount){
		return shift(amount,false,null,false,null);
	}
	
	public boolean readBackShift(float amount, LinkedList<Ph> hist, Iterator<Ph> histI){
		return shift(amount, false, hist, true, histI);
	}
	
	public boolean writeForwardShift(float amount, LinkedList<Ph> hist){
		return shift(amount, true, hist, false, null);
	}
	
	/** Продвигается по рельсовому пути на величину amount
	 * в исходном направлени или против него согласно значению keepDir.
	 * При невозможности дальнейшего продвижения возвращает false,
	 * не меняя состояние вызывающего вектора.
	 * Можно выбирать пути из массива памяти, что делают вагоны,
	 * или добавлять в массив случайно выбираемый путь, что делает локомотив.*/
	private boolean shift(float amount, boolean keepDir, LinkedList<Ph> hist, boolean read, Iterator<Ph> histI){
		
		FwBc chosenFwbc = keepDir==true ? fwbc : fwbc.reverse();
		
		int curCoords[] = {i, j};
		Ph curPh = path, nextPh;
		Tile nextTile;
		FwBc curFwbc = chosenFwbc;
		float remainderAmount = amount;
		
		/* Величина прогресса в начале каждого шага цикла,
		 * например исходный прогресс или прогресс сразу после
		 * перехода на новый тайл. */
		float startProgress = progress;
		
		/* Величина прогресса в конце каждого шага цикла:
		 * исходный прогресс плюс оставшаяся для прохождения
		 * длина пути.*/
		float incrProgress = startProgress+curFwbc.toInt()*amount;
		
		Lrtb outDirection;
		
		while(incrProgress<curPh.minProgress()
			  ||incrProgress>curPh.maxProgress()){
				
			if(incrProgress<curPh.minProgress())
				remainderAmount -= startProgress-curPh.minProgress(); 
			else 
				remainderAmount -= curPh.maxProgress() - startProgress; 
			
			outDirection = curPh.end(curFwbc);
			outDirection.shift(curCoords);
			nextTile = CoreLogics.getTile(curCoords[0], curCoords[1]);
			if(nextTile==null) return false;
			// Чтение из памяти
			if(read && histI!=null && histI.hasNext())
					curPh = nextTile.
							getRail().
								getPath(histI.next());
			// Флафф
			else if(read)
					curPh = nextTile.
						getRail().
							getPath(outDirection.inverse());
			// Запись в память
			else {
				nextPh = nextTile.
						getRail().
						getPath(outDirection.inverse());
				if(nextPh==null) return false;
				if(hist!=null) hist.addFirst(curPh);
				curPh = nextPh;
			}
			if(curPh==null) return false;
			curFwbc = curPh.getFwbcFrom(outDirection.inverse());
	
			startProgress = curFwbc==FwBc.FWD? curPh.minProgress() : curPh.maxProgress();
			incrProgress = startProgress+curFwbc.toInt()*remainderAmount;
			
		}
	
		i = curCoords[0];
		j = curCoords[1];
		path = curPh;
		progress = incrProgress;
		fwbc = curFwbc;
		return true;
		
	}
	
	public RailVect clone(){
		RailVect r = new RailVect();
		r.i = this.i;
		r.j = this.j;
		r.path = this.path;
		r.fwbc = this.fwbc;
		r.progress = this.progress;
		return r;
	}
	
	public boolean choosePathAndMvDir(Lrtb inDirection){

		Rail targetRail = CoreLogics.getTile(this.i, this.j).getRail();
		Ph chosenPath = targetRail.getPathMoreFreely(inDirection);
		if(chosenPath == null){
			chosenPath = targetRail.getPath();
			path = chosenPath;
			fwbc = FwBc.FWD;
			return false;
		}
		path = chosenPath;
		fwbc = chosenPath.getFwbcFrom(inDirection).reverse();
		return true;
	}
	
	public void drawRailCar(Canvas c, Paint p){
		
		float[] stPos = getMapPos();  
		float a = this.getAngle();
		
		float halfwidth = 100/6f;
		
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(3f);
		
		c.save(); 
		c.translate(stPos[0], stPos[1]);  
		c.rotate((float) Math.toDegrees(-a));
		c.drawRect(CoreLogics.realFromRelative(-Cnt.railcarLength/2), 
				   CoreLogics.realFromRelative(-halfwidth), 
				   CoreLogics.realFromRelative(Cnt.railcarLength/2), 
				   CoreLogics.realFromRelative(halfwidth), p);
		c.restore();
		
		p.reset();
		
	}
	
	public float[] getMapPos(){
		float[] toRet = path.shiftOfPrgr((float)progress/path.maxProgress());
		toRet[0]+=j*Cnt.cellSize;
		toRet[1]+=i*Cnt.cellSize;
		return toRet;
	}
	
	public float getAngle(){
		
		return path.angle(progress);
		
	}
	
	public void drawDot(Canvas c, Paint p){
		
		float[] pos = getMapPos();
		p.setColor(Color.BLACK);
		c.drawCircle(pos[0],pos[1],Cnt.cellSize/6, p);
		
	}

}
