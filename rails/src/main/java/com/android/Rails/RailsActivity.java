package com.android.Rails;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class RailsActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new RailView(this));
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    
	    return true;
	}
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	MenuItem a = menu.findItem(R.id.pause);
    	a.setTitle(CoreLogics.paused ? "Resume" : "Pause");
    	a = menu.findItem(R.id.mode);
    	a.setTitle(CoreLogics.mode==Mode.SWITCH ? "Build mode" : "Switch mode");
    	a = menu.findItem(R.id.zoom);
    	if(Scaler.state == Scaler.IN)
    		a.setTitle("Zoom out");
    	else if(Scaler.state == Scaler.OUT)
    		a.setTitle("Zoom in");
    	return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    protected void onRestart() {
    	// TODO Auto-generated method stub
    	RailView.redrawAllReq();
    	super.onRestart();
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.zoom:
            	if(Scaler.state== Scaler.IN){
            		Scaler.state = Scaler.DECR;
            		Scaler.animStartTime = RefresherThread.time;
            	} else if (Scaler.state == Scaler.OUT){
            		Scaler.state = Scaler.INCR;
            		Scaler.animStartTime = RefresherThread.time;
            	}
               
                return true;
            case R.id.reset:
            	CoreLogics.init();
                return true;
            case R.id.pause:
            	CoreLogics.paused = !CoreLogics.paused;
            	invalidateOptionsMenu();
            	return true;
            case R.id.mode:
            	CoreLogics.mode = CoreLogics.mode == Mode.BUILD ? Mode.SWITCH : Mode.BUILD;
            	invalidateOptionsMenu();
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}