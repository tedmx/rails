package com.android.Rails;

// Четыре стороны света.
public enum Lrtb {
	L,R,T,B;
	
	static public Lrtb valueOf(char in){
		 if(in=='R')
			 return R;
		 else if(in=='B')
			 return B;
		 else if(in=='T')
			 return T;
		 else if(in=='L')
			 return L;
		 else 
			 return null;
	}
	
	// изменить координаты тайла
	// в соответствии с выбранным
	// направлением
	public void shift(int[] coords){
		
		if(this==L)
			coords[1]--;
		else if (this==T)
			coords[0]--;
		else if (this==R)
			coords[1]++;
		else if (this==B)
			coords[0]++;
		
	}
	
	// выполняется при переключении
	// с тайла на тайл,
	// при смене "системы координат"
	public Lrtb inverse(){
		
		if(this==L)
			return R;
		else if (this==T)
			return B;
		else if (this==R)
			return L;
		else // this==B
			return T;
		
	}
	
}

