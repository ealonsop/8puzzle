/*
 * Copyright (c) 2006 Greg Rodgers All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *   
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *    
 * - Redistribution in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *   
 * The names of Greg Rodgers, Sun Microsystems, Inc. or the names of
 * contributors may not be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *    
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. GREG RODGERS,
 * SUN MICROSYSTEMS, INC. ("SUN"), AND SUN'S LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL GREG 
 * RODGERS, SUN, OR SUN'S LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT 
 * OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR
 * PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF GREG
 * RODGERS OR SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *   
 * You acknowledge that this software is not designed or intended for use
 * in the design, construction, operation or maintenance of any nuclear
 * facility.
 */

/* modificado por Eduardo Alonso 2011-2012
 * - punto base
 * - escala
 * - rotacion
 * - corrección de las texturas
 * - corrección del color
 * - cambio del color de los objetos
 */

// 3DS 

package cgvutils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;
import android.app.Activity;
import android.util.Log;


public class Model3DS extends BasicModel3DS implements Model3D
{


    private Vec3 colors[];
    private boolean visibility[];
    private int usrtextures[];

    private int texturas[];
//    private TextureCoords[] textureCoords;
    private int compiledList;
    private boolean loaded = false;
    private boolean scaleset = false;
    private boolean rotateset = false;
	Obj tempObj;    

    float v[], vt[], vn[];
    
    ShortBuffer ivBuf;
    

    public static GL10 gl;


  // constructor


    public static void Init(GL10 xgl, Activity actv)
    {
       gl = xgl;
       activity = actv;
    }

    private Vec3 scale, rotate, size, Osize, min, max;

    // Constructor
    public Model3DS()
    {
		ByteBuffer vbb = ByteBuffer.allocateDirect(3*2);	
		vbb.order(ByteOrder.nativeOrder());
        ivBuf = vbb.asShortBuffer();     
       
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
        int i, j;
        for(i = 0; i < objects.size(); i++)
        {
	    Obj pObject = objects.get(i);
		for(j = 0; j < pObject.numOfVerts; j++)
		{
			int index = j;
			if ( primero == 1)
			{
				min.set(pObject.verts[ index ]);
				max.set(pObject.verts[ index ]);
				primero = 0;
			}
			else
			{
				if ( min.x > pObject.verts[ index ].x )
					min.x = pObject.verts[ index ].x;
				if ( max.x < pObject.verts[ index ].x )
					max.x = pObject.verts[ index ].x;
				if ( min.y > pObject.verts[ index ].y )
					min.y = pObject.verts[ index ].y;
				if ( max.y < pObject.verts[ index ].y )
					max.y = pObject.verts[ index ].y;
				if ( max.z < pObject.verts[ index ].z )
					max.z = pObject.verts[ index ].z;
				if ( min.z > pObject.verts[ index ].z )
					min.z = pObject.verts[ index ].z;
			}
		}
      }
      size.x = max.x - min.x;
      size.y = max.y - min.y;
      size.z = max.z - min.z;
      Osize.set(size);
    }

    private void moveObjects( float dx, float dy, float dz )
    {
        int i, j;
        for(i = 0; i < objects.size(); i++)
        {
         Obj pObject = objects.get(i);
         for(j = 0; j < pObject.numOfVerts; j++)
         {
            pObject.verts[ j ].x += dx;
            pObject.verts[ j ].y += dy;
            pObject.verts[ j ].z += dz;
         }
        }

        min.x += dx;
        max.x += dx;
        min.y += dy;
        max.y += dy;
        min.z += dz;
        max.z += dz;
    }

