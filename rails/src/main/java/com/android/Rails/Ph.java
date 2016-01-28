package com.android.Rails;

import java.util.EnumMap;


public enum Ph {
    VER, HOR, LEFTO, LEFBO,
    TOPRI, BOTRI;
    
    static final float horVerMaxProgress = Cnt.relativeCellSize;
    static final float curveMaxProgress = Math.round((float)(horVerMaxProgress*Math.PI/4));
    
    static Lrtb map[][] = new Lrtb[6][2];
    
    static
    {
    	map[VER.ordinal()][0] = Lrtb.B;
    	map[VER.ordinal()][1] = Lrtb.T;
    	map[HOR.ordinal()][0] = Lrtb.L;
    	map[HOR.ordinal()][1] = Lrtb.R;
    	map[BOTRI.ordinal()][0] = Lrtb.B;
    	map[BOTRI.ordinal()][1] = Lrtb.R;
    	map[LEFBO.ordinal()][0] = Lrtb.L;
    	map[LEFBO.ordinal()][1] = Lrtb.B;
    	map[TOPRI.ordinal()][0] = Lrtb.T;
    	map[TOPRI.ordinal()][1] = Lrtb.R;
    	map[LEFTO.ordinal()][0] = Lrtb.L;
    	map[LEFTO.ordinal()][1] = Lrtb.T;
    	
    }
    
  public boolean intersects(Ph other){
	  if(map[this.ordinal()][0]==map[other.ordinal()][0] || 
		 map[this.ordinal()][1]==map[other.ordinal()][0] || 
		 map[this.ordinal()][0]==map[other.ordinal()][1] || 
		 map[this.ordinal()][1]==map[other.ordinal()][1])
		  	return true;
	  return false;
  }
    
    public boolean contains(Lrtb in){
    	
    	if(map[this.ordinal()][0] == in 
    	   || map[this.ordinal()][1] == in)
    		return true;
    	else return false;
    }
    
    public Lrtb otherEnd(Lrtb e){
    	
    	if(map[this.ordinal()][0] == e)
    		return map[this.ordinal()][1];
    	else if (map[this.ordinal()][1] == e)
    		return map[this.ordinal()][0];
    	else return null;
    }
    
    static public Ph make(Lrtb a, Lrtb b){
    	for (Ph p : Ph.values())    
    		if(p.contains(a) && p.contains(b)) return p;
    	return null;
    }
    
    /** Направление, на которое
     будете смотреть при выходе из пути,
     если будете двигаться
     согласно указанному FwBc.*/
    public Lrtb end (FwBc dir){
    	
    	return map[this.ordinal()][dir==FwBc.FWD? 1 : 0];
    	
    }
    
    // знак приращения скорости
    // в зависимости от типа дороги
    // и начальной точки 
    public FwBc getFwbcFrom(Lrtb source){
    	
    	if(map[this.ordinal()][0] == source)
    		return FwBc.FWD;
    	else if (map[this.ordinal()][1] == source)
    		return FwBc.BCK;
    	else return null;
    	
    }
    
    public float maxProgress(){
    	if(this == HOR || this == VER){
    		return horVerMaxProgress;
    	} else {
    		return curveMaxProgress;
    	}
    }
    
   public float minProgress(){
    	return 0;
    }
   
	public float[] shiftOfPrgr(float progress){
		float[] ret = {0,0};
		if(this == Ph.HOR){
			ret[0] = progress*Cnt.cellSize;
			ret[1] = 0.5f*Cnt.cellSize;
		} else if(this == Ph.VER){
			ret[0] = 0.5f*Cnt.cellSize;
			ret[1] = (1-progress)*Cnt.cellSize;
		} else if (this == Ph.LEFBO){
			ret[0] = (float)(Cnt.cellSize/2.0f*Math.cos(Math.toRadians(90*(progress-1))));
			ret[1] = (float)(Cnt.cellSize+Cnt.cellSize/2.0f*Math.sin(Math.toRadians(90*(progress-1))));
		} else if (this == Ph.LEFTO){
			ret[0] = (float)(Cnt.cellSize/2.0f*Math.cos(Math.toRadians(90*(1-progress))));
			ret[1] = (float)(Cnt.cellSize/2.0f*Math.sin(Math.toRadians(90*(1-progress))));
		} else if (this == Ph.TOPRI){
			ret[0] = (float)(Cnt.cellSize+Cnt.cellSize/2.0f*Math.cos(Math.toRadians(180-90*progress)));
			ret[1] = (float)(Cnt.cellSize/2.0f*Math.sin(Math.toRadians(180-90*progress)));
		} else if (this == Ph.BOTRI){
			ret[0] = (float)(Cnt.cellSize+Cnt.cellSize/2.0f*Math.cos(Math.toRadians(180+90*progress)));
			ret[1] = (float)(Cnt.cellSize+Cnt.cellSize/2.0f*Math.sin(Math.toRadians(180+90*progress)));
		} 
		
		return ret;
	}
	
   /* Угол, который составляет с нулем 
    * находящийся в правом полукруге единичной окружности конец прямой,
    * движущейся по пути. */
   public float angle(float progress){
	   
		   if(this == HOR)
			   return 0;
		    else if (this==VER)
			   return (float)Math.PI/2;
		    else if(this==BOTRI)
			   return (float)(Math.PI/2*(1-progress/(float)BOTRI.maxProgress()));
		    else if(this==LEFBO)
			   return (float)(-Math.PI/2*progress/(float)LEFBO.maxProgress());
		    else if(this==LEFTO)
			   return (float)(Math.PI/2*progress/(float)LEFTO.maxProgress());
		    else  // TOPRI
			   return (float)(-Math.PI/2*(1-progress/(float)TOPRI.maxProgress()));
		   
		   
   }

}