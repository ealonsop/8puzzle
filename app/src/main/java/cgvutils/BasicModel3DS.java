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

package cgvutils;

import java.util.Vector;

import android.app.Activity;

// 3DS

public class BasicModel3DS
{
    protected Loader3DS loader = new Loader3DS();
    protected Vector<Material> materials = new Vector<Material>();
    protected Vector<Obj> objects = new Vector<Obj>();
    public static Activity activity;

    // Constructor
    public BasicModel3DS()
    {
    }

    // Load the model
    public boolean load(int file)
    {
    	loader.activity = activity;
        if (!loader.load(this, file))
            return false;

        return true;
    }

    public boolean load(int file, BP x, BP y, BP z )
    {
    	loader.activity = activity;    	
        return load(file);
    }

    // Add material
    public void addMaterial(Material mat)
    {
        materials.add(mat);
    }

    // Add an object
    public void addObject(Obj obj)
    {
        objects.add(obj);
    }

    // Get material
    public Material getMaterial(int index)
    {
        return materials.get(index);
    }

    // Get an object
    public Obj getObject(int index)
    {
        return objects.get(index);
    }

    // Get the number of objects
    public int getNumberOfObjects()
    {
        return objects.size();
    }

    // Get the number of materials
    public int getNumberOfMaterials()
    {
        return materials.size();
    }
}