    private void readTextures()
    {
        int numMaterials = materials.size();
        int i;

        if ( numMaterials == 0 )
            return;

      /*
        texture = new Texture[numMaterials];
        for (i=0; i<numMaterials; i++) {
            loadTexture(materials.get(i).strFile, i);
            materials.get(i).texureId = i;
        }
        */

        texturas = new int[numMaterials];
        for (i=0; i<numMaterials; i++) {
         //   if ( ! materials.get(i).strFile.isEmpty() ) {
              // Textures.LoadTexture(materials.get(i).strFile, i, texturas);
            //loadTexture(materials.get(i).strFile, i);
               materials.get(i).texureId = i;
       //     }
        }
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
        if (!super.load(file))
            return false;

        loaded = true;

        calcDims();

        float dx, dy, dz;

        dx = -min.x - size.x * x;
        dy = -min.y - size.y * y;
        dz = -min.z - size.z * z;

        moveObjects(dx,dy,dz);

        readTextures();
        
        int i, j;

        if ( getNumberOfObjects() > 0 ) {
           colors = new Vec3[getNumberOfObjects()];
           visibility = new boolean[getNumberOfObjects()];
           usrtextures = new int[getNumberOfObjects()];
           for (  i=0; i<getNumberOfObjects();i++ )
           {
        	   tempObj = objects.get(i);
               colors[i] = new Vec3(1,1,1);
                if ( materials.size() > 0 &&  tempObj.materialID >= 0) {
                   byte pColor[] = materials.get(tempObj.materialID).color;
                   colors[i].x = (pColor[0] & 255)/255f;
                   colors[i].y = (pColor[1] & 255)/255f;
                   colors[i].z = (pColor[2] & 255)/255f;               
                }
               visibility[i] = true;
               usrtextures[i] = -1;
           }
        }
        
        ByteBuffer vbb;
       	int nv;
       	
        for ( i = 0; i < getNumberOfObjects(); i++ ) {
        	tempObj = objects.get(i);
         	nv = tempObj.numOfVerts;
    		Log.d(this.getClass().getName(),"NV="+nv);
        	vbb = ByteBuffer.allocateDirect(nv*3*4);	
            vbb.order(ByteOrder.nativeOrder());
            tempObj.vBuf = vbb.asFloatBuffer();
            tempObj.vBuf.position(0);
            for (j=0; j < nv; j++ ) {
            	tempObj.vBuf.put(tempObj.verts[j].x);
            	tempObj.vBuf.put(tempObj.verts[j].y);
            	tempObj.vBuf.put(tempObj.verts[j].z);
            }
            tempObj.vBuf.position(0); 
            tempObj.verts = null;
            
        	nv = tempObj.numOfVerts;
    		Log.d(this.getClass().getName(),"NN="+nv);
        	vbb = ByteBuffer.allocateDirect(nv*3*4);	
            vbb.order(ByteOrder.nativeOrder());
            tempObj.vnBuf = vbb.asFloatBuffer();
            tempObj.vnBuf.position(0);
            for (j=0; j < nv; j++ ) {
            	tempObj.vnBuf.put(tempObj.normals[j].x);
            	tempObj.vnBuf.put(tempObj.normals[j].y);
            	tempObj.vnBuf.put(tempObj.normals[j].z);
            }
            tempObj.vnBuf.position(0);
            tempObj.normals = null;
     
        	nv = tempObj.numTexVertex;
    		Log.d(this.getClass().getName(),"NT="+nv);
        	vbb = ByteBuffer.allocateDirect(nv*2*4);	
            vbb.order(ByteOrder.nativeOrder());
            tempObj.vtBuf = vbb.asFloatBuffer();
            tempObj.vtBuf.position(0);
            for (j=0; j < nv; j++ ) {
            	tempObj.vtBuf.put(tempObj.texVerts[j].x);
            	tempObj.vtBuf.put(tempObj.texVerts[j].y);
            }
            tempObj.vtBuf.position(0);
            tempObj.texVerts = null;
            
            
            
        	
        }


      //  compiledList = gl.glGenLists(1);

   //     regenlist();
        
        return loaded;
    }
    
    public void render()
    {
       // gl.glCallList(compiledList);
        genList();
    }

    public void draw( float x, float y, float z, float r )
    {
	   gl.glPushMatrix();
	   gl.glTranslatef( x, y, z );
	   if ( rotateset )
	      gl.glRotatef( r, rotate.x, rotate.y, rotate.z );
	   if ( scaleset )
	      gl.glScalef( scale.x, scale.y, scale.z );
           render();
	   gl.glPopMatrix();
    }

    public void draw( float x, float y, float z )
    {
	   gl.glPushMatrix();
	   gl.glTranslatef( x, y, z );
	   if ( scaleset )
	      gl.glScalef( scale.x, scale.y, scale.z );
           render();
	   gl.glPopMatrix();
    }

    public void draw( int iani, int iframe, float x, float y, float z)
    {
        draw(x,y,z);
    }
    
