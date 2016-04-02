/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cgvutils;

import javax.microedition.khronos.opengles.GL10;
import android.app.Activity;
/**
 *
 * @author ealonso
 */
public class Lights {
  // private attributes

  private static float nvec1[];
  private static float nvec2[];
  private static float nnorm[];
  public static GL10 gl;

  public static int lgl[];
  public static float ldata[][];
  public static Activity activity;
  // constructor


  public static void Init(GL10 xgl, Activity actv)
  {
	  int i;
	  
      gl = xgl;
      activity = actv;
       lgl = new int[8];
       lgl[0] = GL10.GL_LIGHT0;
       lgl[1] = GL10.GL_LIGHT1;
       lgl[2] = GL10.GL_LIGHT2;
       lgl[3] = GL10.GL_LIGHT3;
       lgl[4] = GL10.GL_LIGHT4;
       lgl[5] = GL10.GL_LIGHT5;
       lgl[6] = GL10.GL_LIGHT6;
       lgl[7] = GL10.GL_LIGHT7;

       ldata = new float[8][16];
       for (i=0; i<8; i++ ) {
           ldata[i][0] = 0; ldata[i][1] = 0; ldata[i][2] = 0; ldata[i][3] = 1;
           ldata[i][4] = 0; ldata[i][5] = 0; ldata[i][6] = 0; ldata[i][7] = 1;
           ldata[i][8] = 1; ldata[i][9] = 1; ldata[i][10] = 1; ldata[i][11] = 1;
           ldata[i][12] = 1; ldata[i][13] = 1; ldata[i][14] = 1; ldata[i][15] = 1;
       }

       gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 1);
       gl.glEnable(GL10.GL_COLOR_MATERIAL);
       gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_SPECULAR, ldata[0], 12 );
       gl.glMaterialf(GL10.GL_FRONT,GL10.GL_SHININESS,20);

   	   for ( i = 0; i < 8; i++ )
	   {
	     gl.glLightfv( lgl[i], GL10.GL_DIFFUSE, ldata[i],8);
	     gl.glLightfv( lgl[i], GL10.GL_POSITION, ldata[i],0);
	   }

       nvec1 = new float[3];
       nvec2 = new float[3];
       nnorm = new float[3];

       Turn( 0, true );
       gl.glEnable( GL10.GL_NORMALIZE );

  }

  public static void Enable()
  {
      gl.glEnable( GL10.GL_LIGHTING );
  }

  public static void Disable()
  {
      gl.glDisable( GL10.GL_LIGHTING );
  }

  public static void Turn( int i, boolean on )
  {
      if ( on )
         gl.glEnable( lgl[i] );
      else
         gl.glDisable( lgl[i] );
  }
  public static void Pos( int i, float x, float y, float z )
  {
      ldata[i][0] = x;
      ldata[i][1] = y;
      ldata[i][2] = z;
      gl.glLightfv( lgl[i], GL10.GL_POSITION, ldata[i], 0);
  }

  public static void Color( int i, float r, float g, float b )
  {
      ldata[i][8+0] = r;
      ldata[i][8+1] = g;
      ldata[i][8+2] = b;
      gl.glLightfv( lgl[i], GL10.GL_DIFFUSE, ldata[i], 8);
  }

  public static void Specular( float r, float g, float b )
  {
      int i = 0;
      ldata[i][12+0] = r;
      ldata[i][12+1] = g;
      ldata[i][12+2] = b;
      gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_SPECULAR, ldata[i], 12);
  }

  public static void Shininess( int s )
  {
	  gl.glMaterialf(GL10.GL_FRONT,GL10.GL_SHININESS,s);
  }
  
  public static void NoAttenuation( int i )
  {
	  gl.glLightf( lgl[i] ,GL10.GL_CONSTANT_ATTENUATION , 1 );
	  gl.glLightf( lgl[i], GL10.GL_LINEAR_ATTENUATION , 0 );
	  gl.glLightf( lgl[i], GL10.GL_QUADRATIC_ATTENUATION , 0 );
  }

  public static void Attenuation( int i, float a )
  {
	  gl.glLightf( lgl[i] ,GL10.GL_CONSTANT_ATTENUATION , 0 );
	  gl.glLightf( lgl[i], GL10.GL_LINEAR_ATTENUATION , 0 );
	  gl.glLightf( lgl[i], GL10.GL_QUADRATIC_ATTENUATION , a );
  }

  public static void Vector(float x1, float y1, float z1,
				 float x2, float y2, float z2, float vec[])
  {
	vec[0] = x1-x2;
	vec[1] = y1-y2;
	vec[2] = z1-z2;
  }

  public static void Cross(float vec1[], float vec2[], float cross[] )
  {
	cross[0] = ((vec1[1] * vec2[2]) - (vec1[2] * vec2[1]));
	cross[1] = ((vec1[2] * vec2[0]) - (vec1[0] * vec2[2]));
	cross[2] = ((vec1[0] * vec2[1]) - (vec1[1] * vec2[0]));
  }

  private static float val(float x)
  {
      if ( x > -0.000001f && x < 0.000001f )
          return 0;
      else
          return x;
  }

  public static void Normal(float x1, float y1, float z1,
				   float x2, float y2, float z2,
				   float x3, float y3, float z3 )
  {
     Vector( x1, y1, z1, x2, y2, z2, nvec1 );
     Vector( x2, y2, z2, x3, y3, z3, nvec2 );
     Cross( nvec1, nvec2, nnorm );
     gl.glNormal3f(val(nnorm[0]),val(nnorm[1]),val(nnorm[2]));
     //gl.glNormal3fv(nnorm,0);
  }

  public static void Normal(float x1, float y1, float z1,
				   float x2, float y2, float z2,
				   float x3, float y3, float z3,
				   float normal[]  )
  {
     Vector( x1, y1, z1, x2, y2, z2, nvec1 );
     Vector( x2, y2, z2, x3, y3, z3, nvec2 );
     if ( normal == null )
	    normal = nnorm;
     Cross( nvec1, nvec2, normal );
     //gl.glNormal3f(val(nnorm[0]),val(nnorm[1]),val(nnorm[2]));//este
     
     
     
//     gl.glNormal3fv(normal,0);
  }

  public static void Normal(float vec1[], float vec2[], float vec3[], float normal[])
  {
    Normal( vec1[0], vec1[1], vec1[2],
				 vec2[0], vec2[1], vec2[2],
				 vec3[0], vec3[1], vec3[2], normal );
  }

}
