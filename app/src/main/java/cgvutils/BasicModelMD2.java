/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cgvutils;

import java.io.*;
import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;

/**
 *
 * @author ealonso
 */
public class BasicModelMD2 {
    
    static final int MAX_TRIANGLES = 4096;
    static final int MAX_VERTICES = 2048;
    static final int MAX_TEXCOORDS = 2048;
    static final int MAX_FRAMES = 512;
    static final int MAX_SKINS = 32;
    static final int MAX_FRAMESIZE = MAX_VERTICES * 4 + 128;
    
    public MD2_Header      header;
    public MD2_Skin        skins[];
    public MD2_TexCoord    texCoords[];
    public MD2_Triangle    triangles[];
    public MD2_Frame       frames[];
    public int             glCommandBuffer[];

    public int             numAnimations;
    public MD2_Animation   animations[];
    
    public static Activity activity;

// File reader
    private File file;
    private boolean loaded = false;
    private InputStream fileInputStream;
    private DataInputStream dataInputStream;
    
    public int loadanims[];
    boolean loadframes[];
    
    
    public BasicModelMD2()
    {
        header = new MD2_Header();
        skins = null;
        texCoords = null;
        triangles = null;
        frames = null;
        glCommandBuffer = null;
    }
    
    public boolean Load( int fileName )
    {
        int i, j;
        
        fileInputStream = activity.getResources().openRawResource(fileName);


        dataInputStream = new DataInputStream(fileInputStream);


        try {
            header.magic = ReadInt(); 
            header.version = ReadInt(); 
            header.skinWidth = ReadInt(); 
            header.skinHeight = ReadInt(); 
            header.frameSize = ReadInt(); 
            header.numSkins = ReadInt(); 
            header.numVertices = ReadInt(); 
            header.numTexCoords = ReadInt(); 
            header.numTriangles = ReadInt(); 
            header.numGlCommands = ReadInt(); 
            header.numFrames = ReadInt(); 
            header.offsetSkins = ReadInt(); 
            header.offsetTexCoords = ReadInt(); 
            header.offsetTriangles = ReadInt(); 
            header.offsetFrames = ReadInt(); 
            header.offsetGlCommands = ReadInt(); 
            header.offsetEnd = ReadInt();         
        } catch (IOException e) {
            System.err.println("Error reading header: "+fileName);
            return false;
        }
        
        if ( header.magic != 844121161 )
            return false;
        
        
//	System.out.println("Reading skins...");            
        if ( header.numSkins > 0 ) {
            skins = new MD2_Skin[header.numSkins];
            for (i = 0; i < header.numSkins; i++ ) {
                skins[i] = new MD2_Skin();
                readSkin(skins[i]);
//                System.out.println("Skin " + i + " = " + skins[i].skin );
            }
        }
        
   //   Log.d(this.getClass().getName(),"reading tex coords.." );
//	System.out.println("Reading tex coords...");        
        if ( header.numTexCoords > 0 ) {
            texCoords = new MD2_TexCoord[header.numTexCoords];
            for (i = 0; i < header.numTexCoords; i++ ) {
                texCoords[i] = new MD2_TexCoord();
                readTexCoord(texCoords[i]);
               // System.out.println("TexCoord: " + i + " = " + texCoords[i].s + " , " + texCoords[i].t );
            }
        }
            
     //   Log.d(this.getClass().getName(),"reading triangles.." );
//	System.out.println("Reading triangles...");        
        if ( header.numTriangles > 0 ) {
            triangles = new MD2_Triangle[header.numTriangles];
            for (i = 0; i < header.numTriangles; i++ ) {
                triangles[i] = new MD2_Triangle();
                readTriangle(triangles[i]);
            }
        }
        
     //   Log.d(this.getClass().getName(),"reading frames.." );
//	System.out.println("Reading frames...");
        if ( header.numFrames > 0 ) {
            MD2_AliasFrame frame;
            frame = new MD2_AliasFrame();
            frame.aliasvertex = new MD2_AliasVertex[header.numVertices];
            for (j=0; j < header.numVertices; j++ )
                frame.aliasvertex[j] = new MD2_AliasVertex();

            frames = new MD2_Frame[header.numFrames];

            for ( i=0; i < header.numFrames; i++  ) {
                frames[i] = new MD2_Frame();
                frames[i].vertexes = new MD2_TriangleVertex[header.numVertices];
                readAliasFrame(frame);
                frames[i].name = charToString(frame.name);
     //           Log.d(this.getClass().getName(),"reading frames: "+frames[i].name);
  //              System.out.println("Frame "+ i + ": " + frames[i].name);
                for (j=0; j < header.numVertices; j++ ) {
                    frames[i].vertexes[j] = new MD2_TriangleVertex();
                    frames[i].vertexes[j].vertex.x = (float) (frame.aliasvertex[j].vertex[0]) * frame.scale.x + frame.translate.x;
                    frames[i].vertexes[j].vertex.z = -1* ((float) (frame.aliasvertex[j].vertex[1]) * frame.scale.y + frame.translate.y);
                    frames[i].vertexes[j].vertex.y = (float) (frame.aliasvertex[j].vertex[2]) * frame.scale.z + frame.translate.z;
                }
            }
            calcAnimations();
        }
        else
            numAnimations = 0;
        
        MD2_Animation ani;
        
        loadframes = new boolean[header.numFrames];
        if ( loadanims != null ) {
        	for ( i = 0; i < header.numFrames; i++ )
        		loadframes[i] = false;
        	for (i=0; i < loadanims.length; i++ ) {
        		ani = animations[loadanims[i]];
        		for (j = ani.sFrame; j <= ani.eFrame; j++ )
        			loadframes[j] = true;
        	}
        }
        else
        	for ( i = 0; i < header.numFrames; i++ )
        		loadframes[i] = true;
        
        /*
        System.out.println("magic: " + header.magic);
	System.out.println("version: " + header.version);
	System.out.println("skinWidth: " + header.skinWidth);
	System.out.println("skinHeight: " + header.skinHeight);
	System.out.println("frameSize: " + header.frameSize);
	System.out.println("numSkins: " + header.numSkins);
	System.out.println("numVertices: " + header.numVertices);
	System.out.println("numTexCoords: " + header.numTexCoords);
	System.out.println("numTriangles: " + header.numTriangles);
	System.out.println("numGlCommands: " + header.numGlCommands);
	System.out.println("numFrames: " + header.numFrames);
	System.out.println("offsetSkins: " + header.offsetSkins);
	System.out.println("offsetTexCoords: " + header.offsetTexCoords);
	System.out.println("offsetTriangles: " + header.offsetTriangles);
	System.out.println("offsetFrames: " + header.offsetFrames);
	System.out.println("offsetGlCommands: " + header.offsetGlCommands);
	System.out.println("offsetEnd: " + header.offsetEnd);
        
	System.out.println("numAnimations: " + numAnimations );*/
   /*     
        for (i=0; i < numAnimations; i++)
            System.out.println(i + " = " + animations[i].name +
                    " " + animations[i].numFrames +
                    " " + animations[i].sFrame +
                    " " + animations[i].eFrame
                    );*/
//	System.out.println("Reading gl commands...");
   //     Log.d(this.getClass().getName(),"reading commands...");
        if ( header.numGlCommands > 0 ) {
            glCommandBuffer = new int[header.numGlCommands];
            try {
                for (i=0; i < header.numGlCommands; i++ ) {
                    glCommandBuffer[i] = ReadInt();
//                    System.out.println(glCommandBuffer[i]);
                }
            }
            catch (IOException e) {
                System.err.println("Error reading commands");
                return false;
            }
        }

        /*
        int val, count, vidx;
        float s, t;
        i = 0;
        val = glCommandBuffer[i++];
        
        while ( val != 0 ) {
            if (val > 0)
	    {
		count = val;
	    }
	    else
	    {
		count = -val;
  	    }
            System.out.println("val " + val + " count " + count);
            while ( count > 0 ) {
                s = Float.intBitsToFloat(glCommandBuffer[i++]);
                t = Float.intBitsToFloat(glCommandBuffer[i++]);
                vidx = glCommandBuffer[i++];
                System.out.println("s = " + s + " t = " + t + " idx " + vidx);
                count--;
            }
	    val = glCommandBuffer[i++];
        }
*/
        
        
//	System.out.println("Computing normals...");
     //   Log.d(this.getClass().getName(),"computing normals...");
        computeNormals();
        
        loadanims = null;
        loadframes = null;
        triangles = null;
        
        Log.d(this.getClass().getName(),"modelo cargado...");
        return true;
    }

