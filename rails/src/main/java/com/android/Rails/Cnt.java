package com.android.Rails;

import android.graphics.Color;

import com.android.Rails.R;

public class Cnt {

	static final int FPS=25;
	static final String LOG = "debug";
	static final boolean antialias = false;
	
	static final int[][] rectVertOffset = {{-1,-1},{1,-1},{1,1},{-1,1}};
	static final float DT = 1000.0f/FPS;
	
	static final int[] cellDim = {12,10};     
	static final int cellSize = 20;
	
	static final int highlightedColor = 0xFFE8E8E8;   
	static final int railColor = 0xFF001D30;
	static final int railToDeployColor = 0xFFFFFFFF;
	static final int railToDemolColor = 0xFFFF0000;
	static final int railToSwitchColor = 0xFFDAFFC4;
	final static int enemyColor = 0xFFF0469B;
	final static int shotColor = 0xFF79AFDB;
	// зеленый+оранжевый FF9500 -> 14FF86
	// фиолетовый+голубой 0x745A8F 0x8DB0BA
	static final int tileStartColor = 0x615A8F;
	static final int tileEndColor = 0xF2F2F2;
	static final int paddingColor = Color.BLACK;
	static final int railcarColor = 0xFFE7FF4D;
	
	static final float relativeCellSize = 100f;
	static final float railcarLength = 40f;
	static final float railcarGap = 10f;
	static final float maxSpeed = 10f;
	static final float maxRotSpd = 6f;
	static final float spdIncr = 0.1f;
	static final int padding = 20;
	/** Множитель обычного масштаба
	 *  в значении увеличенного. **/
	static final float zoomingFactor = 2.0f; 
	final static int zoomAnimDuration = 700;
	
	
	
	
}
