/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

// MD2 OBJECT

package cgvutils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import android.app.Activity;

/**
 *
 * @author ealonso
 */
public class ModelMD2 extends BasicModelMD2 implements Model3D {
    
    private Vec3 colors[];
    private boolean visibility[];
    private int usrtextures[];
    
    private boolean loaded = false;
    private boolean scaleset = false;
    private boolean rotateset = false;

    public static GL10 gl;

    private MD2_Frame frame;
    private int iani; 

    
    FloatBuffer vBuf, vtBuf, vnBuf;

    public static void Init(GL10 xgl, Activity actv)
    {
       gl = xgl;
       activity = actv;
    }


    private Vec3 scale, rotate, size, Osize, min, max;

    // Constructor
    public ModelMD2()
    {
        super();
        loadanims = null;
    }
    
    public ModelMD2( int anims[] )
    {
    	super();
    	loadanims = anims;
    }
    
    public boolean isLoaded()
    {
        return loaded;
    }

    public boolean load(int file )
    {
        return load(file,BP.CENTER,BP.CENTER,BP.CENTER);
    }

    private void calcDims()
    {
        scale =new Vec3();
        rotate =new Vec3();
        size =new Vec3();
        Osize=new Vec3();
        min=new Vec3();
        max=new Vec3();

        int primero;

        primero = 1;
        int i, j, k;

        for ( i=0; i< header.numVertices;i++ )
        {
            Vec3 v = frames[0].vertexes[i].vertex;
			if ( primero == 1)
			{
				min.set(v);
				max.set(v);
				primero = 0;
			}
			else
			{
				if ( min.x > v.x )
					min.x = v.x;
				if ( max.x < v.x )
					max.x = v.x;
				if ( min.y > v.y )
					min.y = v.y;
				if ( max.y < v.y )
					max.y = v.y;
				if ( max.z < v.z )
					max.z = v.z;
				if ( min.z > v.z )
					min.z = v.z;
			}
		//}
              //}
      }
      size.x = max.x - min.x;
      size.y = max.y - min.y;
      size.z = max.z - min.z;
      Osize.set(size);
    }

    private void moveObjects( float dx, float dy, float dz )
    {
        int i, j, k;

        for (i = 0; i < header.numFrames; i++ )
        	if ( frames[i] != null )
               for (j = 0; j < header.numVertices; j++ ) {
                  Vec3 v = frames[i].vertexes[j].vertex;
                  v.x += dx;
                  v.y += dy;
                  v.z += dz;
               }
        min.x += dx;
        max.x += dx;
        min.y += dy;
        max.y += dy;
        min.z += dz;
        max.z += dz;
    }
    
    public boolean load(int file, int x, int y, int z )
    {
        float dx, dy, dz;
        if ( x == BP.LEFT ) dx = 0; else
        if ( x == BP.RIGHT ) dx = 1; else dx = 0.5f;
        if ( y == BP.TOP ) dy = 1;  else
        if ( y == BP.DOWN ) dy = 0; else dy = 0.5f;
        if ( z == BP.FRONT ) dz = 1; else
        if ( z == BP.BACK )  dz = 0; else  dz = 0.5f;
        return load(file,dx,dy,dz);
    }

    public boolean load(int file, float x, float y, float z )
    {

         if (!super.Load(file))
              return false;



        loaded = true;
        
        calcDims();

        float dx, dy, dz;

        dx = -min.x - size.x * x;
        dy = -min.y - size.y * y;
        dz = -min.z - size.z * z;

        moveObjects(dx,dy,dz);

        //readTextures();

        if ( getNumberOfObjects() > 0 ) {
           colors = new Vec3[getNumberOfObjects()];
           visibility = new boolean[getNumberOfObjects()];
           usrtextures = new int[getNumberOfObjects()];
           for ( int i=0; i<getNumberOfObjects();i++ )
           {
               colors[i] = new Vec3(1,1,1);
               visibility[i] = true;
               usrtextures[i] = -1;
           }
        }
///

        iani = 0;
        frame = frames[ animations[0].sFrame ];

      /*
        MD2_Triangle t;
        int i, j;
        Vec3 v;

        for (i = 0; i < header.numTriangles; i++) {
            t = triangles[i];
            System.out.println(i + " = " + t);
            for (j = 0; j < 3; j++ ) {
               v = frame.vertexes[t.vertexIndexes[j]].vertex;
               System.out.print(v);
            }
            System.out.println();
        }
*/
        
        ByteBuffer vbb;
        
    	vbb = ByteBuffer.allocateDirect(50*3*4);	
        vbb.order(ByteOrder.nativeOrder());
        vBuf = vbb.asFloatBuffer();
        
    	vbb = ByteBuffer.allocateDirect(50*3*4);	
        vbb.order(ByteOrder.nativeOrder());
        vnBuf = vbb.asFloatBuffer();
        
    	vbb = ByteBuffer.allocateDirect(50*2*4);	
        vbb.order(ByteOrder.nativeOrder());
        vtBuf = vbb.asFloatBuffer();

        return loaded;
    }
    