    public int getAnimationsCount()
    {
        return numAnimations;
    }

    public MD2_Animation getAnimationInfo(int ani)
    {
        return animations[ani];
    }
    
    private String removeDigits(String name)
    {
        int i, j;
        char c;
        i = name.length()-1;
        j = 0;
//        System.out.println("removing from: " + name);
        while ( i >= 0 && (c = name.charAt(i)) >= '0' && c <= '9' && j < 2 ) {
            i--;
            j++;
        }
        return name.substring(0, i+1);
    }

    private void computeNormals()
    {
        
        int i, j;
        Vec3 vVector1 = new Vec3();
        Vec3 vVector2 = new Vec3();
        Vec3 vPoly[] = new Vec3[3];
        MD2_Triangle t;
        Vec3 normals[] = new Vec3[header.numTriangles];
        Vec3 tempNormals[] = new Vec3[header.numTriangles];
        Vec3 vSum, vZero;
        float nx, ny, nz;
        int shared, index;
        vSum = new Vec3();
        float mag;
        MD2_Frame frame;
        
        vZero = new Vec3(0,0,0);
        
        for (i=0; i<header.numTriangles; i++)
        {
        	normals[i] = new Vec3();
        	tempNormals[i] = new Vec3();
        }

        for (index=0; index<header.numFrames; index++) {
        	
        	if ( loadframes[index] == false )
        	{
        		frames[index] = null;
        		continue;
        	}

          // Log.d(this.getClass().getName(),"normals frame= "+index);

            frame = frames[index];

            for ( i=0; i<header.numTriangles; i++) {
                t = triangles[i];
                for(j=0; j < 3; j++ )
                   vPoly[j] = frame.vertexes[t.vertexIndexes[j]].vertex;


                vVector1.set(vPoly[0]);
                // Subtract vPoly[2] from vVector1
                vVector1.x -= vPoly[2].x;
                vVector1.y -= vPoly[2].y;
                vVector1.z -= vPoly[2].z;

                vVector2.set(vPoly[2]);
                // Subtract vPoly[1] from vVector2
                vVector2.x -= vPoly[1].x;
                vVector2.y -= vPoly[1].y;
                vVector2.z -= vPoly[1].z;

                normals[i].set(vVector1);

                // Compute the cross product
                normals[i].x = vVector1.y*vVector2.z - vVector1.z*vVector2.y;
                normals[i].y = vVector1.z*vVector2.x - vVector1.x*vVector2.z;
                normals[i].z = vVector1.x*vVector2.y - vVector1.y*vVector2.x;

                tempNormals[i].set(normals[i]);


                nx = normals[i].x;
                ny = normals[i].y;
                nz = normals[i].z;
                // Normalize
                mag = (float)Math.sqrt(nx*nx + ny*ny + nz*nz);
                normals[i].x /= mag;
                normals[i].y /= mag;
                normals[i].z /= mag;
            }

            vSum.set(vZero);
            shared=0;

            for (i=0; i<header.numVertices; i++) {
                for (j=0; j<header.numTriangles; j++)  {
                    t = triangles[j];
                    if (t.vertexIndexes[0] == i ||
                        t.vertexIndexes[1] == i ||
                        t.vertexIndexes[2] == i)
                    {
                        // Add the vectors vSum and tempNormals
                        vSum.x += tempNormals[j].x;
                        vSum.y += tempNormals[j].y;
                        vSum.z += tempNormals[j].z;
                        shared++;
                    }
                }

                // Divide the vector vSum by -shared
                vSum.x /= -shared;
                vSum.y /= -shared;
                vSum.z /= -shared;

                frame.vertexes[i].normal.set(vSum);

                // Normalize
                nx = frame.vertexes[i].normal.x;
                ny = frame.vertexes[i].normal.y;
                nz = frame.vertexes[i].normal.z;
                mag = (float)Math.sqrt(nx*nx + ny*ny + nz*nz );
                                             
                frame.vertexes[i].normal.x /= mag;
                frame.vertexes[i].normal.y /= mag;
                frame.vertexes[i].normal.z /= mag;

                vSum.set(vZero);
                shared = 0;
            }
        }
    }


