/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cgvutils;

/**
 *
 * @author ealonso
 */
public class MD2_Triangle {
    short vertexIndexes[] = new short[3];
    short textureIndexes[] = new short[3];
    public String toString()
    {
        return "{"+vertexIndexes[0] + "," + vertexIndexes[1] + "," +
                vertexIndexes[2] + "}";
    }
}

/*
typedef struct
{
   short vertexIndices[3];
   short textureIndices[3];
} md2_triangle_t;
 * */
 