package cgvutils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;


public class ParticleSystem {

  public int   maxparticles;
  public float lifetime;
  public float psize;
  public float xspeed;
  public float yspeed;
  public float zspeed;
  public float ygravity;
  public float ydie;
  public float yspini, xspini, zspini;
  public long  time;
  public boolean bornagain;

  public FloatBuffer labV, labTV;
  public ShortBuffer labI1;


  int nbTexture;
  int lado=1;

  private Particle p[];
  public int textures[];

  public static Activity activity;
  public static GL10 gl;

  Pattern pat;

  public static void Init( GL10 xgl, Activity actv )
  {
       gl = xgl;
       activity = actv;

   }

  public ParticleSystem(int maxp)
    {
      gl.glEnable( GL10.GL_BLEND );    
      
      maxparticles = maxp;
      p = new Particle[maxp];
          for( int i=0; i < maxparticles; i++ )
        	  p[i] = createParticle();

      lifetime = 300;
      psize = 0.1f;
      xspeed = 500.0f;
      yspeed = 1000f;
      zspeed = 500.0f;
      ygravity = -0.00004f;
      xspini = 0;
      yspini = 0.008f;
      zspini = 0;
      time = 0;
      ydie = 0;
      bornagain = true;
      
	  ByteBuffer vbb = ByteBuffer.allocateDirect(4*3*4);	
	  vbb.order(ByteOrder.nativeOrder());
      labV = vbb.asFloatBuffer();     
      
	  vbb = ByteBuffer.allocateDirect(4*2*4);	
	  vbb.order(ByteOrder.nativeOrder());
      labTV = vbb.asFloatBuffer();
     
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

      setColor( 255, 255, 0, 255, 1 );
      
      pat = null;
    }


  public void  setColor( int r, int g, int b, int alpha, int nt )
  {
      byte bb[];
      int tsize = 128;

      //int dtalpha;
      if ( nt < 1 || nt > 10 )
          return;
      gl.glEnable( GL10.GL_TEXTURE_2D );
      nbTexture = nt;
      textures = new int[ nbTexture ];
      gl.glGenTextures( nbTexture, textures, 0 );
      //dtalpha = alpha/nt;
      for (int i = 0; i < nbTexture; i++ )
      {
          bb = calcTextureData( tsize, 4,
                  r-r/nt*i,
                  g-g/nt*i,
                  b-b/nt*i,
                  4f,
                  alpha/*-dtalpha*i*/ );
          initTexture( gl, bb, i, tsize );
      }

  }

  public void setPattern( byte bpat[] )
  {
	    if ( bpat != null )
	    	pat = new Pattern(bpat);
	    else
	    	pat = null;
  }

  public void setPattern( int rcid )
  {
    	pat = new Pattern(rcid);
  }
  
  private byte[] calcTextureData( int size, int bytesPerPixel,
		  int r, int g, int b, 
		  float alphaScale, int maxAlpha  )
  {      
	  int halfSize = size/2;
	  int nbBytes = size*size*bytesPerPixel;
	  int nbBytesRow = size*bytesPerPixel;
	  byte[] data = new byte[nbBytes];
	  for(int i=0; i < nbBytes; i+=bytesPerPixel){
		  data[i]   = (byte)r;
		  data[i+1] = (byte)g;
		  data[i+2] = (byte)b;
		  //data[i+3] = (byte)255; // Calc below.
	  }      

	  
	  for(int y=0; y < size; y++) {
		  for(int x=0; x < size; x++) {
			  if ( pat == null || pat.getBit(x, y) ) {
				  int dx = x - halfSize;
				  int dy = y - halfSize;
				  int a = maxAlpha - 
						  (int) ( Math.sqrt((double)(dx*dx+dy*dy)) 
					  * alphaScale );
				  if (a < 0) { a = 0; }
				  data[ y * nbBytesRow + x * bytesPerPixel + 3 ] = (byte)a;
			  }
			  else {
				  data[ y * nbBytesRow + x * bytesPerPixel + 3 ] = 0;
	  }
  }
}	
return data;
}

  private void initTexture( GL10 gl, byte b[], int index, int size )
  {
    gl.glBindTexture( GL10.GL_TEXTURE_2D, textures[ index ] );
    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
    // Set texture data.
    ByteBuffer texture = ByteBuffer.allocateDirect(b.length);	
	  texture.order(ByteOrder.nativeOrder());
    texture.put( b, 0, b.length );
    gl.glTexImage2D( GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, size, size, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, texture.rewind() ) ;
  }


  private Particle createParticle()
  {
      Particle np = new Particle( lifetime, 0f, psize );
      resetParticle(np);
      return np;
  }
  
  private void resetParticle( Particle np )
  {
      int sig;
      np.reset(lifetime, 0f, psize);
      sig = Math.random() < 0.5 ? 1 : -1;
      np.setSpeed( // Init random Speed.
	  xspini-(float)Math.random() /xspeed * sig,
      yspini-(float)Math.random() /yspeed * sig,
      zspini-(float)Math.random() /zspeed* sig  );
  }

  private void resetParticle( Particle np, int sig )
  {
      np.reset(lifetime, 0f, psize);
      np.setSpeed( // Init random Speed.
	  xspini-(float)Math.random() /xspeed * sig,
      yspini-(float)Math.random() /yspeed * sig,
      zspini-(float)Math.random() /zspeed* sig  );
  }
  