    private void calcAnimations()
    {
        int sFrame, i;
        String name, nxtname;
        ArrayList <MD2_Animation> anims;

        if ( header.numFrames == 1 ) {
            numAnimations = 1;
            animations = new MD2_Animation[1];
            animations[0] = new MD2_Animation();
            animations[0].name = "main";
            animations[0].numFrames = 1;
            animations[0].sFrame = 0;
            animations[0].eFrame = 0;
            return;
        }

        anims = new ArrayList<MD2_Animation>();
        name = removeDigits(frames[0].name);
        sFrame = 0;
        for ( i = 1; i <= header.numFrames; i++ ) {
            if ( i == header.numFrames )
                nxtname = "";
            else
                nxtname = frames[i].name;
            if ( ! nxtname.startsWith(name) ) {
                MD2_Animation ani;
                ani = new MD2_Animation();
                ani.name = name;
                ani.sFrame = sFrame;
                ani.eFrame = i-1;
                ani.numFrames = ani.eFrame-ani.sFrame+1;
                anims.add(ani);
                sFrame = i;
                name = removeDigits(nxtname);
            }
        }
        numAnimations = anims.size();
        animations = new MD2_Animation[numAnimations];
        for (i=0; i<numAnimations; i++)
            animations[i] = anims.get(i);
        
        anims = null;
        
    }

