package com.cixlabs.njuego8;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
 
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.cixlabs.njuego8.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.opengl.GLES10;
import android.opengl.GLU;
import android.opengl.GLUtils;
//import android.opengl.GLES10;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

import cgvutils.*;

/**
 * This class implements our custom renderer. Note that the GL10 parameter passed in is unused for OpenGL ES 2.0
 * renderers -- the static class GLES20 is used instead.
 */
public class Juego8Renderer implements GLSurfaceView.Renderer 
{

	public Activity activity;
	
	float v_x, v_y, v_z;

	int fs;
	
	String objetivos[];
	int    objetivo;

	public GL10 gl;
    
    FloatBuffer labV, labTV, labNV, labFTV;
    ShortBuffer labI1;
    
	int _width=320, _height=480;
    
    int lastAction;

    int inipos;     
    
    boolean dataloaded;
    
    Model3D obj;
    
    boolean onpaused;
    
    Problema P;
    
    double springDT;
    float springX, springY;
    SpringODE spring;
    
    Canvas canvas;
    Paint  paint;
    Bitmap bitmap;
    
    long ltime, now;
    
    /*double mass = 1;
    double mu = 0.5;
    double k = 20;
    double x0 = 0.4;*/

    double mass = 2;
    double mu = 0.2f;//flexibilidad: 0.5 mayor..menos flexibilidad
    double k = 20;
    double x0 = 8;//empuje inicial
    double x0real;
    
    float springRot[];
    float lastspringRot[], springInterpol, deltaInterpol;
    
    float rhh = 2.777777f;
    float rvh = 1.55838f;
    int celw; 
    
    float zlight;
    
    boolean vertical;
    
    boolean moving;
    
    boolean scrambling, solving;
    Estado scramble;
    int    solvidx;
    
    float texfondo, rotfondo;
    
    ParticleSystem ps;
    boolean drawparticle;
    float partx, party;
    
    float onepix;
    
    box    tab, ob, scram, solut;

	
	MediaPlayer mptouch, mpshuf; 
	boolean mptouchplaying,mpshufplaying;
	boolean sound;

    
    public class box {
    	float x, y, z, w, h;
    	int   ix, iy, iw, ih;
    	
    	public boolean in( float _x, float _y )
    	{
    		return _x >= ix && _y >= iy && _x <= ix+iw && _y <= iy+ih;
    	}
    }
    
	public Juego8Renderer (Activity act)
	{	
		activity = act;
		ByteBuffer vbb = ByteBuffer.allocateDirect(4*3*4);	
		vbb.order(ByteOrder.nativeOrder());
        labV = vbb.asFloatBuffer();     
         
		vbb = ByteBuffer.allocateDirect(4*2*4);	
		vbb.order(ByteOrder.nativeOrder());
        labTV = vbb.asFloatBuffer();
        
		vbb = ByteBuffer.allocateDirect(4*3*4);	
		vbb.order(ByteOrder.nativeOrder());
        labNV = vbb.asFloatBuffer();
        labNV.position(0);

		vbb = ByteBuffer.allocateDirect(4*2*4);	
		vbb.order(ByteOrder.nativeOrder());
        labFTV = vbb.asFloatBuffer();
        
    	labTV.position(0);
    	labTV.put(0); labTV.put(0);
    	labTV.put(1); labTV.put(0);
    	labTV.put(1); labTV.put(1);
    	labTV.put(0); labTV.put(1);

    	
	    
        vbb = ByteBuffer.allocateDirect(6*2);
        vbb.order(ByteOrder.nativeOrder());
        labI1 = vbb.asShortBuffer();
        labI1.position(0);
        labI1.put((short)0); labI1.put((short)1); labI1.put((short)2);
        labI1.put((short)0); labI1.put((short)2); labI1.put((short)3);
        
        
	    v_x = 0;
        v_y = 0;
        // -1 vertical
        // -1.5;
        v_z = -0.9f;
        
//        zlight = 0.5f;
        zlight = 0.2f;
          
    	lastAction = -1;
    	
        dataloaded = false;

		Log.d(this.getClass().getName(),"constructor.." );
		
		onpaused = false;
		
        objetivos = new String[5];


        objetivos[0] = "012345678";
        objetivos[1] = "123456780";
        objetivos[2] = "123405678";
        objetivos[3] = "360258147";
        objetivos[4] = "470258136";

		Juego8Activity j8 = (Juego8Activity)act;
		String jugada;
		if ( j8.objetivo >= 0 ) {
			objetivo = j8.objetivo;
			jugada = j8.jugada.estado.toString();
		}
		else {
			objetivo = 0;
			jugada = "120345678";
		}
		P = new Problema(jugada, objetivos[objetivo] );
		if ( j8.objetivo < 0 )
			P.scramble(5);
        j8.objetivo = objetivo;
        j8.jugada = P;

        spring = new SpringODE(mass, mu, k, x0);
 	    spring.updatePositionAndVelocity(springDT);
 	    x0real = spring.getX();
        springDT = 0.05;// 0.05
        spring.setX0(0);
 	    spring.setQ(0, 1);
 	    
        springX = 0;
        springRot = new float[3];
        springRot[0] = 0;
        springRot[1] = 0;
        springRot[2] = 0;
        
        lastspringRot = new float[3];
        lastspringRot[0] = 0;
        lastspringRot[1] = 0;
        lastspringRot[2] = 0;
        
        springInterpol = 1;
        deltaInterpol = 0.1f;

        moving = false;
        
        scramble = new Estado();
        scramble.scramble();

        ob = new box();
        scram = new box();
        solut = new box();
        tab = new box();

        scrambling = solving = false;
    
        drawparticle = false;
        
        texfondo = 0;

        mptouch = null;
        mpshuf = null;
        createMPlayer();
        
        sound = true;

	}