    public void drawI( int iani1, int iframe1, int iani2, int iframe2, float pol, float x, float y, float z)
    {
        draw(x,y,z);
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

/*
 * eliminado por ealonso
 * porque no carga correctamente las texturas
    private void loadTexture(String strFile, int id)
    {
        File file = new File(strFile);
        try {
            texture[id] = TextureIO.newTexture(file, true);
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }
    }
*/
    public void regenlist()
    {
      //  gl.glNewList(compiledList, GL.GL_COMPILE);
            genList();
      //  gl.glEndList();
    }

    public int getNumberOfFaces(int i)
    {
        return objects.get(i).numOfFaces;
    }
    
    private void genList()
    {
//        TextureCoords coords;
    	int index, i,  j, whichVertex;
        
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        //
        
       // gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vBuf); 
       // gl.glNormalPointer(GL10.GL_FLOAT, 0, vnBuf);
        
        for (i=0; i<objects.size(); i++) {
            if ( !visibility[i] ) continue;
            tempObj = objects.get(i);
            
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, tempObj.vBuf); 
            gl.glNormalPointer(GL10.GL_FLOAT, 0, tempObj.vnBuf);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, tempObj.vtBuf );

            if(tempObj.hasTexture) {
                gl.glColor4f(1,1,1,1);
                Textures.Enable();
                if ( usrtextures[i] >= 0 )
                    Textures.Select(usrtextures[i] );
      //          else
        //            Textures.Select(tempObj.materialID, texturas);
            }
            else
            {
			// Turn off texture mapping and turn on color
//			    gl.glDisable(GL.GL_TEXTURE_2D);
                Textures.Disable();
			// Reset the color to normal again
                gl.glColor4f(colors[i].x,colors[i].y,colors[i].z,1);
            }
                for (j=0; j<tempObj.numOfFaces; j++) {
                	//vBuf.position(0);
                	//vnBuf.position(0);
                	ivBuf.position(0);
                	for (whichVertex=0; whichVertex<3; whichVertex++) {
                        index = tempObj.faces[j].vertIndex[whichVertex];
                        ivBuf.put((short)index);
                        
                      /*  
                       // gl.glNormal3f(tempObj.normals[index].x, tempObj.normals[index].y, tempObj.normals[index].z);
                        if (tempObj.hasTexture) {
                            if (tempObj.texVerts != null) {
                       //         gl.glTexCoord2f(tempObj.texVerts[index].x, tempObj.texVerts[index].y);
                            }
                        }
                        else {
                            if (materials.size() > 0 &&  tempObj.materialID >= 0) {
                                byte pColor[] = materials.get(tempObj.materialID).color;
                                float cr, cg, cb;
                                cr = (pColor[0] & 255)/255f;
                                cg = (pColor[1] & 255)/255f;
                                cb = (pColor[2] & 255)/255f;
                                gl.glColor4f(cr,cg,cb,1);
                            }
                        }
                        
                        vBuf.put(tempObj.verts[index].x);
                        vBuf.put(tempObj.verts[index].y);
                        vBuf.put(tempObj.verts[index].z);
                        vnBuf.put(tempObj.normals[index].x);
                        vnBuf.put(tempObj.normals[index].y);
                        vnBuf.put(tempObj.normals[index].z);*/
                        
                        
                /*        v[whichVertex*3] = tempObj.verts[index].x;
                        v[whichVertex*3+1] = tempObj.verts[index].y;
                        v[whichVertex*3+2] = tempObj.verts[index].z;
                        vn[whichVertex*3] = tempObj.normals[index].x;
                        vn[whichVertex*3+1] = tempObj.normals[index].y;
                        vn[whichVertex*3+2] = tempObj.normals[index].z;*/
                    }
                	tempObj.vBuf.position(0);
                  //  vBuf.put(v);
              //      vBuf.position(0);
                	ivBuf.position(0);
                	tempObj.vtBuf.position(0);
                  //  vnBuf.position(0);
                 //   vnBuf.put(vn);
                	tempObj.vnBuf.position(0); 
                    
                    //gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
                	gl.glDrawElements(GL10.GL_TRIANGLES, 3, GL10.GL_UNSIGNED_SHORT, ivBuf);
                    //
                	
                }
       
           
            if (tempObj.hasTexture)
                Textures.Disable();
                //texture[tempObj.materialID].disable();
        }
        
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);        
    }
    
    public void setObjectColor( int i, float r, float g, float b )
    {
        if ( i < getNumberOfObjects() )
        { 
            Obj tempObj = objects.get(i);
            tempObj.hasTexture = false;
            tempObj.materialID = -1;
            colors[i].x = r;
            colors[i].y = g;
            colors[i].z = b;
           // regenlist();
        }
    }

    public Vec3 getObjectColor( int i )
    {
        return colors[i];
    }

    public void setObjectVisibility( int i, boolean v )
    {
        if ( i < getNumberOfObjects() )
        { 
            boolean x = visibility[i];
            if ( x != v ) {
                visibility[i] = v;
              //  regenlist();
            }
        }
    }
    
    public boolean getObjectVisibility( int i )
    {
        return visibility[i];
    }

    public void setObjectTexture( int i, int t )
    {
        if ( i < getNumberOfObjects() )
        { 
             usrtextures[i] = t;
            // regenlist();
        }
    }
    
    public int getObjectTexture( int i )
    {
        return usrtextures[i];
    }

    public int getAnimationFramesCount(int ani)
    {
        return 0;
    }
    
    public String getAnimationName(int ani)
    {
        return "main3DS";
    }

    public int getNumberOfAnimations()
    {
        return 0;
    }
    
    public int     getType()
    {
        return M_3DS;
    }
    
}
