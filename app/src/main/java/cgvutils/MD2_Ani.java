/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cgvutils;


/**
 *                  
 * @author eduardo
 */
public class MD2_Ani {

	public Model3D obj;

    public int aniI;

	public int fraI;

	public int aniE;

	public  int aniC;

	public int aniD;
	public int laniI, lfraI;
	public float interpol, deltainterpol;

    public MD2_Ani(Model3D obj)
    {
        this.obj = obj;
        reset();
    }

    public void reset()
    {
        laniI = lfraI = 0;
        aniI = 0;
        fraI = 0;
        aniE = 0;
        aniC = 0;
        aniD = 0;
        interpol = 0;
        deltainterpol = 0.2f;
    }

    public void rewind()
    {
       laniI = aniI;
       lfraI = fraI;
       aniC = aniI = aniE;
       fraI = 1;
       interpol = 0;
    }
    public void animate()
    { 

      //  Log.d(this.getClass().getName(),"draw frame.. " + laniI + " " + lfraI + " " +
       //        aniI + " " + fraI + " " + interpol  );
        
        if ( obj.getAnimationFramesCount(aniI) > 0 )
           obj.drawI(laniI, lfraI, aniI, fraI, interpol, 0,0,0);
        else
           obj.draw(aniI, 0,0,0,0);
        interpol += deltainterpol;
        if (interpol >= 1) {
            interpol = 0;
            laniI = aniI;
            lfraI = fraI;
            fraI = (fraI+1) % obj.getAnimationFramesCount(aniI);
            if ( fraI == 0 )
                aniI = aniC = aniD;
        }
    }
    public void drawlastframe()
    {
        if ( obj.getAnimationFramesCount(aniI) > 0 )
           obj.drawI(laniI, lfraI, aniI, fraI, interpol, 0,0,0);
        else
           obj.draw(aniI, fraI,0,0,0);
    }


}