    public void draw( float x, float y, float z, float r )
    {
	   gl.glPushMatrix();
	   gl.glTranslatef( x, y, z );
	   if ( rotateset )
	      gl.glRotatef( r, rotate.x, rotate.y, rotate.z );
	   if ( scaleset )
	      gl.glScalef( scale.x, scale.y, scale.z );
            renderCmd();
	   gl.glPopMatrix();
    }

    public void draw( float x, float y, float z )
    {
	   gl.glPushMatrix();
	   gl.glTranslatef( x, y, z );
	   if ( scaleset )
	      gl.glScalef( scale.x, scale.y, scale.z );
           renderCmd();
	   gl.glPopMatrix();
    }

    public void draw( int iani, int iframe, float x, float y, float z)
    {
           this.iani = iani;
           frame = frames[ animations[iani].sFrame + iframe ];
           draw(x,y,z);
    }

    public void drawI( int iani1, int iframe1, int iani2, int iframe2, float pol, float x, float y, float z)
    {
           this.iani = iani1;
           frame = frames[ animations[iani1].sFrame + iframe1 ];
	   gl.glPushMatrix();
	   gl.glTranslatef( x, y, z );
	   if ( scaleset )
	      gl.glScalef( scale.x, scale.y, scale.z );
           renderCmdI(iani1,iframe1,iani2,iframe2,pol);
	   gl.glPopMatrix();
    }
    
    public void draw()
    {
        render();
    }


    public void setRotate( float x, float y, float z )
    {
        rotateset = true;
        rotate.x = x;
        rotate.y = y;
        rotate.z = z;
    }

    public void setScale( float x, float y, float z )
    {
        scaleset = true;
        scale.x = x;
        scale.y = y;
        scale.z = z;
        size.x = Osize.x*scale.x;
        size.y = Osize.y*scale.y;
        size.z = Osize.z*scale.z;
    }

    public void setXYZScale( float x, float y, float z )
    {
        float fx, fy, fz;
        fx = x/Osize.x;
        fy = y/Osize.y;
        fz = z/Osize.z;
        setScale( fx, fy, fz );
    }

    public void setXSize( float f )
    {
        float factor = f/Osize.x;
        setScale( factor,factor,factor );
    }
    public void setYSize( float f )
    {
        float factor = f/Osize.y;
        setScale( factor,factor,factor );
    }
    public void setZSize( float f )
    {
        float factor = f/Osize.z;
        setScale( factor,factor,factor );
    }

    public Vec3 getOriginalSize()
    {
        return Osize;
    }

    public Vec3 getSize()
    {
        return size;
    }
    public float getXSize()
    {
        return size.x;
    }
    public float getYSize()
    {
        return size.y;
    }
    public float getZSize()
    {
        return size.z;
    }

    public Vec3 getMin()
    {
        return min;
    }
    public Vec3 getMax()
    {
        return min;
    }

   public void regenlist()
    {
    }
       
    public int getNumberOfFaces(int i)
    {
        return header.numTriangles;
    }
   
    public void render()
    {
        int i, j;
        Vec3 v, n;
        MD2_Triangle t;

        boolean useTexCoord;
/*
        useTexCoord = usrtextures[iani] >= 0;
        
        if ( useTexCoord ) {
            Textures.Enable();
            Textures.Select( usrtextures[iani]);
        }
        
        for (i = 0; i < header.numTriangles; i++) {
            t = triangles[i];
            gl.glBegin( GL.GL_TRIANGLES );
            for (j = 0; j < 3; j++ ) {
               v = frame.vertexes[t.vertexIndexes[j]].vertex;
               n = frame.vertexes[t.vertexIndexes[j]].normal;
               gl.glNormal3f(n.x,n.y,n.z);
               if ( useTexCoord ) {
                   gl.glTexCoord2f(
                     texCoords[t.textureIndexes[j]].s/(float)header.skinWidth,
                     1-texCoords[t.textureIndexes[j]].t/(float)header.skinHeight 
                   );
               }
               gl.glVertex3f(v.x,v.y,v.z);
            }
            gl.glEnd();
        }
        if ( useTexCoord ) {
            Textures.Disable();
        }        
*/
    }

    public void renderCmd()
    {
        int i, vidx;
        Vec3 v, n;
        float s, t;

        boolean useTexCoord;
/*
        useTexCoord = usrtextures[iani] >= 0;
        
        if ( useTexCoord ) {
            Textures.Enable();
            Textures.Select( usrtextures[iani]);
        }
    
        int val, count;
        
        i = 0;
        val = glCommandBuffer[i++];
        
        while ( val != 0 ) {
            if (val > 0)
	    {
		gl.glBegin (GL.GL_TRIANGLE_STRIP);
		count = val;
	    }
	    else
	    {
	        gl.glBegin (GL.GL_TRIANGLE_FAN);
		count = -val;
  	    }
            while ( count-- > 0 ) {
                s = Float.intBitsToFloat(glCommandBuffer[i++]);
                t = Float.intBitsToFloat(glCommandBuffer[i++]);
                vidx = glCommandBuffer[i++];
                v = frame.vertexes[vidx].vertex;
                n = frame.vertexes[vidx].normal;
                gl.glNormal3f(n.x,n.y,n.z);
                if ( useTexCoord ) {
                   gl.glTexCoord2f(s,1-t);
               }
               gl.glVertex3f(v.x,v.y,v.z);
            }
            gl.glEnd();
	    val = glCommandBuffer[i++];
        }
        if ( useTexCoord ) {
            Textures.Disable();
        }
        */        
    }

