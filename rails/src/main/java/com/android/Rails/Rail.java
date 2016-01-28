package com.android.Rails;

import java.util.EnumMap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.RectF;

public class Rail {
	
	EnumMap<Ph,PhSt> set = new EnumMap<Ph, PhSt>(Ph.class);
	
	public static Path interim = new Path();
	public static Path outerim = new Path();
	static boolean inited = false;
	Ph highlightedPh = null;
	final static int rimSegmentsCount = 6;
	/** 
	 * Красивый центр гнутого рельса.
	 */
	static float centerShift = (float) (Cnt.cellSize*Math.sqrt(2)/4f);
	
	// вершины, обозначающие возможные концы любого рельса
	static float[][] pts = {{(float)Cnt.cellSize/3,0},
				     {(float)Cnt.cellSize*2/3,0},
				     {Cnt.cellSize,(float)Cnt.cellSize/3},
				     {Cnt.cellSize,(float)Cnt.cellSize*2/3},
				     {(float)Cnt.cellSize*2/3,Cnt.cellSize},
				     {(float)Cnt.cellSize/3,Cnt.cellSize},
				     {0,(float)Cnt.cellSize*2/3},
				     {0,(float)Cnt.cellSize/3}};
	
	static void drawLine(int s, int e, Paint p, Canvas c){  
		c.drawLine(pts[s][0], pts[s][1], pts[e][0], pts[e][1], p);
	}
	
	public Rail(){
		if(!inited){
			interim.moveTo(pts[6][0], pts[6][1]);
			outerim.moveTo(pts[7][0], pts[7][1]);
			Path targetPaths[] = {interim,outerim};
			float arcRadiuses[] = {Cnt.cellSize/3.0f,Cnt.cellSize*2/3.0f};
			float radProgress = 0;
			
			for(int j=0; j<2; j++){
				for(int i=1; i<=rimSegmentsCount; i++){
					radProgress = (float)Math.toRadians(90.0f*(1-(float)i/rimSegmentsCount));
					targetPaths[j].lineTo(arcRadiuses[j]*(float)Math.cos(radProgress),
										  Cnt.cellSize-arcRadiuses[j]*(float)Math.sin(radProgress));
				}
			}
			interim.lineTo(pts[5][0], pts[5][1]);
			outerim.lineTo(pts[4][0], pts[4][1]);
			
			inited = true;
		}
	}
	
	public void onDraw(Canvas canvas, Paint paint){
		
		paint.setStyle(Style.STROKE);
		
		for (Ph p : Ph.values()) {
			
			if(!set.containsKey(p)){ 
				continue;
			} else if(set.get(p)==PhSt.EXI){
				paint.setColor(Cnt.railColor);
				paint.setStrokeWidth(0.5f);
			} else if(set.get(p)==PhSt.DEMOL){
				paint.setColor(Cnt.railToDemolColor);
				paint.setStrokeWidth(3);
			} else if(set.get(p)==PhSt.TODEPL){
				 paint.setColor(Cnt.railToDeployColor);
				 paint.setStrokeWidth(3);
			} else if(set.get(p)==PhSt.TOSWTCH){
				 paint.setColor(Cnt.railToSwitchColor);
				 paint.setStrokeWidth(1);
			} 
			
			drawPath(canvas, paint, p, 1);
			
			paint.setStrokeWidth(0);
			
		}
		
		paint.setStyle(Style.FILL);
	
	}
	
	static public void drawPath(Canvas canvas, Paint paint, Ph p, float scale){
		if(p==Ph.VER){
			canvas.save();
			canvas.scale(scale, scale, Cnt.cellSize/2.0f,Cnt.cellSize/2.0f);
			drawLine(0, 5,  paint, canvas);
			drawLine(1, 4, paint, canvas);
			canvas.restore();
		} else if(p==Ph.HOR){
			canvas.save();
			
			canvas.scale(scale, scale, Cnt.cellSize/2.0f,Cnt.cellSize/2.0f);
			drawLine(7, 2,  paint, canvas);
			drawLine(6, 3, paint, canvas);
			canvas.restore();
		} else if(p==Ph.LEFTO){
			canvas.save();
			
			canvas.scale(scale, scale, 
					centerShift,
					centerShift);
			
			canvas.translate(Cnt.cellSize,0);
			canvas.rotate(90);
			
			
			canvas.drawPath(interim, paint);
			canvas.drawPath(outerim, paint);
			canvas.restore();
		} else if(p==Ph.LEFBO){
			
			canvas.save();
			canvas.scale(scale, scale, 
					centerShift,
					Cnt.cellSize-centerShift);
			canvas.drawPath(interim, paint);
			canvas.drawPath(outerim, paint);
			canvas.restore();
			
		} else if(p==Ph.TOPRI){
			canvas.save();
			
			canvas.scale(scale, scale, 
					Cnt.cellSize-centerShift,
					centerShift);
			canvas.translate(Cnt.cellSize,Cnt.cellSize);
			canvas.rotate(180);
			canvas.drawPath(interim, paint);
			canvas.drawPath(outerim, paint);
			canvas.restore();
		} else if(p==Ph.BOTRI){
			canvas.save();
			canvas.scale(scale, scale, 
					Cnt.cellSize-centerShift,
					Cnt.cellSize-centerShift);
			canvas.translate(0,Cnt.cellSize);
			canvas.rotate(270);
			canvas.drawPath(interim, paint);
			canvas.drawPath(outerim, paint);
			canvas.restore();
		}
	}
	