	public void createMPlayer()
	{
		if ( mptouch != null )
			destroyMPlayer();
        mptouchplaying = mpshufplaying = false;
        mptouch = MediaPlayer.create(activity,R.raw.bip);
    	mpshuf = MediaPlayer.create(activity,R.raw.shuffle);
    	
    	mptouch.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { 
    	    public void onCompletion(MediaPlayer mp) { 
    	       // mp.stop();
    			mptouchplaying = false;
    	    } 
    	});
    	
    	mpshuf.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { 
    	    public void onCompletion(MediaPlayer mp) { 
    	       // mp.stop();
    			mpshufplaying = false;
    	    } 
    	});
	}
	
	public void destroyMPlayer()
	{
		if ( mptouch != null ) {
			mptouch.release();
			mpshuf.release();
		}
		mptouch = null;
		mpshuf = null;
		mptouchplaying = mpshufplaying = false;
	}
	
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) 
	{
		gl = glUnused;
		Log.d(this.getClass().getName(),"creando superficie.." );
		if ( dataloaded == false ) {
			loadModel();
		}
	
		Lights.Init(gl, activity);   

	    ParticleSystem.Init(gl, activity);
	    
	    ps = new ParticleSystem(5);
	    ps.psize = 0.6f;
	    ps.lifetime = 50;
	    ps.xspeed = 30;
	    ps.yspeed = 1000;//800
	    ps.zspeed = 100000;
	    ps.yspini = 0.03f;//0.02f;
	    ps.ygravity = -0.001f;//0008
	    ps.bornagain = false;
		
		Textures.Init(gl,  20, activity );
		loadTextures();

		gl.glClearColor(0,0,0.8f,1);
      /*
        float fogColor[] = {0.8f,0.8f,0.8f, 1.0f};
        
 		gl.glFogf(GL10.GL_FOG_MODE, GL10.GL_LINEAR); // Fog Mode GL_EXP, GL_EXP2, GL_LINEAR
    	gl.glFogfv(GL10.GL_FOG_COLOR, fogColor, 0); // Set Fog Color
    	gl.glFogf(GL10.GL_FOG_DENSITY, 0.3f); // How Dense Will The Fog Be
    	gl.glHint(GL10.GL_FOG_HINT, GL10.GL_DONT_CARE); // Fog Hint Value
    	gl.glFogf(GL10.GL_FOG_START, 1); // Fog Start Depth
    	gl.glFogf(GL10.GL_FOG_END, 7); // Fog End Depth
    	gl.glEnable(GL10.GL_FOG); // Enables GL_FOG
		*/
		gl.glEnable( GL10.GL_DEPTH_TEST );
		gl.glEnable(GL10.GL_NORMALIZE);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);   

	}	
	
	public void onSurfaceChanged(GL10 glUnused, int width, int height) 
	{
		gl = glUnused;
		
	    gl.glViewport(0, 0, width, height);
	    
        _width = width;
        _height = height;
	    float ratio = (float)_width / _height;
	    gl.glMatrixMode(GL10.GL_PROJECTION);
	    gl.glLoadIdentity();
	    
/*	    
	    float size = .01f * (float) Math.tan(Math.toRadians(30.0) / 2); 
	    gl.glFrustumf(-size, size, -size / ratio, size / ratio, 0.001f,200.0f);
*/
		GLU.gluPerspective(gl, 45.0f, ratio, 0.1f, 100.0f);  
	    
		Log.d(this.getClass().getName(),"superficie changed.." );

		vertical = _height > _width;
	    float areal;
	    float rat, rat2;
	   
		tab.w = tab.h = 3;

	//	Log.d(this.getClass().getName(),"width: " + _width + " height: " + _height );
		
		if ( vertical ) {
			onepix = rvh*3/_width;

			tab.iw = (int)(_width/rvh);
			
			rat = (float)_height/_width;
			rat2 = rat * rvh / 1.5145833f;
			v_z = rat2*-8.3f/rvh;

			
//			v_z = -0.9f; // glFrustumf
//			v_z = -8.3f; // gluPerspective
//			v_z = -9.2f; // gluPerspective, fullscreen
			
		    areal =_height/2.0f * tab.w / tab.iw;
		    
		    ob.w = 1.5f;		    
		    ob.y = areal - 0.2f - ob.w;
		    ob.x = -ob.w/2;
		    ob.z = v_z;
		    
		    ob.ih = ob.iw = (int)(tab.iw*ob.w/tab.w);
		    ob.iy = (int)(tab.iw*0.2f/tab.w);
		    ob.ix = (int)(_width-ob.iw)/2;
		    
		    scram.w = 1.8f;
		    scram.x = -1.5f;
		    scram.z = ob.z;
		    scram.h = 0.6f;
		    scram.y = -1.5f-0.3f-scram.h;

		    scram.iw = (int)(tab.iw*scram.w/tab.w);
		    scram.ih = (int)(tab.iw*scram.h/tab.w);
		    scram.ix = (int)(_width/2 + tab.iw*scram.x/tab.w);
		    scram.iy = (int)(_height/2 - tab.iw*scram.y/tab.w - scram.ih );
		    
		}
		else {
			onepix = rhh*3/_width;
					
		    tab.iw = (int)(_width/rhh);

		    rat = (float)_height/_width;
		    rat2 = rat * rhh / 0.50875f;
		    v_z = rat2 *-5.0f / rhh;

//		    v_z = -1.5f; // glFrustum
//			v_z = -5f; // gluPerspective
//			v_z = -5.9f; // gluPerspective, fullscreen

		    
		    areal =_width/2.0f * tab.w / tab.iw;
		    
		    ob.w = 1.5f;		    
		    ob.x = -areal + 0.5f;
		    ob.y = -ob.w/2;
		    ob.z = v_z;
		    
		    ob.ih = ob.iw = (int)(tab.iw*ob.w/tab.w);
		    ob.ix = (int)(tab.iw*0.5f/tab.w);
		    ob.iy = (int)(_height-ob.iw)/2;

		    scram.w = 1.8f;
		    scram.x = 1.5f+0.3f;
		    scram.z = ob.z;
		    scram.h = 0.6f;
		    scram.y = 1.5f-scram.h;
		    scram.iw = (int)(tab.iw*scram.w/tab.w);
		    scram.ih = (int)(tab.iw*scram.h/tab.w);
		    scram.ix = (int)(_width/2+ tab.iw*scram.x/tab.w);
		    scram.iy = (int)(_height/2 - tab.iw*scram.y/tab.w - scram.ih );
		    
		}
		tab.x = -1f;
		tab.y = -1f;
		tab.z = v_z;
		tab.ix = (int)((_width-tab.iw)/2);
		tab.iy = (int)((_height-tab.iw)/2);
		v_x = 0;
		v_y = 0;
		celw = tab.iw/3;
		
		solut.w = scram.w;
		solut.h = scram.h;
		solut.x = scram.x;
		solut.y = scram.y-0.8f;
		solut.z = scram.z;
		solut.iw = scram.iw;
		solut.ih = scram.ih;
		solut.ix = scram.ix;
		solut.iy = (int)(_height/2 - tab.iw*solut.y/tab.w - scram.ih );
		
		

	}	
	
	public void loadModel()
	{
		//Log.d(this.getClass().getName(),"cargando modelo.." );
		Model3DS.Init(gl, activity);
		obj = new Model3DS();
		obj.load( R.raw.uno, BP.CENTER, BP.TOP, BP.CENTER );
		if ( obj.isLoaded() ) {
				  obj.setXYZScale(0.9f,1f,0.9f);
				  obj.setRotate(1, 0, 0);
				  obj.setObjectTexture(0, 0);
			  }
		Model3DS.gl = gl;
		dataloaded = true;
	}
	
	void loadTextures() 
	{
		Textures.Load(R.drawable.a1);
		Textures.Load(R.drawable.a2);
		Textures.Load(R.drawable.a3);
		Textures.Load(R.drawable.a4);
		Textures.Load(R.drawable.a5);
		Textures.Load(R.drawable.a6);
		Textures.Load(R.drawable.a7);
		Textures.Load(R.drawable.a8);
		Textures.Load(R.drawable.fondo);
		
		String gText;
		float scale = 30f;
		Rect bounds = new Rect();
		RectF rect;
		rect  = new RectF(0,0,127,63);
		int x, y;

//		int clearpixel;
		
		bitmap = Bitmap.createBitmap(128, 64, Config.ARGB_8888 );
		bitmap = bitmap.copy(Config.ARGB_8888, true);
//		clearpixel = bitmap.getPixel(0, 0);
		canvas = new Canvas(bitmap);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize((int)scale); //scale
		
//		paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

		int wrect  = 129/3;
		for ( int i = 0; i < 3; i++ ) {
			if ( i == 0 )
			  gText = "E";
			else 
				if ( i == 1 ) 
					gText = "M";
				else
					gText = "H";// activity.getString(R.string.shuffle);
			paint.getTextBounds(gText, 0, gText.length(), bounds);
			rect.set(i*wrect+i, 0, i*wrect+wrect-3+i, 63);
			x = (int)rect.left + (wrect-3 - bounds.width())/2 - 1;
			y = (int)rect.top + (64 + bounds.height())/2;
			paint.setColor(Color.rgb(0,0,255));
			canvas.drawRoundRect(rect, 5,5, paint);
			paint.setColor(Color.rgb(255, 255, 0));
			canvas.drawText(gText, x, y, paint);
		}
		Textures.Load(bitmap); // 9

		rect.set(0,0,127,63);
		gText = activity.getString(R.string.solve);
		paint.getTextBounds(gText, 0, gText.length(), bounds);
		x = (bitmap.getWidth() - bounds.width())/2;
		y = (bitmap.getHeight() + bounds.height())/2;
	//	bitmap.eraseColor(clearpixel);
	//	canvas.drawARGB(50,0,0,255);
	//	canvas.drawRGB(0,0,255);
		paint.setColor(Color.rgb(0, 0, 255));
		canvas.drawRoundRect(rect, 5,5, paint);
		paint.setColor(Color.rgb(255, 255, 0));
		canvas.drawText(gText, x, y, paint);
		Textures.Load(bitmap); // 10

		gText = activity.getString(R.string.solved);
		paint.getTextBounds(gText, 0, gText.length(), bounds);
		x = (bitmap.getWidth() - bounds.width())/2;
		y = (bitmap.getHeight() + bounds.height())/2;
		paint.setColor(Color.rgb(255, 255, 0));
		canvas.drawRoundRect(rect, 5,5, paint);
		paint.setColor(Color.rgb(0, 0, 255));
		canvas.drawText(gText, x, y, paint);
		Textures.Load(bitmap); // 11
		
		ps.setPattern(R.drawable.patcross2);
        ps.setColor(150,150,0 , 255, 5);
	}
	
	void dibuja_boton( box b, int t )
	{
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

        gl.glNormalPointer(GL10.GL_FLOAT, 0, labNV );       
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, labV); 
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, labTV );
	
        Textures.Enable();
        
        //if ( t == 10 )
        	//gl.glColor4f(0.6f,0.6f,0.6f,1);
        //else
      	    gl.glColor4f(1,1,1,1);
        
		Textures.Select(t);
		labV.position(0);
		labV.put(b.x); labV.put(b.y+b.h); labV.put(b.z);
		labV.put(b.x+b.w); labV.put(b.y+b.h); labV.put(b.z);
		labV.put(b.x+b.w); labV.put(b.y); labV.put(b.z);
		labV.put(b.x); labV.put(b.y); labV.put(b.z);
      	
		labNV.position(0);
		labNV.put(0); labNV.put(0); labNV.put(-1);
		labNV.put(0); labNV.put(0); labNV.put(-1);
		labNV.put(0); labNV.put(0); labNV.put(-1);
		labNV.put(0); labNV.put(0); labNV.put(-1);
      
		labV.position(0);
		labTV.position(0);
		labNV.position(0);                	
		labI1.position(0);
		gl.glDrawElements( GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_SHORT, labI1 );
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		
	}
	
	void dibujaObjetivo( int idx, float obx, float oby, float obz )
	{
		int i, c;
		float x1, z1, cw, cd;
		String s;
		
		s = objetivos[idx];


        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

        gl.glNormalPointer(GL10.GL_FLOAT, 0, labNV );       
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, labV); 
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, labTV );
	
        Textures.Enable();
        
        if ( idx != objetivo )
        	gl.glColor4f(1,1,1,0.6f);
        else
        	gl.glColor4f(1,1,1,1);
        
        cw = ob.w/3;
        cd = cw*0.9f;
		for ( i = 0; i < 9; i++ )
		{
			x1 = (i % 3)*cw;
			z1 = (i / 3)*cw;
			c = s.codePointAt(i)  - 48; //e.codePointAt(i) - 48;		
			if ( c != 0 ) {
				Textures.Select(c-1);
				labV.position(0);
				labV.put(obx+x1); labV.put(oby+z1); labV.put(obz);
				labV.put(obx+x1+cd); labV.put(oby+z1); labV.put(obz);
				labV.put(obx+x1+cd); labV.put(oby+z1+cd); labV.put(obz);
				labV.put(obx+x1); labV.put(oby+z1+cd); labV.put(obz);
      	
				labNV.position(0);
				labNV.put(0); labNV.put(0); labNV.put(1);
				labNV.put(0); labNV.put(0); labNV.put(1);
				labNV.put(0); labNV.put(0); labNV.put(1);
				labNV.put(0); labNV.put(0); labNV.put(1);
      
				labV.position(0);
				labTV.position(0);
				labNV.position(0);                	
				labI1.position(0);
				gl.glDrawElements( GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_SHORT, labI1 ); 
			}
		 }


	      gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	      gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);	
	      
	      
	      Textures.Disable();
