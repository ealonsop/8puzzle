package com.cixlabs.njuego8;




import android.content.Context;
import android.graphics.Canvas;
import android.opengl.GLES10;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class Juego8View extends GLSurfaceView 
{
	
	public Juego8Renderer mRenderer;
	
	float lastX, lastY, lastDX, lastDY;
	int first;
	boolean moving;
	int mctr;
	// Offsets for touch events	 
	
	
	Context contexto;
    
        	
	public Juego8View(Context context) 
	{
		super(context);		
		first = 1;
		moving = false;
		mctr = 0;
	}
	
	public Juego8View(Context context, AttributeSet attrs) 
	{
		super(context, attrs);		
		first = 1;
		contexto = context;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		float x, y;
        float deltaX, deltaY;
		
		if (event != null && mRenderer != null )
		{			

		  //    Log.d(this.getClass().getName(),String.valueOf(x) + " " + String.valueOf(y));
		      
	
			if ( event.getAction() == MotionEvent.ACTION_UP ) {
				   x = event.getX();
				   y = event.getY();
				   //Log.d(this.getClass().getName(),"touch.. " + x + " , " + y);

/*				   if ( moving ) {
						deltaX = Math.abs(x - lastDX);
						deltaY = Math.abs(y - lastDY);
						if ( deltaX <= 5 && deltaY <= 5 )
							moving = false;
				   }*/
				   if ( mctr <= 3 )
					   moving = false;
			       if ( !moving ) {
			    	//  Log.d(this.getClass().getName(),"touchOK.. " + x + " , " + y);
			          mRenderer.touch_up(x,y);
			       }
			       moving = false;
			       mctr = 0;
				   return true;
			}
			
			if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
				   x = event.getX();
				   y = event.getY();
			    if ( first == 1 ) {
			           first = 0;
			           lastX = x;
			           lastY = y;
			    }
			    lastDX = x;
			    lastDY = y;
//				Log.d(this.getClass().getName(),"down.. " + x + " , " + y);			    
				moving = false;
				mctr = 0;
		        mRenderer.touch_down(x,y);
				return true;
			}
			
			if (event.getAction() == MotionEvent.ACTION_MOVE)
			{

				moving = true;
				mctr++;
				x = event.getX();
				y = event.getY();
	//			Log.d(this.getClass().getName(),"moving.. " + x + " , " + y);
				if ( first == 1 ) {
				           first = 0;
				           lastX = x;
				           lastY = y;
				           return true;
				}
				       

				deltaX = x - lastX;
				deltaY = y - lastY;    
				lastX = x;
				lastY = y;		      
				float adx = Math.abs(deltaX);
				float ady = Math.abs(deltaY);
				      
			//		  Log.d(this.getClass().getName(),"move.. " + x + " , " + y);

      
				if( adx > 20 || ady > 20 || mctr <= 3 )
				    return true;
				      
				if ( ady > adx ) { // 
				    	  if ( deltaY > 0 ) { // hacia abajo
				                  mRenderer.moveDown(x,y);		  
				    	  }
				    	  else {
				                  mRenderer.moveUp(x,y);  		  
				    	  }
				    	  
				}
				else
				    	  if ( deltaX < 0 ) {
				                  mRenderer.moveLeft(x,y);
				    	  }
				    	  else {
				    		      mRenderer.moveRight(x,y);
				    	  }
					
					//mRenderer.mouseMoved(event);
													
			}
		}
		return super.onTouchEvent(event);
	}
	
	// Hides superclass method.
	public void setRenderer(Renderer renderer)//, float density) 
	{
		mRenderer = (Juego8Renderer)renderer;
		//mDensity = density;
		super.setRenderer(renderer);
	}
	
	public void onResume()
	{
		mRenderer.onResume();
		super.onResume();
	}
	
	public void onPause()
	{
		mRenderer.onPause();
		super.onPause();
	}
	
	public void onBackPressed()
	{
		mRenderer.onBackPressed();		
	}	

	 public void onSensorChanged(float[] values)
	 {
		 if ( mRenderer == null )
			  return;
		 
		 if ( values[1] < -10 ) {
			// mRenderer.moveForward(1);
		 }
		 else
			 if ( values[1] > 10 ) {
				// mRenderer.moveBackward(1);
			 }
			 else {
				 mRenderer.stopAction();
			 }
		 
		 if ( values[2] < -15 ) {
			// mRenderer.rotateLeft(15);
		 }
		 else
			 if ( values[2] > 15 ) {
				// mRenderer.rotateRight(15);
			 }
			 else {
				// mRenderer.stopAction();
			 }
	 }

}

