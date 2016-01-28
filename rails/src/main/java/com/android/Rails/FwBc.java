package com.android.Rails;

// Индикатор движения вперед или назад
// по одному неразветвленному участку рельс.
public enum FwBc {
	FWD, BCK;
	
	public int toInt(){
		if(this==FWD)
			return 1;
		else 
			return -1;
	}
	
	public FwBc reverse(){
		if(this==FWD)
			return BCK;
		 else 
			return FWD;
	}
	
}