//	      gl.glColor4f(1,1,1,1);
	         
          gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);  
          
  		  for ( i = 0; i < 9; i++ )
  		  {
  			x1 = (i % 3)*cw;
  			z1 = (i / 3)*cw;
  			labV.position(0);
  			labV.put(obx+x1); labV.put(oby+z1); labV.put(obz);
  			labV.put(obx+x1+cd); labV.put(oby+z1); labV.put(obz);
  			labV.put(obx+x1+cd); labV.put(oby+z1+cd); labV.put(obz);
  			labV.put(obx+x1); labV.put(oby+z1+cd); labV.put(obz);
        	
        
  			labV.position(0);
  			gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, 4 ); 
  		  }
          
          
		  gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		  
	}
	
	
	void dibujaEstado( Estado s )
	{
		int x1, z1, i, c;

/*
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

        gl.glNormalPointer(GL10.GL_FLOAT, 0, labNV );       
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, labV); 
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, labTV );
	*/
        Textures.Enable();

		for ( i = 0; i < 9; i++ )
		  {
			x1 = i % 3;
			z1 = (i / 3);
			c = s.data[i] - 48; //e.codePointAt(i) - 48;
			if ( c != 0 ) {
				obj.setObjectTexture(0, c-1);
				obj.draw( tab.x+x1, tab.y+z1, 0, 90 );
			}
		  }
			
		/*	if ( c != 0 ) {
			Textures.Select(c-1);
        	labV.position(0);
        	labV.put(-1+x1-0.5f); labV.put(-1+z1-0.5f); labV.put(0);
        	labV.put(-1+x1+1-0.5f); labV.put(-1+z1-0.5f); labV.put(0);
        	labV.put(-1+x1+1-0.5f); labV.put(-1+z1+1-0.5f); labV.put(0);
        	labV.put(-1+x1-0.5f); labV.put(-1+z1+1-0.5f); labV.put(0);
        	
            labNV.position(0);
            labNV.put(0); labNV.put(0); labNV.put(1);
            labNV.put(0); labNV.put(0); labNV.put(1);
            labNV.put(0); labNV.put(0); labNV.put(1);
            labNV.put(0); labNV.put(0); labNV.put(1);
        
        	labV.position(0);
        	labTV.position(0);
        	labNV.position(0);                	
        	labI1.position(0);
        	gl.glDrawElements( GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_SHORT, labI1 ); 
			}*/
			
		
	/*      gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	      gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	      gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);*/
	  		
	}

	public void onDrawFrame(GL10 glUnused) 
	{
	    int i, j;
	     	
	    if ( onpaused ) {
			//Log.d(this.getClass().getName(),"paused.." );
			return;
	    }
       
        if ( !dataloaded ) {

    		gl = glUnused;
            gl.glClearColor(0.5f,0,0,1);
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            ltime = System.currentTimeMillis();
            return;
            
        }

		gl = glUnused;
        gl.glClearColor(0,0,0.4f,1);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
     

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        
//        gl.glRotatef(rotfondo, 0,0,1);
//        rotfondo += 0.1f;

        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

        gl.glNormalPointer(GL10.GL_FLOAT, 0, labNV ); 
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, labV); 
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, labFTV );
	