  public void reset()
  {
      for( int i=0; i < maxparticles; i++ )
          resetParticle( p[i], i%2 == 0 ? -1 : 1 );
      time = 0;
  }
  
  public void draw()
    {

      time++;

      gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE);
      gl.glDepthMask( false );

      gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
      gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
      gl.glVertexPointer(3, GL10.GL_FLOAT, 0, labV); 
      gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, labTV );
      
      for( int i=0; i < maxparticles; i++ )
      {
    	  if ( p[i].getPosY() < ydie || ! p[i].isAlive() )
    	  {
    		  if ( bornagain )
    			  resetParticle(p[i]);
    		  else {
    		  }
    	  }
    	  else {
    		  p[i].incSpeedY( ygravity );
    		  p[i].evolve();

    		  int tltime = (int)lifetime/nbTexture;
    		  int tidx = (int)(lifetime-p[i].getLifetime())/tltime;
    		  if ( tidx >= nbTexture || tidx < 0 ) tidx = nbTexture-1;
    		  gl.glBindTexture( GL10.GL_TEXTURE_2D,textures[tidx] );
    		  p[i].draw( gl );
    	  }
      } // end particle loop.
      gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
      gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
      
      gl.glDepthMask( true );
      gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    }

  private class Pattern {
	  final int w = 128;
	  final int h = 128;
	  final int wbytes = w/8;
	  public byte  bytes[];
	  
	  public Pattern(byte pat[])
	  {
		  bytes = pat;
	  }
	  
	  public Pattern( int rcid )
	  {
		    bytes = new byte[wbytes*h];
			InputStream is = activity.getResources().openRawResource(rcid); 
			Bitmap img = BitmapFactory.decodeStream(is);
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			for ( int i = 0; i < 128; i++ )
				for ( int j = 0; j < 128; j++ )
				{
					int pix = img.getPixel(j, i);
					setBit( j, i, pix != Color.BLACK );
				}
		  
	  }
	  
	  public void setBit( int x, int y, boolean v )
	  {
		  byte b;
		  int bidx;
		  bidx = y*wbytes + x/8;
		  
		  b = bytes[bidx];
		  
		  int bit = x % 8;
		  byte vb = (byte) (1<<(7-bit)); 
		  if ( v )
			  b |= vb;
		  else
			  b &= ~vb;
		  bytes[bidx] = b;
	  }
	  
	  public boolean getBit( int x, int y )
	  {
		  byte b;
		  b = bytes[ y*wbytes + x/8 ];
		  int bit = x % 8;
		  return ( b & 1<<(7-bit)) != 0;
	  }
  }

  public class Particle 
  {
	  public static final int X = 0;
	  public static final int Y = 1;
	  public static final int Z = 2;

	  private float lifetime = 100f;
	  private float decay = 1f;
	  // The pariticls resites inside a rectangle.
	  private float size = 0.4f;

	  private float pos[] = {0.0f, 0.0f, 0.0f};
	  private float speed[] = {0.0f, 0.0f, 0.0f};

	  Particle( float lifetime, float decay, float size )
	  {
		    reset(lifetime,decay,size);
	  }

	  public void reset(float lifetime, float decay, float size)
	  {
	      if( lifetime != 0) { this.lifetime = lifetime; }
	      if( decay != 0) { this.decay = decay; }
	      if( size != 0) { this.size = size; }
	      pos[X] = pos[Y] = pos[Z] = 0f;
	  }
	  
	  public float getLifetime() { return lifetime; }

	  public float getPosX() { return pos[X]; }
	  public float getPosY() { return pos[Y]; }
	  public float getPosZ() { return pos[Z]; }

	  public float getSpeedX() { return speed[X]; }
	  public float getSpeedY() { return speed[Y]; }
	  public float getSpeedZ() { return speed[Z]; }

	  public void setSpeed( float sx, float sy, float sz ) 
	  { 
	      speed[X] = sx;
	      speed[Y] = sy;
	      speed[Z] = sz;
	  }

	  public void incSpeedX( float ds ) { speed[X] += ds; }
	  public void incSpeedY( float ds ) { speed[Y] += ds; }
	  public void incSpeedZ( float ds ) { speed[Z] += ds; }

	  public boolean isAlive() { return (lifetime > 0.0); }

	  public void evolve()
	    {
	      lifetime -= decay;
	      // Update locaton.
	      for(int i=0; i<3; i++)
	    	  pos[i] += speed[i];
	    }

	  public void draw( GL10 gl )
	    { 
	      final float halfSize = size / 2f;
	      final float x = pos[X]-halfSize;
	      final float y = pos[Y]-halfSize;
	      final float xs = pos[X]+halfSize;
	      final float ys = pos[Y]+halfSize;
	      
	      labV.position(0);
		  labV.put(x); labV.put(y); labV.put(pos[Z]);
		  labV.put(xs); labV.put(y); labV.put(pos[Z]);
		  labV.put(xs); labV.put(ys); labV.put(pos[Z]);
		  labV.put(x); labV.put(ys); labV.put(pos[Z]);
			
		  labV.position(0);
		  labTV.position(0);
		  labI1.position(0);
		  gl.glDrawElements( GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_SHORT, labI1 );
	    }
	}
}