    public void renderCmdI(int iani1, int iframe1, int iani2, int iframe2,float pol)
    {
        int i, vidx;
        Vec3 v1, n1, v2, n2;
        float s, t;
        MD2_Frame frame2;
        float x1, y1, z1, x2, y2, z2;

        frame = frames[ animations[iani1].sFrame + iframe1 ];
        frame2 = frames[ animations[iani2].sFrame + iframe2 ];
        
     //   Log.d(this.getClass().getName(),"draw frame.." + iframe1 + " --- " + iframe2 );
   //   Log.d(this.getClass().getName(),"draw frame.. " + iani1 + " " + iframe1 + " " +
     //           iani2 + " " + iframe2 + " " + pol  );
        
        boolean useTexCoord;

        useTexCoord = usrtextures[iani] >= 0;
        
    
        int val, count;
        
        i = 0;
        val = glCommandBuffer[i++];
        
        int typeofFig;
        
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        if ( useTexCoord ) {
            Textures.Enable();
            Textures.Select( usrtextures[iani]);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, vtBuf );
        }
        	
        
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vBuf); 
        gl.glNormalPointer(GL10.GL_FLOAT, 0, vnBuf);
        
        while ( val != 0 ) {
            if (val > 0)
            {
            	typeofFig = GL10.GL_TRIANGLE_STRIP;
            	count = val;
            }
            else
            {
	    		typeofFig = GL10.GL_TRIANGLE_FAN;
	    		count = -val;
            }
            vBuf.position(0);
            vnBuf.position(0);
            vtBuf.position(0);
            
            int total = count;
       //     Log.d(this.getClass().getName(),"count: " + count );
            while ( count-- > 0 ) {
            	
                s = Float.intBitsToFloat(glCommandBuffer[i++]);
                t = Float.intBitsToFloat(glCommandBuffer[i++]);
                vidx = glCommandBuffer[i++];
                v1 = frame.vertexes[vidx].vertex;
                n1 = frame.vertexes[vidx].normal;
                v2 = frame2.vertexes[vidx].vertex;
                n2 = frame2.vertexes[vidx].normal;
                
             //   Log.d(this.getClass().getName(),"normal: " + n1.x + " --- " + n2.x );
                vnBuf.put( (1-pol)*n1.x + pol*n2.x );
                vnBuf.put(  (1-pol)*n1.y + pol*n2.y );
                vnBuf.put( (1-pol)*n1.z + pol*n2.z  );
                
                if ( useTexCoord ) {
            	    vtBuf.put(s);
            	    vtBuf.put(1-t);    
                }
                x1 = v1.x;
                y1 = v1.y;
                z1 = v1.z;
                x2 = v2.x;
                y2 = v2.y;
                z2 = v2.z;
                vBuf.put(x1 + pol * (x2 - x1) );
                vBuf.put(y1 + pol * (y2 - y1) );
                vBuf.put(z1 + pol * (z2 - z1) );
            }
            vBuf.position(0);
            vnBuf.position(0);
            vtBuf.position(0);
            gl.glDrawArrays(typeofFig, 0, total);
            val = glCommandBuffer[i++];
        }
        if ( useTexCoord ) {
           Textures.Disable();
           gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        }   
        
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
        
    }

    public int getNumberOfAnimations()
    {
        return numAnimations;
    }
    
    public int getAnimationFramesCount(int ani)
    {
        return animations[ani].numFrames;
    }
    
    public String getAnimationName(int ani)
    {
        return animations[ani].name;
    }
    
   public int getNumberOfObjects()
   {
       if ( loaded )
           return numAnimations;
       else
          return 0;
   }
   
   public void setObjectColor( int i, float r, float g, float b )
    {
        if ( i < getNumberOfObjects() )
        { 
            colors[i].x = r;
            colors[i].y = g;
            colors[i].z = b;
          //  regenlist();
        }
    }

    public Vec3 getObjectColor( int i )
    {
        return colors[i];
    }

    //no utilizado
    public void setObjectVisibility( int i, boolean v )
    {
        if ( i < getNumberOfObjects() )
        { 
            boolean x = visibility[i];
            if ( x != v ) {
                visibility[i] = v;
            //    regenlist();
            }
        }
    }
    
    //no utilizado
    public boolean getObjectVisibility( int i )
    {
        return visibility[i];
    }

    public void setObjectTexture( int i, int t )
    {
        if ( i < getNumberOfObjects() )
        { 
             usrtextures[i] = t;
          //   regenlist();
        }
    }
    
    public int getObjectTexture( int i )
    {
        return usrtextures[i];
    }

    public int     getType()
    {
        return M_MD2;
    }    
    
}


