/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cgvutils;

/**
 *
 * @author eduardo
 */
public class MD2_AliasFrame {
  Vec3 scale = new Vec3();
  Vec3 translate = new Vec3();
  char name[] = new char[16];
  MD2_AliasVertex aliasvertex[];
}

/*
typedef struct
{
   float scale[3];
   float translate[3];
   char name[16];
   md2_alias_triangleVertex_t alias_vertices[1];
} md2_alias_frame_t;
*/