	public Ph getPath(Ph p){
		if(set.containsKey(p)){
			return p;
		}
		return null;
	}
	/** Выбирает первый попавшийся
	 * вид рельсов, имеющий выход 
	 * в направлении in.
	 */
	public Ph getPath(Lrtb in){
		if(highlightedPh!=null && highlightedPh.contains(in)) return highlightedPh;
		for (Ph p : Ph.values()) 
			if(set.containsKey(p) 
				&& set.get(p)!=PhSt.TODEPL
				&& p.otherEnd(in)!=null)
					return p;
		return null;
	}
	
	/** Выбирает первый попавшийся
	 * вид рельсов, имеющий выход 
	 * в направлении in или в направлении,
	 * перпендикулярном in.
	 */ 
	public Ph getPathMoreFreely(Lrtb in){
		Ph selectionTry = getPath(in);
		if(selectionTry!=null){
			return selectionTry;
		}
		if(in==Lrtb.B || in==Lrtb.T){
			selectionTry = getPath(Lrtb.L);
			if(selectionTry!=null){
				return selectionTry;
			}
			selectionTry = getPath(Lrtb.R);
			if(selectionTry!=null){
				return selectionTry;
			}
		}  else if (in==Lrtb.L || in==Lrtb.R){
			selectionTry = getPath(Lrtb.B);
			if(selectionTry!=null){
				return selectionTry;
			}
			selectionTry = getPath(Lrtb.T);
			if(selectionTry!=null){
				return selectionTry;
			}
		}
		return null;
		
	}

	public void clear(){
		set.clear();
	}
 
	public Ph getPath(){
		for (Ph p : Ph.values()) 
			if(set.containsKey(p) && set.get(p)==PhSt.EXI)
				return p;
		return null;
	}

	public void applyAct(){
		
		for (Ph p : Ph.values()) 
			if(set.containsKey(p))
				if(set.get(p)==PhSt.DEMOL){
					if(p == highlightedPh) highlightedPh = null;
					set.remove(p);
					break;
				} else if(set.get(p)==PhSt.TODEPL){
					set.remove(p);
					set.put(p, PhSt.EXI);
					break;
				} else if (set.get(p)==PhSt.TOSWTCH){
					Effector.deploy(CoreLogics.selectedTile[0], 
									CoreLogics.selectedTile[1],
									p);
				}
	}

	public void cancelAct(){
		
		for (Ph p : Ph.values())    
			if(set.containsKey(p))
				if(set.get(p)==PhSt.DEMOL){
					set.remove(p);
					if(highlightedPh == p) 
						set.put(p, PhSt.TOSWTCH);
					else 
						set.put(p, PhSt.EXI);
					break;
				} else if(CoreLogics.mode == Mode.SWITCH && set.get(p)==PhSt.TOSWTCH){
					set.remove(p);
					set.put(p, PhSt.EXI);
					break;
				} else if(set.get(p)==PhSt.TODEPL){
					set.remove(p);
					break;
				}
			
	}

	public void addToPreview(Ph in){
			
		cancelAct();
		
		if(set.containsKey(in)){
			set.remove(in);
			set.put(in, PhSt.DEMOL);
		} else {
			set.put(in, PhSt.TODEPL);
		}
		 
	}
	
	public void switchHighlight(Ph in){
		
		if(set.containsKey(in)){
			boolean anyIntersection = false;
			for (Ph p : Ph.values())
				if(p!=in 
				&& set.containsKey(p) 
				&& p.intersects(in))
					anyIntersection = true;
			if(!anyIntersection) return;
			cancelAct();
			set.remove(in);
			set.put(in, PhSt.TOSWTCH);
			highlightedPh = in;
		}
		 
	}

	public void lay(Ph in){
		set.put(in, PhSt.EXI);
	}
	
	/**
	 * Есть ли у данного рельса несколько путей, 
	 * имеющих выход в данном направлении.
	 */
	public boolean fork(Lrtb in){
		
		boolean gotOne = false;
		for (Ph p : Ph.values())    
			if(set.containsKey(p) && p.contains(in))
				if(gotOne) return true;
				else gotOne = true;
		
		return false;
	}
	
}