//        Lights.Disable();
        Textures.Enable();
        
        Lights.Enable();
        Lights.Turn(0, true);
        
        if ( vertical )
           Lights.Pos(0, 0, texfondo , tab.z+0.5f );
        else
           Lights.Pos(0, texfondo, 0 , tab.z+0.5f );
        	
        texfondo += 0.01f;
		if ( texfondo >= 10 )
			texfondo = -10;
        
        //Lights.NoAttenuation(0);
        Lights.Attenuation(0,0.07f);

        int a = 5;
        gl.glColor4f(1,1,1,1);
		Textures.Select(8);

		labNV.position(0);
		labNV.put(0); labNV.put(0); labNV.put(1f);
		labNV.put(0); labNV.put(0); labNV.put(1f);
		labNV.put(0); labNV.put(0); labNV.put(1f);
		labNV.put(0); labNV.put(0); labNV.put(1f);

		labFTV.position(0);
		labFTV.put(0); labFTV.put(0);
		labFTV.put(0+1); labFTV.put(0);
		labFTV.put(0+1); labFTV.put(1);
		labFTV.put(0); labFTV.put(1);
	    //	texfondo+=0.002f;
/*					if ( texfondo >= 3 )
						texfondo = 0;*/
		
		for ( i = -a; i <= a; i++ ) {
			for ( j = -a; j <= a; j++ ) {
				labV.position(0);
				labV.put(j); labV.put(i); labV.put(tab.z-1);
				labV.put(j+1); labV.put(i); labV.put(tab.z-1);
				labV.put(j+1); labV.put(i+1); labV.put(tab.z-1);
				labV.put(j); labV.put(i+1); labV.put(tab.z-1);

				labNV.position(0);
				labV.position(0);
				labFTV.position(0);
				labI1.position(0);
				gl.glDrawElements( GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_SHORT, labI1 );
			}
		}
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	    gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
        
        gl.glLoadIdentity();
    	gl.glTranslatef( v_x, v_y, tab.z );
    	
    	float rx, ry, rz, sx;
    	
        Lights.Enable();
        Lights.Turn(0, true);
        Lights.Pos(0, 0, 0 , 1 ); //zlight );
        if ( solving && solvidx < 0 )
           Lights.Attenuation(0,0.8f);
        else
           Lights.NoAttenuation(0);
    	
    	if ( springInterpol >= 1 ) { 
        	   spring.updatePositionAndVelocity(springDT);
        	   sx = springX = (float)spring.getX();
       		   deltaInterpol = 0;
       		   rx = springRot[0];
       		   ry = springRot[1];
       		   rz = springRot[2];
    	}
    	else { //interpolar el vector de rotacion
    		sx = (float)(springX + springInterpol*(x0real-springX));
    		springInterpol += deltaInterpol;
    		if ( springInterpol > 1 )
    			springInterpol = 1;
    		rx = lastspringRot[0] + springInterpol*(springRot[0]-lastspringRot[0]);
    		ry = lastspringRot[1] + springInterpol*(springRot[1]-lastspringRot[1]);
    		rz = lastspringRot[2] + springInterpol*(springRot[2]-lastspringRot[2]);
    	}
	//	Log.d(this.getClass().getName(),"sx = " + sx + " "  + rx + " " + ry + " " + rz);
    	if ( rx != 0 || ry != 0 || rz != 0 )
     	       gl.glRotatef( sx, rx, ry, rz );
    	
        

        if ( scrambling ) {
        	/*
        	scramble.scramble();
        	if ( P.has_solution( scramble, P.objetivo ) )
        		scrambling = false;
        	P.estado.set(scramble);
        	*/
        	//P.scramble(5);
        	scrambling = false;
        }
        
        if ( solving && solvidx >= 0 ) {
            now = System.currentTimeMillis();
            if ( now-ltime >= 500 ) {
                ltime = now;
            	if ( P.solution[solvidx] != -1  )
            		P.do_action(P.solution[solvidx++] );
               	else
               		solving = false;
        	}
        }

        
        gl.glColor4f(1,1,1, 1.0f);
        dibujaEstado( P.estado );
               
        gl.glLoadIdentity();
        Lights.Pos(0, ob.x+ob.w/2, ob.y+ob.w/2, ob.z+zlight);
        Lights.Attenuation(0,0.9f);   

       
        i = objetivo-1;
        j = objetivo+1;
        
        if ( vertical ) {
        	if ( i >= 0 ) dibujaObjetivo( i, ob.x-ob.w, ob.y, ob.z );
        	dibujaObjetivo( objetivo, ob.x, ob.y, ob.z );
        	if ( j < objetivos.length ) dibujaObjetivo( j, ob.x+ob.w, ob.y, ob.z );
        }
        else {
        	if ( i >= 0 ) dibujaObjetivo( i, ob.x, ob.y-ob.w, ob.z );
        	dibujaObjetivo( objetivo, ob.x, ob.y, ob.z );
        	if ( j < objetivos.length ) dibujaObjetivo( j, ob.x, ob.y+ob.w, ob.z );
        }
        
        gl.glLoadIdentity();
        Lights.Pos(0, scram.x+scram.w/2, (scram.y+scram.h+solut.y)/2, scram.z+1);
        Lights.Attenuation(0,0.9f);
        dibuja_boton(scram,9);
        if ( P.estado.equals(P.objetivo ) ) 
        	dibuja_boton(solut,11);
        else
        	dibuja_boton(solut,10);

        if ( drawparticle ) {
        	gl.glLoadIdentity();
        	gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);
        	Lights.Disable();
        	gl.glColor4f(1,1,1,1);
        	gl.glTranslatef(partx, party, tab.z );
        	ps.draw();
        	if ( ps.time >= ps.lifetime ) {
        		drawparticle = false;
        	}
        }
        
	}	

	public void make_touchsound()
	{
		if ( sound )
			if ( !mptouchplaying ) {
				mptouchplaying = true;
				mptouch.start();
			}
	}
	
	public void make_shufflesound()
	{
		if ( sound )
			if ( !mpshufplaying ) {
				mpshufplaying = true;
				mpshuf.start();
			}
	}
	
	public int getPos( float x, float y )
	{
		   int pos;
		   

		   ps.reset();
		   partx = (x -_width/2)*onepix;
		   party = -(y - _height/2)*onepix;
		   drawparticle = true;
		   
		   int yidx, xidx;
		   
		   x = x - tab.ix;
		   y = y - tab.iy;
		   
		   if ( x < 0 || y < 0 ) {
			   /*if ( x < 0 && y > 0 )
				   zlight += 0.05;
			   else
				   zlight -= 0.05;
			   Log.d(this.getClass().getName(),"zlight " + zlight);*/
			   return -1;
		   }
		   
		   yidx = (int)(y / celw);
		   xidx = (int)(x / celw);
		   if ( yidx > 2 || xidx > 2 )
			   return -1;
		   
		   pos = (2-yidx) * 3 + xidx;
		   return pos;
	}

	public void make_move( int pos  )
	{

		
	   if ( scrambling || solving )
		   return;
	   
	   char cell = P.estado.data[pos];
	   if ( cell == '0' )
		   return;

	   P.do_action(cell);

	   //   Log.d(this.getClass().getName(),"pos " + pos);

		if ( pos == 4 ) // centro?
		   return;

		spring.setX0(x0);
		spring.setQ(x0, 1);
		spring.updatePositionAndVelocity(springDT);
		x0real = spring.getX();
		   
		lastspringRot[0] = springRot[0];
		lastspringRot[1] = springRot[1];
		lastspringRot[2] = springRot[2];
		
		   
		deltaInterpol = 0.1f;
		springInterpol = 0;	   
	   
		switch ( pos ) {
	  	      case 6: springRot[0] = -1;  springRot[1] = -1; springRot[2] = 1; break;
	  	      case 7: springRot[0] = -1;  springRot[1] = 0; springRot[2] = 0; break;
	  	      case 8: springRot[0] = -1;  springRot[1] = 1; springRot[2] = -1; break;
	  	      case 3: springRot[0] = 0;  springRot[1] = -1; springRot[2] = 0; break;
	  	      case 4: springRot[0] = 0;  springRot[1] = 1; springRot[2] = 0; break;
	  	      case 5: springRot[0] = 0;  springRot[1] = 1; springRot[2] = 0; break;
	  	      case 0: springRot[0] = 1;  springRot[1] = -1; springRot[2] = 1; break;
	  	      case 1: springRot[0] = 1;  springRot[1] = 0; springRot[2] = 0; break;
	  	      case 2: springRot[0] = 1;  springRot[1] = 1; springRot[2] = -1; break;
	  	      default: springRot[0] = 0;  springRot[1] = 1; springRot[2] = 0; break;
		}
		//	Log.d(this.getClass().getName(),"pos.... "+ pos );
	}

	public boolean check_buttons(float x, float y)
	{
		if ( scram.in(x,y) ) { 
			if ( !scrambling && !solving ) {
			   make_shufflesound();
			   int bidx = (int)(x - scram.ix) / (scram.iw/3);
			   if ( bidx == 0 ) 
				   P.scramble(10);
			   else
				   if ( bidx == 1 )
					   P.scramble(15);
				   else
					   P.scramble(25);
					   
			  // scrambling = true;
		     /*   MediaPlayer mp = MediaPlayer.create(activity,R.raw.shuffle);
				mp.start();
				
				mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { 
				    public void onCompletion(MediaPlayer mp) { 
				        mp.stop(); 
				    } 
				}); */

				
		   }
		   return true;
		}
		   
		if ( solut.in(x,y) ) {
			if ( !scrambling && !solving ) {
			   solving = true;
			   solvidx = -1;
			   if ( P.find_solution() ) {
				   solvidx = 0;
			   }
		   }
		   return true;
		}
		return false;
	}
	
	public void touch_up( float x, float y )
	{
	   moving = false;
		
/*	   if ( x >= iobx && y >= ioby && x <= iobx+iobw && y <= ioby+iobw )
	   {
		   objetivo = (objetivo+1) % objetivos.length;
		   P.set_objetivo( objetivos[objetivo]);
		   return;
 	   }*/
		
	   if ( check_buttons(x,y) )
		   return;
	   
	   int pos = getPos(x,y);
	   if ( pos < 0 )
		   return;
	   make_move(pos);
	   inipos = -1;
	}

	public void touch_down( float x, float y )
	{
	   make_touchsound();
		
	   inipos = getPos(x,y);
	   moving = false;
	}
	
	public void check_movedirection( float x, float y, int aa )
	{
	    moving = true;
	    
		if ( inipos < 0 )
			return;
		
	    int pos = getPos(x,y);
	    if ( pos < 0 ) return;
	       
	    char cell = P.estado.data[pos];
		if ( cell == '0' ) 
			cell = P.estado.data[pos = inipos];
		else
			if ( pos != inipos )
				return;

	    int a = P.find_action( P.estado, cell );
	    if ( a == aa ) 
	       make_move(pos);
	    
	}
	
	public void moveUp(float x, float y)
	{
		if ( scrambling || solving ) { moving = true; return; }

		make_touchsound();

		if ( check_buttons(x,y) )
			   return;
		
		if ( ob.in(x,y) )
		   {
			   if ( objetivo > 0 && !moving ) {
				   objetivo = objetivo-1;
				   ((Juego8Activity)activity).objetivo = objetivo;
				   P.set_objetivo( objetivos[objetivo]);
			   }
			   moving = true;
			   return;
	 	}		
		check_movedirection(x,y,3);
		
	}
	
	public void moveDown(float x, float y)
	{
		if ( scrambling || solving ) { moving = true; return; }

 	    make_touchsound();
		
		if ( check_buttons(x,y) )
			   return;

		if ( ob.in(x,y) )
		   {
			   if ( objetivo < objetivos.length-1 && !moving ) {
				   objetivo = objetivo+1;
				   ((Juego8Activity)activity).objetivo = objetivo;
				   P.set_objetivo( objetivos[objetivo]);
			   }
			   moving = true;
			   return;
	 	}		
		check_movedirection(x,y,1);
	}
	
	public void moveRight(float x, float y)
	{
		if ( scrambling || solving ) { moving = true; return; }
		
	    make_touchsound();
		
		if ( check_buttons(x,y) )
			   return;

		if ( ob.in(x,y) )
		   {
			   if ( objetivo > 0 && !moving ) {
				   objetivo = objetivo-1;
				   ((Juego8Activity)activity).objetivo = objetivo;
				   P.set_objetivo( objetivos[objetivo]);
			   }
			   moving = true;
			   return;
	 	}		
		check_movedirection(x,y,0);
	}		
	
	public void moveLeft(float x, float y)
	{
		if ( scrambling || solving ) { moving = true; return; }
		
	    make_touchsound();

		if ( check_buttons(x,y) )
			   return;
		
		if ( ob.in(x,y) )
		   {
			   if ( objetivo < objetivos.length-1 && !moving ) {
				   objetivo = objetivo+1;
				   ((Juego8Activity)activity).objetivo = objetivo;
				   P.set_objetivo( objetivos[objetivo]);
			   }
			   moving = true;
			   return;
	 	}		
		check_movedirection(x,y,2);
	}	
	
		
	public void stopAction()
	{
		
	}
	
    public void onPause()
    {
		Log.d(this.getClass().getName(),"pausing....." );
		destroyMPlayer();
		onpaused = true;
    }
    
    
    public void onResume()
    {
		Log.d(this.getClass().getName(),"resuming....." );
		createMPlayer();
		onpaused = false;
    }
    
    public void onBackPressed()
    {
		Log.d(this.getClass().getName(),"back....." );
		destroyMPlayer();
    }
    
}

