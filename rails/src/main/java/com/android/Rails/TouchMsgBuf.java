package com.android.Rails;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Matrix;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class TouchMsgBuf {
	
	static public class TouchMsg {
		int action;
		float[] pos;
		float deltaTime;
		
		TouchMsg(int inpAction, float[] inpPos, float inpDeltaTime){
			deltaTime = inpDeltaTime;
			action = inpAction;
			pos = inpPos;
		}
	}
	
	static List<TouchMsg> msgStack = new ArrayList<TouchMsg>();
	
	static void execMsg(TouchMsg inMsg){

		switch (inMsg.action){
			
		case KeyEvent.ACTION_UP:
			
			if(!CoreLogics.browseMode){
				boolean trainStaysHere = CoreLogics.a.tileOccupied(CoreLogics.selectedTile[0],
																   CoreLogics.selectedTile[1]);
				if(trainStaysHere){
					CoreLogics.tiles[CoreLogics.selectedTile[0]]
									[CoreLogics.selectedTile[1]]
										.getRail()
											.cancelAct();
					Msg.toast("Train stays here.");
				} else 
					
					CoreLogics.tiles[CoreLogics.selectedTile[0]]
									[CoreLogics.selectedTile[1]]
										.getRail()
											.applyAct();
			} else {
				
				RailView.trans[0]-=
						(RailView.camStartPos[0]-inMsg.pos[0])/Scaler.currentScale;
					RailView.trans[1]-=
						(RailView.camStartPos[1]-inMsg.pos[1])/Scaler.currentScale;
					
					RailView.antialias = true;
					
					CoreLogics.browseMode = false;
				
			}
			
			CoreLogics.unselectTile();
			
			RailView.redrawAllReq();
			
			break;
			
		case KeyEvent.ACTION_DOWN:
			
			RailView.camStartPos = inMsg.pos;
			
			float[] mapPos = CoreLogics.screenToMap(inMsg.pos);
			int[] gridPts = CoreLogics.mapToGrid(mapPos);
			
			if(gridPts[0]<0 
				|| gridPts[1]<0 
				|| gridPts[0]>=CoreLogics.tiles.length 
				|| gridPts[1]>=CoreLogics.tiles[0].length ){
				
				CoreLogics.browseMode = true;
					
				
			} else {

				CoreLogics.selectTile(gridPts[0], gridPts[1]);
				RailView.redrawAllReq();
				
			}
			
			break;  
			
		case KeyEvent.ACTION_MULTIPLE:
			
			if(CoreLogics.browseMode){
				
				RailView.trans[0]-=
					(RailView.camStartPos[0]-inMsg.pos[0])/Scaler.currentScale;
				RailView.trans[1]-=
					(RailView.camStartPos[1]-inMsg.pos[1])/Scaler.currentScale;
				RailView.camStartPos = inMsg.pos;
				
			}
			
			if(!CoreLogics.browseMode){
				
				float[] mapCoords = CoreLogics.screenToMap(inMsg.pos); 
				
				if(!CoreLogics.inBound(mapCoords, CoreLogics.selectedTile)){
					
					CoreLogics.browseMode = true;
					CoreLogics.tiles[CoreLogics.selectedTile[0]][CoreLogics.selectedTile[1]].getRail().
						cancelAct();
					RailView.antialias = false;
					
				} else {
				
					Ph type = CoreLogics.sector(mapCoords,CoreLogics.selectedTile);
					if(CoreLogics.mode == Mode.BUILD){
						CoreLogics.tiles[CoreLogics.selectedTile[0]][CoreLogics.selectedTile[1]].getRail().
							addToPreview(type);
					} else {
						CoreLogics.tiles[CoreLogics.selectedTile[0]][CoreLogics.selectedTile[1]].getRail().
							switchHighlight(type);
					}
				}
			}
			
			RailView.redrawAllReq();
			
			break;
			
		}   
	}
	
	static public void addMsg (int event, float[] pos, float deltaTime){
		
		synchronized(msgStack){ 
			
			msgStack.add(new TouchMsg(event, pos, deltaTime));
			
		}
		
	}
	
	static public void execStack(){
		
		synchronized(msgStack){
			
			if(msgStack.size()>0){
				Iterator<TouchMsg> msgStackI = msgStack.iterator();
				
				while(msgStackI.hasNext()){
					execMsg(msgStackI.next());
				}
				
				msgStack.clear();
			}
			
		}
		
	}
	
}
