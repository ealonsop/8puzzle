/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cgvutils;

/**
 *
 * @author eduardo
 */
public class MD2_AliasVertex {
  int vertex[] = new int[3];//byte en el archivo
  int lightNormalIndex;//byte en el archivo
}

/*
typedef struct
{
   byte vertex[3];
   byte lightNormalIndex;
} md2_alias_triangleVertex_t;
*/