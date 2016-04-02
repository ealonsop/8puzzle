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

/* modificado por Eduardo Alonso 2011
 * - xzangle
 * - yangle
 * - distance
 * - cross
 * - normalize
 */

package cgvutils;

public class Vec3
{
    public float x,y,z;

    public Vec3()
    {
    }

    public Vec3(float _x, float _y, float _z)
    {
        x = _x;
        y = _y;
        z = _z;
    }

    public Vec3(Vec3 v)
    {
        x = v.x;
        y = v.y;
        z = v.z;
    }
    public void set(Vec3 v)
    {
        x = v.x;
        y = v.y;
        z = v.z;
    }

    public void set(float vx, float vy, float vz)
    {
        x = vx;
        y = vy;
        z = vz;
    }

    //siguientes métodos agregados por EAlonso

    public double xzangle( Vec3 v )
    {
        double dx, ary, dz, m;

        dx = v.x - x;
        dz = v.z - z;

        if ( dx != 0 ) {
            m = -dz/dx;
            ary = Math.toDegrees(Math.atan(m));
            if ( dx < 0 )
               ary += 180;
        }
        else {
            if ( dz < 0 )
                ary = 90;
            else
                ary = -90;
        }
        return (float)ary;
    }

    public double distance( Vec3 v )
    {
        float dx, dy, dz;
        dx = x-v.x;
        dy = y-v.y;
        dz = z-v.z;
        return Math.sqrt( dx*dx + dy*dy + dz*dz );
    }

    public double yangle( Vec3 v )
    {
        double dy, arx, d;

        dy = v.y - y;
        d = distance(v);
        if ( d > 0 )
           arx = Math.toDegrees(Math.asin(dy/d));
        else
           arx = 0;
        return arx;
    }

    public void cross ( Vec3 v1, Vec3 v2 )
    {
        x = v1.y*v2.z - v1.z*v2.y;
        y = v1.z*v2.x - v1.x*v2.z;
        z = v1.x*v2.y - v1.y*v2.x;
    }

    public void normalize()
    {
       float mag = (float)Math.sqrt(x*x + y*y + z*z);
       x /= mag;
       y /= mag;
       z /= mag;
    }

    public String toString()
    {
        return "["+x+","+y+","+z+"]";
    }

}