    private String charToString( char c[] )
    {
        String r = "";
        for (int i=0; i < c.length && c[i] != 0; i++ )
            r = r + Character.toString(c[i]);
        return r;
    }
    
    private void readSkin( MD2_Skin skin )
    {
        char buf[] = new char[64];
        try {
        for (int i=0; i < buf.length; i++ )
            buf[i] = ReadChar();
        } catch (IOException e) {
            System.err.println("Error reading skins ");
        }
        skin.skin = charToString(buf);
    }

    private void readTexCoord( MD2_TexCoord texcoord )
    {
        try {
            texcoord.s = (short)ReadShort();
            texcoord.t = (short)ReadShort();
        } catch (IOException e) {
            System.err.println("Error reading texcoord ");
        }
    }

    private void readTriangle( MD2_Triangle triangle )
    {
        int i;
        try {
            for (i=0; i < 3; i++ )
              triangle.vertexIndexes[i] = (short)ReadShort();
            for (i=0; i < 3; i++ )
              triangle.textureIndexes[i] = (short)ReadShort();
        } catch (IOException e) {
            System.err.println("Error reading triangle ");
        }
    }

    private void readAliasFrame( MD2_AliasFrame frame )
    {
        int i;
        try {
            frame.scale.x = ReadFloat();
            frame.scale.y = ReadFloat();
            frame.scale.z = ReadFloat();
            frame.translate.x = ReadFloat();
            frame.translate.y = ReadFloat();
            frame.translate.z = ReadFloat();
            for (i=0; i < frame.name.length; i++ )
              frame.name[i] = ReadChar();
            for (i=0; i < header.numVertices;i++)
                readAliasVertex(frame.aliasvertex[i]);
        } catch (IOException e) {
            System.err.println("Error reading frame ");
        }

    }

    private void readAliasVertex( MD2_AliasVertex avertex )
    {
        try {
            avertex.vertex[0] = ReadByte();
            avertex.vertex[1] = ReadByte();
            avertex.vertex[2] = ReadByte();
            avertex.lightNormalIndex = ReadByte();            
        } catch (IOException e) {
            System.err.println("Error reading alias vertex");
        }
   }


    private char ReadChar()  throws IOException {
        return (char)dataInputStream.read();
    }

    private int ReadByte() throws IOException {
        return dataInputStream.read() & 0xff;
    }

    private int ReadShort() throws IOException 
    {
        int b1 = ReadByte();
        int b2 = ReadByte() << 8;
        return b1 | b2;
    }

    private float ReadFloat() throws IOException
    {
        return Float.intBitsToFloat(ReadInt());
    }

    private int ReadInt() throws IOException
    {
        DataInputStream in = dataInputStream;
        return (int)(in.read() + (in.read() << 8) + (in.read() << 16) + (in.read() << 24));
    }
    
}
