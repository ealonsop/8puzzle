/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cgvutils;

/**
 *
 * @author ealonso
 */
public interface Model3D {
    
    public static final int M_3DS = 1;
    public static final int M_OBJ = 2;
    public static final int M_MD2 = 3;
    
    public int     getType();
    
    public boolean isLoaded();
    public boolean load(int id);
    public boolean load(int id, int x, int y, int z);
    public boolean load(int id, float x, float y, float z);
    public void regenlist();
    public void render();
    public void draw(float x, float y, float z, float r);
    public void draw(float x, float y, float z);
    public void setRotate(float x, float y, float z);
    public void setScale(float x, float y, float z);
    public void setXYZScale(float x, float y, float z);
    public void setXSize(float f);
    public void setYSize(float f);
    public void setZSize(float f);
    public Vec3 getOriginalSize();
    public Vec3 getSize();
    public float getXSize();
    public float getYSize();
    public float getZSize();
    public Vec3 getMin();
    public Vec3 getMax();
    public int getNumberOfObjects();
    public int getNumberOfFaces(int i);
    
    public void setObjectColor(int i, float r, float g, float b);
    public Vec3 getObjectColor(int i);
    public void setObjectVisibility(int i, boolean v);
    public boolean getObjectVisibility(int i);
    public void setObjectTexture(int i, int t);
    public int getObjectTexture(int i);
    
    public void draw(int iani, int iframe, float x, float y, float z);
    public void drawI(int iani1, int iframe1, int iani2, int iframe2, float pol, float x, float y, float z);
    public int getNumberOfAnimations();
    public int getAnimationFramesCount(int ani);
    public String getAnimationName(int ani);
    
    
    
    
}
