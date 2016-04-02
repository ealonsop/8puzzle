/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cgvutils;

/**
 *
 * @author ealonso
 */

import java.io.InputStream;

import android.app.Activity;
import javax.microedition.khronos.opengles.GL10;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;


public class Textures {
  // private attributes

  public static Activity activity;
  public static GL10 gl;
  // constructor
  public static int texture[];
  public static int nt;

  public static void Init( GL10 xgl, int maxt, Activity actv )
  {
	  activity = actv;
      gl = xgl;
 
      texture = new int[maxt];
      nt = 0;
//      gl.glPolygonMode (GL10.GL_FRONT_AND_BACK, GL10.GL_FILL);
//      gl.glEnable (GL.GL_CULL_FACE);
//      gl.glShadeModel (GL.GL_SMOOTH);    
//      gl.glTexGeni(GL.GL_S, GL.GL_TEXTURE_GEN_MODE, GL.GL_SPHERE_MAP); 	// Set The Texture Generation Mode For S To Sphere Mapping
//      gl.glTexGeni(GL.GL_T, GL.GL_TEXTURE_GEN_MODE, GL.GL_SPHERE_MAP); 	// Set The Texture Generation Mode For T To Sphere Mapping
      gl.glEnable( GL10.GL_BLEND );
	// habilita y establece la operacion para el chequeo del color Alpha (transp)
      gl.glEnable( GL10.GL_ALPHA_TEST );
      gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

  }

  public static void Enable()
  {
        gl.glEnable(GL10.GL_TEXTURE_2D);
  }

  public static void Disable()
  {
        gl.glDisable(GL10.GL_TEXTURE_2D);
  }

  public static void Select(int i)
  {
    gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[i]);
  }

  public static void Select(int i, int texturea[] )
  {
    gl.glBindTexture(GL10.GL_TEXTURE_2D, texturea[i]);
  }

  public static int Load( int rcid )
  {
      if ( LoadTexture( rcid, nt, texture ) > 0 )
         return nt++;
      else
         return -1;
  }

  
public static int LoadTexture(int id, int tidx,  int texture[])
	{	
		gl.glGenTextures(1, texture, tidx);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[tidx]);
		
		InputStream is = activity.getResources().openRawResource(id); 
		 
		// Load the texture. 
		
	//	Bitmap img = BitmapFactory.decodeResource(activity.getResources(), id);
		Bitmap img = BitmapFactory.decodeStream(is);
		
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, img, 0); 
		
		return 1;
		
	}

public static int Load(Bitmap img)
{	
	gl.glGenTextures(1, texture, nt);
	gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[nt]);
	
	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, img, 0); 
	
	nt++;
	
	return 1;
	
}
  
}
